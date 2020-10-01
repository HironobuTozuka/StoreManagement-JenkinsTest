package inc.roms.rcs.service.inventory;

import inc.roms.rcs.exception.ResourceNotFoundException;
import inc.roms.rcs.service.inventory.domain.model.*;
import inc.roms.rcs.service.inventory.domain.repository.SkuRepository;
import inc.roms.rcs.service.inventory.domain.repository.SlotDimensionsRepository;
import inc.roms.rcs.service.inventory.domain.repository.ToteRepository;
import inc.roms.rcs.service.inventory.exception.ToteNotFoundException;
import inc.roms.rcs.service.inventory.request.CreateToteRequest;
import inc.roms.rcs.service.inventory.request.ToteListRequest;
import inc.roms.rcs.service.inventory.response.ToteDetails;
import inc.roms.rcs.service.inventory.response.ToteListResponse;
import inc.roms.rcs.service.inventory.response.ToteTechnicalData;
import inc.roms.rcs.service.machineoperator.ZoneService;
import inc.roms.rcs.service.machineoperator.model.ZoneState;
import inc.roms.rcs.service.omnichannel.OmniChannelService;
import inc.roms.rcs.service.task.domain.TaskBundleService;
import inc.roms.rcs.vo.common.ResponseCode;
import inc.roms.rcs.vo.common.TemperatureRegime;
import inc.roms.rcs.vo.order.OrderId;
import inc.roms.rcs.vo.sku.DistributionType;
import inc.roms.rcs.vo.sku.SkuId;
import inc.roms.rcs.vo.tote.*;
import inc.roms.rcs.vo.zones.ZoneFunction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import javax.persistence.Transient;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static inc.roms.rcs.service.inventory.domain.model.ToteFunction.DELIVERY;
import static inc.roms.rcs.service.inventory.domain.model.ToteFunction.STORAGE;
import static inc.roms.rcs.vo.tote.ToteStatus.AVAILABLE;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Service
@RequiredArgsConstructor
@Slf4j
public class ToteService {

    private final SlotDimensionsRepository slotDimensionsRepository;
    private final ToteRepository toteRepository;
    private final SkuRepository skuRepository;
    private final ZoneService zoneService;
    private final TaskBundleService taskBundleService;
    private final @Qualifier("AsyncOmniChannelService") OmniChannelService omniChannelService;

    public Tote updateTote(Tote tote) {
        return toteRepository.save(tote);
    }

    public ToteListResponse list(ToteListRequest request) {
        List<Tote> all = toteRepository.findAll(ToteRepository.buildSpec(request));
        return ToteListResponse.builder().totes(all).build();
    }

    public ToteDetails get(ToteId toteId) {
        Tote tote = toteRepository.findByToteId(toteId).orElseThrow(() -> new ResourceNotFoundException(toteId.getToteId(), Tote.class));
        return ToteDetails.convert(tote);
    }

    public Optional<Tote> findToteByToteId(ToteId toteId) {
        return toteRepository.findByToteId(toteId);
    }

    public Tote getToteById(Integer toteId) {
        return toteRepository.findById(toteId).orElse(null);
    }

    public SlotDimensions getSlotDimenstionsFor(ToteHeight toteHeight, TotePartitioning totePartitioning) {
        return slotDimensionsRepository.findById(new ToteType(totePartitioning, toteHeight)).orElseThrow();
    }

    public List<Tote> getStorageToteBySkuId(SkuId skuId) {
        return toteRepository.getBySkuIdAndToteFunction(skuId, ToteFunction.STORAGE);
    }
    
    public Set<ToteType> getStorageToteTypesOfAvailableTotesWithEmptySlots() {
        List<Tote> allTotes = toteRepository.findAllByToteStatusAndToteFunction(AVAILABLE, ToteFunction.STORAGE);
        return allTotes.stream().filter(this::hasEmptySlot).map(Tote::getToteType).collect(toSet());
    }

