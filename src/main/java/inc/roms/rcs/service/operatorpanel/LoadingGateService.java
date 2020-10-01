package inc.roms.rcs.service.operatorpanel;

import com.google.common.base.Strings;
import inc.roms.rcs.exception.BusinessExceptions;
import inc.roms.rcs.service.configuration.ConfigKey;
import inc.roms.rcs.service.configuration.ConfigurationService;
import inc.roms.rcs.service.cubing.CubingService;
import inc.roms.rcs.service.cubing.model.request.DetermineBestToteTypeRequest;
import inc.roms.rcs.service.cubing.model.response.DetermineBestToteTypeResponse;
import inc.roms.rcs.service.inventory.SkuService;
import inc.roms.rcs.service.inventory.SupplyService;
import inc.roms.rcs.service.inventory.ToteService;
import inc.roms.rcs.service.inventory.domain.model.*;
import inc.roms.rcs.service.inventory.exception.MixedTemperatureRegimesException;
import inc.roms.rcs.service.inventory.exception.ToteNotFoundException;
import inc.roms.rcs.service.inventory.response.SupplyItemsDetails;
import inc.roms.rcs.service.location.LocationService;
import inc.roms.rcs.service.location.model.Location;
import inc.roms.rcs.service.machineoperator.MachineOperatorService;
import inc.roms.rcs.service.machineoperator.ZoneService;
import inc.roms.rcs.service.machineoperator.model.LedsRequest;
import inc.roms.rcs.service.machineoperator.model.ZoneState;
import inc.roms.rcs.service.omnichannel.OmniChannelService;
import inc.roms.rcs.service.operatorpanel.domain.barcode.config.BarcodeScannersPrefixProperties;
import inc.roms.rcs.service.operatorpanel.exception.NoSpaceForStockException;
import inc.roms.rcs.service.operatorpanel.exception.NotEnoughStoragePlacesForTote;
import inc.roms.rcs.service.operatorpanel.model.SkuBatchModel;
import inc.roms.rcs.service.operatorpanel.request.InductRequest;
import inc.roms.rcs.service.operatorpanel.request.StorageSlotModel;
import inc.roms.rcs.service.operatorpanel.request.TotesForSkuRequest;
import inc.roms.rcs.service.operatorpanel.response.InductResponse;
import inc.roms.rcs.service.operatorpanel.response.InductResponseDetails;
import inc.roms.rcs.vo.common.Quantity;
import inc.roms.rcs.vo.common.ResponseCode;
import inc.roms.rcs.vo.common.TemperatureRegime;
import inc.roms.rcs.vo.common.TransactionId;
import inc.roms.rcs.vo.sku.DistributionType;
import inc.roms.rcs.vo.sku.SkuId;
import inc.roms.rcs.vo.tote.*;
import inc.roms.rcs.vo.zones.ZoneFunction;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static inc.roms.rcs.vo.location.LocationId.LOADING_GATE;
import static inc.roms.rcs.vo.zones.ZoneFunction.STAGING;
import static inc.roms.rcs.vo.zones.ZoneFunction.STORAGE;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Service
@Slf4j
@AllArgsConstructor
public class LoadingGateService {

    private final ToteService toteService;
    private final MachineOperatorService machineOperatorService;
    private final CubingService cubingService;
    private final SkuService skuService;
    private final OmniChannelService omniChannelService;
    private final LocationService locationService;
    private final BarcodeScannersPrefixProperties barcodeScannersPrefixProperties;
    private final SupplyService supplyService;
    private final ZoneService zoneService;
    private final BusinessExceptions businessExceptions;
    private final ConfigurationService configurationService;

    public void openLoadingGate() {
        machineOperatorService.openLoadingGate();
    }