    public Optional<Tote> findAvailableStorageToteByTypeWithEmptySlot(ToteType toteType) {
        List<Tote> allTotesByToteType = toteRepository.findAllByToteTypeAndToteStatusAndToteFunction(toteType, AVAILABLE, ToteFunction.STORAGE);
        return allTotesByToteType.stream().filter(this::hasEmptySlot).min(Comparator.comparing(Tote::getEmptySlotsCount));
    }

    public boolean isPlaceLocationOccupied() {
        ZoneState placeZone = zoneService.getZone(ZoneFunction.PLACE);
        List<Tote> totesOnPlaceLocations = toteRepository.findAllByZoneId(placeZone.getZoneId());
        Optional<Tote> first = totesOnPlaceLocations.stream().filter(it -> AVAILABLE.equals(it.getToteStatus()))
                .findFirst();

        boolean present = first.isPresent();
        boolean ongoingPicking = taskBundleService.isOngoingPicking();

        log.debug("Is available tote on place location: {}. Is picking in progress: {}.", present, ongoingPicking);

        return present || ongoingPicking;
    }

    public Optional<Tote> getNextAvailableDeliveryTote() {
        ZoneState placeZone = zoneService.getZone(ZoneFunction.PLACE);
        List<Tote> totesOnPlaceLocations = toteRepository.findAllByZoneId(placeZone.getZoneId());
        Optional<Tote> first = totesOnPlaceLocations.stream().filter(it -> AVAILABLE.equals(it.getToteStatus()))
                .findFirst();

        if(first.isPresent()) {
            return first;
        }

        return findAvailableDeliveryTote();
    }

    public Optional<Tote> findAvailableDeliveryTote() {
        return findAllAvailableDeliveryTotes().stream().findFirst();
    }

    public List<Tote> findAllAvailableDeliveryTotes() {
        List<Tote> allTotesByToteType = toteRepository.findAllByToteStatusAndToteFunction(AVAILABLE, DELIVERY);
        return allTotesByToteType.stream().filter(this::isEmpty).collect(toList());
    }

    public List<Tote> findAllTotesWithAvailableStock() {
        List<Tote> allTotesByToteType = toteRepository.findAllByToteStatusAndToteFunction(AVAILABLE, ToteFunction.STORAGE);
        return allTotesByToteType.stream().filter(this::hasAvailableStock).collect(toList());
    }

    private boolean hasAvailableStock(Tote tote) {
        return tote.getSlots().stream()
                .map(Slot::getStorageInventory)
                .filter(Objects::nonNull)
                .anyMatch(it -> it.getAvailable().gt(0));
    }

    private boolean isEmpty(Tote tote) {
        return tote.getAllSlots().stream().allMatch(Slot::isEmpty);
    }

    private boolean hasEmptySlot(Tote tote) {
        return tote.getEmptySlotsCount() > 0;
    }

    public List<Tote> getStorageToteWithAvailableSku(SkuId skuId) {
        return toteRepository.getTotesWithAvailableSkuAndToteFunction(skuId, ToteFunction.STORAGE);
    }

    public Tote getToteWithStorageReservation(Reservation it) {
        return toteRepository.getToteWithStorageReservation(it);
    }

    public void cleanStorageReservation(Reservation reservation) {
        Tote tote = getToteWithStorageReservation(reservation);
        Slot storageSlot = tote.getAllSlots().stream().filter(slot -> slot.getStorageInventory().getReservations().contains(reservation)).findFirst().orElseThrow();
        storageSlot.getStorageInventory().getReservations().remove(reservation);
        storageSlot.getStorageInventory().recalculate();
        updateTote(tote);
    }

    public List<Tote> getDeliveryTotesFor(OrderId orderId) {
        return toteRepository.getDeliveryTotesByOrderId(orderId);
    }

    public Integer countDeliveryTotes() {
        return toteRepository.countByToteFunction(DELIVERY);
    }

    public ToteTechnicalData getToteDetails(@RequestParam ToteId toteId) {
        ToteTechnicalData.Builder builder = ToteTechnicalData.builder(toteId);
        Tote tote = findToteByToteId(toteId).orElseThrow();

        tote.getAllSlots().stream()
                .map(Slot::getStorageInventory)
                .filter(Objects::nonNull)
                .map(StorageInventory::getSkuBatch)
                .filter(Objects::nonNull)
                .filter(it -> Objects.nonNull(it.getSkuId()))
                .forEach(skuBatch -> {
                    Optional<Sku> optional = skuRepository.findById(skuBatch.getSkuId());
                    Sku sku = optional.orElseThrow();
                    builder.sku(sku, skuBatch.getQuantity());
                });

        return builder.build();
    }

    public Tote getToteWithDeliveryInventory(DeliveryInventory deliveryInventory) {
        return toteRepository.getToteWithDeliveryInventory(deliveryInventory);
    }

    public void create(CreateToteRequest request) {
        Optional<Tote> byToteId = toteRepository.findByToteId(request.getToteId());
        Tote tote = byToteId.orElse(new Tote());
        tote.setToteStatus(AVAILABLE);
        tote.setToteId(request.getToteId());
        tote.setToteOrientation(ToteOrientation.NORMAL);
        tote.setToteType(new ToteType(request.getPartitioning(), request.getHeight()));

        List<Slot> slots = request.getSlotModels().stream().map(this::createSlot).collect(Collectors.toList());
        tote.setSlots(slots);

        toteRepository.save(tote);
    }

    private Slot createSlot(CreateToteRequest.SlotModel slotModel) {
        Slot slot = new Slot();
        slot.setOrdinal(slotModel.getOrdinal());
        StorageInventory storageInventory = new StorageInventory();
        SkuBatch skuBatch = new SkuBatch();
        skuBatch.setSkuId(slotModel.getSkuId());
        skuBatch.setQuantity(slotModel.getQuantity());
        storageInventory.setSkuBatch(skuBatch);
        slot.setStorageInventory(storageInventory);
        return slot;
    }

    public List<Tote> findAllWithDeliveryInventory() {
        List<Tote> all = toteRepository.findAll();
        return all.stream()
                .filter(it -> {
                    List<Slot> allSlots = it.getAllSlots();
                    return allSlots.stream()
                            .anyMatch(slot -> slot.getDeliveryInventory() != null);
                }).collect(toList());
    }

    public void clean(ToteId toteId) {
        Optional<Tote> byToteId = toteRepository.findByToteId(toteId);
        Tote tote = byToteId.orElseThrow(IllegalArgumentException::new);
        if(DELIVERY.equals(tote.getToteFunction())) {
            Stream<Slot> slotStream = tote.getAllSlots().stream().filter(it -> it.getDeliveryInventory() != null);

            slotStream
                    .filter(it -> it.getDeliveryInventory() != null)
                    .forEach(it -> it.setDeliveryInventory(null));

            tote.setToteStatus(AVAILABLE);
        } else if (STORAGE.equals(tote.getToteFunction())) {
            log.debug("{}: Recalculating!", toteId);
            List<StorageInventory> inventories = tote.getSlots().stream().map(Slot::getStorageInventory).filter(Objects::nonNull).collect(toList());
            log.debug("{}: Recalculating, slots: ", toteId);
            inventories.forEach(it -> {
                if(it.getReservations() != null) {
                    log.debug("{}: Recalculating, Number of reservations: {}", toteId, it.getReservations().size());
                } else {
                    log.debug("{}: Recalculating: NULL Reservations", toteId);
                }
                log.debug("{}: Recalculating, Available: {}", toteId, it.getAvailable());
                it.recalculate();
                log.debug("{}: Recalculating, Available after calculating: {}", toteId, it.getAvailable());
            });
        }
        toteRepository.save(tote);
    }