    @Transactional
    public InductResponse induct(InductRequest inductRequest) {
        log.debug("Got induct request with tote id: {}", inductRequest.getToteId());
        Tote tote = toteService.findToteByToteId(inductRequest.getToteId()).orElseThrow(() -> businessExceptions.toteNotFoundException(inductRequest.getToteId()));
        // NoSuchElementException("no such tote, tote_id: " +
        // inductRequest.getToteId()));

        // TODO whole this part, regarding update of tote slots should be extracted from
        // here to ToteService
        List<Slot> oldSlots = (tote.getSlots() == null ? new ArrayList<>() : List.copyOf(tote.getSlots()));

        if (tote.getSlots() == null) {
            tote.setSlots(new ArrayList<>());
        }

        reportSkuBatchChangesToECom(inductRequest, oldSlots);

        inductRequest.getSlots().forEach(slotModel -> {
            if (slotModel.getSkuId() != null && !Strings.isNullOrEmpty(slotModel.getSkuId().getSkuId()))
                skuService.getReadySku(slotModel.getSkuId());

            Slot slotByOrdinal = tote.getSlotByOrdinal(slotModel.getOrdinal());
            tote.addSlot(slotByOrdinal);
            if (slotByOrdinal.getDeliveryInventory() != null)
                return;
            if (slotByOrdinal.getStorageInventory() == null)
                slotByOrdinal.setStorageInventory(new StorageInventory());
            if (slotByOrdinal.getStorageInventory().getSkuBatch() == null)
                slotByOrdinal.getStorageInventory().setSkuBatch(new SkuBatch());
            //add data to scheduled supply
            slotByOrdinal.getStorageInventory().getSkuBatch().setQuantity(slotModel.getQuantity());
            slotByOrdinal.getStorageInventory().getSkuBatch().setSkuId(slotModel.getSkuId());
            slotByOrdinal.getStorageInventory().recalculate();

            if (slotModel.getSupplyItemId() != null) {
                SupplyItemsDetails supplyDetails = supplyService.getSupplyItemByItemId(slotModel.getSupplyItemId());
                slotByOrdinal.getStorageInventory().getSkuBatch().setSellByDate(supplyDetails.getSellByDate());
                slotByOrdinal.getStorageInventory().getSkuBatch().setState(SkuBatchState.AVAILABLE);
            }
        });

        tote.getSlots().stream().map(Slot::getStorageInventory).filter(Objects::nonNull).forEach(StorageInventory::recalculate);

        Integer targetDestinationTotes = configurationService.getConfigValue(ConfigKey.NUMBER_OF_DELIVERY_TOTES);
        Integer currentNumberOfDestinationTotes = toteService.countDeliveryTotes();

        if (tote.getToteType().getTotePartitioning().equals(TotePartitioning.BIPARTITE) &&
                tote.getToteType().getToteHeight().equals(ToteHeight.LOW) &&
                tote.getNotEmptySlotsCount() == 0 &&
                targetDestinationTotes > currentNumberOfDestinationTotes) {
            tote.setToteFunction(ToteFunction.DELIVERY);
        } else {
            tote.setToteFunction(ToteFunction.STORAGE);
        }

        tote.setToteStatus(ToteStatus.AVAILABLE);

        log.debug("Updating tote: {}", tote);
        accept(tote);
        toteService.updateTote(tote);
        locationService.removeToteFromLocation(LOADING_GATE);
        turnAllLightsOff();
        return new InductResponse(ResponseCode.ACCEPTED, new InductResponseDetails(tote.getToteId(), tote.getToteFunction()));
    }

    private void reportSkuBatchChangesToECom(InductRequest inductRequest, List<Slot> oldSlots) {
        inductRequest.getSlots().forEach(it -> {
            log.debug("Induct tote {}: slot ordinal: {}", inductRequest.getToteId(), it.getOrdinal());
            Optional<Slot> oldValue = oldSlots.stream().filter(slot -> slot.getOrdinal() == it.getOrdinal()).findFirst();
            Quantity delta = it.getQuantity();
            log.debug("Induct tote {}: Setting quantity to: {}", inductRequest.getToteId(), delta);
            if (oldValue.isPresent()) {
                log.debug("Induct tote {}: there was sku batch earlier!", inductRequest.getToteId());
                if (skuBatchIsNotNull(oldValue.get())) {
                    log.debug("Induct tote {}: sku batch was not null!", inductRequest.getToteId());
                    SkuBatch oldBatch = oldValue.get().getStorageInventory().getSkuBatch();
                    if (it.getSkuId().equals(oldBatch.getSkuId())) {
                        delta = delta.minus(oldBatch.getQuantity());
                        log.debug("Induct tote {}: There was the same sku, delta is: {}!", inductRequest.getToteId(), delta);
                    } else {
                        omniChannelService.updateInventory(oldBatch.getSkuId(), oldBatch.getQuantity().multiply(-1L));
                        log.debug("Induct tote {}: There was different sku, removing it from CP!", inductRequest.getToteId());
                    }
                }
            }
            if (it.getSupplyItemId() != null) {
                log.debug("Induct tote {}: Slot had supply item id, updating!", inductRequest.getToteId());
                supplyService.updateItem(it.getSupplyItemId(), delta);
            } else {
                log.debug("Induct tote {}: Slot didn't have supply item id, updating!", inductRequest.getToteId());
                omniChannelService.updateInventory(it.getSkuId(), delta);
            }
        });
    }