    public Optional<Tote> findToteWithEmptySlotsMatchingTempRegime(TemperatureRegime temperatureRegime) {
        return toteRepository.findAllByToteStatusAndToteFunction(AVAILABLE, ToteFunction.STORAGE)
                .stream()
                .filter(it -> it.getEmptySlotsCount() > 0)
                .filter(it -> it.getTemperatureRegime().equals(temperatureRegime))
                .filter(it -> skuWithTempRegime(it, temperatureRegime))
                .min(Comparator.comparing(Tote::getEmptySlotsCount));
    }

    private boolean skuWithTempRegime(Tote tote, TemperatureRegime temperatureRegime) {
        if(tote.getNotEmptySlotsCount() == 0) return true;
        return tote.getSlots().stream().filter(it -> !it.isEmpty())
                .map(Slot::getStorageInventory)
                .filter(Objects::nonNull)
                .map(StorageInventory::getSkuBatch)
                .filter(Objects::nonNull)
                .map(SkuBatch::getSkuId)
                .filter(Objects::nonNull)
                .map(skuRepository::findBySkuId)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .allMatch(it -> temperatureRegime.equals(DistributionType.toTempRegime(it.getDistributionType())));
    }

    public void markAsFailing(ToteId toteId, ToteStatus toteStatus) {
        Tote tote = toteRepository.findByToteId(toteId).orElseThrow(() -> new ToteNotFoundException(toteId));
        tote.setToteStatus(toteStatus);
        if(tote.getToteFunction().equals(DELIVERY)) {
            failDeliveryTote(tote);
        } else if (tote.getToteFunction().equals(ToteFunction.STORAGE)) {
            tote.skuBatches().forEach(omniChannelService::removeInventory);
        }
        toteRepository.save(tote);
    }

    private void failDeliveryTote(Tote tote) {
        tote.getSlots()
                .stream()
                .filter(it -> it.getDeliveryInventory() != null)
                .map(Slot::getDeliveryInventory)
                .forEach(di -> di.setReservations(new ArrayList<>()));
    }

    public Tote delete(ToteId toteId) {
        Tote toteToBeDeleted = toteRepository.findByToteId(toteId).orElseThrow(() -> new ToteNotFoundException(toteId));
        toteRepository.delete(toteToBeDeleted);
        return toteToBeDeleted;
    }


    public ToteResponse delete(ToteRequest toteRequest) {
        return ToteResponse.from(delete(toteRequest.getToteId()));
    }

    public BatchToteActionResponse delete(BatchToteActionRequest toteBatchRequest) {
        List<ToteId> deleted = new ArrayList<>();
        List<ToteId> failures = new ArrayList<>();

        toteBatchRequest.getToteIds().forEach(toteId -> {
            try {
                delete(toteId);
                deleted.add(toteId);
            } catch (Exception e) {
                failures.add(toteId);
            }
        });

        return BatchToteActionResponse.builder()
                .responseCode(failures.isEmpty() ? ResponseCode.ACCEPTED : ResponseCode.REJECTED)
                .responseDetails(ToteBatchResponseDetails.builder()
                        .failures(failures)
                        .success(deleted).build()).build();
    }

    public void cleanDeliveryTote(ToteId toteId, OrderId orderId) {
        Optional<Tote> maybeTote = findToteByToteId(toteId);
        Tote tote = maybeTote.orElseThrow();
        tote.getAllSlots().stream()
                .filter(it -> it.getDeliveryInventory() != null)
                .filter(it -> Objects.equals(it.getDeliveryInventory().getOrderId(), orderId))
                .forEach(it -> cleanDeliveryInventory(it, orderId));
        tote.setToteStatus(AVAILABLE);
        tote.setZoneId(zoneService.getZone(ZoneFunction.STAGING).getZoneId());
        updateTote(tote);
    }


    @Transient
    public void cleanDeliveryInventory(Slot slot, OrderId orderId) {
        if(slot.getDeliveryInventory() == null) return;
        if(!Objects.equals(slot.getDeliveryInventory().getOrderId(), orderId)) return;

        slot.setDeliveryInventory(null);
    }
}