    private boolean skuBatchIsNotNull(Slot oldValue) {
        return oldValue.getStorageInventory() != null && oldValue.getStorageInventory().getSkuBatch() != null;
    }

    private ZoneState getZone(Tote tote) {
        ZoneState zone;
        ZoneState placeZone = zoneService.getZone(ZoneFunction.PLACE);
        if (ToteFunction.DELIVERY.equals(tote.getToteFunction())) {
            if (!toteService.isPlaceLocationOccupied()) {
                zone = placeZone;
            } else {
                zone = zoneService.getZone(STAGING);
            }
        } else {
            Optional<TemperatureRegime> temperatureRegime = calculateTempRegime(tote);
            if (temperatureRegime.isPresent()) {
                zone = zoneService.getZone(STORAGE, temperatureRegime.get());
            } else {
                List<ZoneState> zones = zoneService.getZones(STORAGE);
                zone = zones.stream().min(Comparator.comparing(ZoneState::getAvailableLocations)).orElseThrow();
            }
        }
        if (placeZone != zone && !zone.getZoneId().equals(tote.getZoneId()) && !zone.getAvailableLocations().gt(0)) {
            throw new NotEnoughStoragePlacesForTote(zone.getZoneId(), tote.getToteId());
        }
        return zone;
    }

    private Optional<TemperatureRegime> calculateTempRegime(Tote tote) {
        List<Sku> skus = tote.getAllSlots().stream()
                .map(Slot::getStorageInventory)
                .filter(Objects::nonNull)
                .map(StorageInventory::getSkuBatch)
                .map(SkuBatch::getSkuId)
                .filter(Objects::nonNull)
                .map(skuService::getReadySku)
                .collect(toList());

        List<TemperatureRegime> tempRegimes = skus.stream().map(Sku::getDistributionType)
                .map(DistributionType::toTempRegime)
                .distinct().collect(toList());

        if (tempRegimes.size() > 1) {
            throw new MixedTemperatureRegimesException(skus.stream().map(Sku::getSkuId).collect(toList()));
        }

        if (tempRegimes.size() == 0) {
            return Optional.empty();
        }

        return Optional.ofNullable(tempRegimes.get(0));
    }

    public Map<Tote, Quantity> requestTotesWithAssignedSkuOrGetEmptyTotes(TotesForSkuRequest totesForSkuRequest) {
        Map<Tote, Quantity> reservedTotes = new HashMap<>();
        log.info("Reserving totes for {}", totesForSkuRequest);
        // this is just to verify that the squ exists
        skuService.getReadySku(totesForSkuRequest.getSkuId());
        List<Tote> totesWithSku = toteService.getStorageToteBySkuId(totesForSkuRequest.getSkuId());
        if (!totesWithSku.isEmpty()) {
            Tote tote = totesWithSku.get(0);
            Quantity quantity = Quantity.of(100);
            reservedTotes.put(tote, quantity);
            getToteForResupply(tote);
            return reservedTotes;
        } else {
            return requestTotes(totesForSkuRequest);
        }
    }

    public Map<Tote, Quantity> requestTotes(TotesForSkuRequest totesForSkuRequest) {
        Map<Tote, Quantity> reservedTotes = new HashMap<>();
        log.info("Reserving totes for {}", totesForSkuRequest);
        Sku sku = skuService.getReadySku(totesForSkuRequest.getSkuId());
        Quantity left = totesForSkuRequest.getQuantity();
        while (left.gt(0)) {
            Set<ToteType> toteTypesOfEmptySlots = toteService.getStorageToteTypesOfAvailableTotesWithEmptySlots();

            if (toteTypesOfEmptySlots.isEmpty() && left.gt(0)) {
                throw new NoSpaceForStockException(totesForSkuRequest.getSkuId(), left, reservedTotes.size(), "No more empty totes!");
            }

            DetermineBestToteTypeRequest determineBestToteTypeRequest = new DetermineBestToteTypeRequest(sku,
                    toteTypesOfEmptySlots, 0.8d);
            DetermineBestToteTypeResponse bestToteType = cubingService
                    .determineBestToteType(determineBestToteTypeRequest);
            Optional<Tote> maybeTote = toteService.findAvailableStorageToteByTypeWithEmptySlot(bestToteType.getBestToteType());

            if (maybeTote.isPresent()) {
                Tote tote = maybeTote.get();
                Quantity totalQuantityThatCanBePlacedInTote = bestToteType.getMaxQuantity().multiply(tote.getEmptySlotsCount());
                getToteForResupply(maybeTote.get());
                reservedTotes.put(maybeTote.get(), totalQuantityThatCanBePlacedInTote);
                left = left.minus(totalQuantityThatCanBePlacedInTote);
                log.info("Quantity left to distribute between totes: {}, last tote can take up to: {} eaches of sku",
                        left, totalQuantityThatCanBePlacedInTote);
            }
        }

        return reservedTotes;
    }

    public void deliverTote(ToteId toteId, ZoneFunction zoneFunction) {
        machineOperatorService.deliverTote(toteId, zoneService.getZone(zoneFunction).getZoneId());
    }

    private void getToteForResupply(Tote tote) {
        tote.setToteStatus(ToteStatus.RESERVED);
        toteService.updateTote(tote);
        deliverTote(tote.getToteId(), ZoneFunction.LOADING_GATE);
        log.info("Reserved tote {} for resupplying", tote.getToteId());
    }

    public Tote findToteByToteId(ToteId toteId) {
        return toteService.findToteByToteId(toteId).orElseThrow();
    }

    public void turnLightsOnOverEmptySlots(Tote tote) {
        List<Integer> ledsOverEmptySlots = new ArrayList<>();
        List<Slot> slots = tote.getAllSlots();

        for (int i = 0; i < slots.size(); i++) {
            if (slots.get(0).isEmpty()) {
                ledsOverEmptySlots.add(Integer.valueOf(
                        barcodeScannersPrefixProperties.getPrefix(tote.getToteType().getTotePartitioning(), i)));
            }
        }

        machineOperatorService.turnOnLights(new LedsRequest(ledsOverEmptySlots));
    }

    public void turnAllLightsOff() {
        machineOperatorService.turnLightsOff();
    }

    private Map<SkuId, Quantity> calculateSkuDiff(List<SkuBatchModel> oldSkuBatches, InductRequest inductRequest) {
        Map<SkuId, Quantity> diff = inductRequest.getSlots()
                .stream()
                .filter(Objects::nonNull)
                .filter(it -> it.getQuantity() != null)
                .collect(toMap(StorageSlotModel::getSkuId, StorageSlotModel::getQuantity, Quantity::plus));

        Map<SkuId, Quantity> oldSkuMap = oldSkuBatches.stream().collect(toMap(SkuBatchModel::getSkuId, SkuBatchModel::getQuantity, Quantity::plus));
        oldSkuMap.forEach((k, v) -> diff.merge(k, v, Quantity::minus));
        return diff;
    }

    public void closeLoadingGate(TransactionId transactionId) {
        Location location = locationService.getLocation(LOADING_GATE);
        if (location.getCurrentTote() != null) {
            ToteId toteId = location.getCurrentTote().getToteId();
            Tote tote = toteService.findToteByToteId(toteId).orElseThrow(() -> new ToteNotFoundException(toteId));
            accept(tote);
            toteService.updateTote(tote);
        } else {
            machineOperatorService.closeLoadingGate(transactionId);
        }
    }

    public void accept(ToteId toteId) {
        Tote tote = toteService.findToteByToteId(toteId).orElseThrow(() -> new ToteNotFoundException(toteId));
        accept(tote);
        toteService.updateTote(tote);
    }

    public void accept(Tote tote) {
        ZoneState zone = getZone(tote);
        tote.setZoneId(zone.getZoneId());
        tote.setTemperatureRegime(zone.getTemperatureRegime());
        tote.setToteStatus(ToteStatus.AVAILABLE);

        machineOperatorService.moveTote(tote.getToteId(), tote.getZoneId());
    }

}
