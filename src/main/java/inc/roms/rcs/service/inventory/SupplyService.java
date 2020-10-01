package inc.roms.rcs.service.inventory;

import inc.roms.rcs.exception.BusinessExceptions;
import inc.roms.rcs.service.inventory.domain.model.*;
import inc.roms.rcs.service.inventory.domain.repository.ScheduledSupplyItemRepository;
import inc.roms.rcs.service.inventory.domain.repository.ScheduledSupplyRepository;
import inc.roms.rcs.service.inventory.exception.MixedTemperatureRegimesException;
import inc.roms.rcs.service.inventory.request.BatchDisposeSkuBatchRequest;
import inc.roms.rcs.service.inventory.request.DisposeSkuBatchRequest;
import inc.roms.rcs.service.inventory.request.ListSupplyRequest;
import inc.roms.rcs.service.inventory.request.ScheduleSupplyRequest;
import inc.roms.rcs.service.inventory.response.*;
import inc.roms.rcs.service.machineoperator.MachineOperatorService;
import inc.roms.rcs.service.machineoperator.ZoneService;
import inc.roms.rcs.service.omnichannel.OmniChannelService;
import inc.roms.rcs.service.operatorpanel.exception.NoSpaceForStockException;
import inc.roms.rcs.service.operatorpanel.request.SupplyToteRequest;
import inc.roms.rcs.service.operatorpanel.response.SupplyToteResponse;
import inc.roms.rcs.service.operatorpanel.response.SupplyToteResponseDetails;
import inc.roms.rcs.vo.common.Quantity;
import inc.roms.rcs.vo.common.ResponseCode;
import inc.roms.rcs.vo.sku.DistributionType;
import inc.roms.rcs.vo.supply.ScheduledSupplyItemId;
import inc.roms.rcs.vo.zones.ZoneFunction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static inc.roms.rcs.vo.sku.DistributionType.toTempRegime;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class SupplyService {

    private final ScheduledSupplyRepository scheduledSupplyRepository;
    private final ScheduledSupplyItemRepository scheduledSupplyItemRepository;
    private final SkuBatchService skuBatchService;
    private final SkuService skuService;
    private final ToteService toteService;
    private final MachineOperatorService machineOperatorService;
    private final ZoneService zoneService;
    private final BusinessExceptions businessExceptions;
    private final OmniChannelService omniChannelService;

    private final Clock clock;

    //TODO move all error handling to error package (along with error responses with 2xx)
    public ScheduleSupplyResponse scheduleSupply(ScheduleSupplyRequest request) {
        ScheduledSupply supply = scheduledSupplyRepository.findByDeliveryDateAndDeliveryTurnAndDistributionType(
                request.getDeliveryDate(),
                request.getDeliveryTurn(),
                request.getDistributionType()).orElseGet(ScheduledSupply::createNew);
        supply.setDeliveryDate(request.getDeliveryDate());
        supply.setDeliveryTurn(request.getDeliveryTurn());
        supply.setDistributionType(request.getDistributionType());

        ScheduledSupplyItem item = new ScheduledSupplyItem();
        item.setQuantity(request.getQuantity());
        item.setSellByDate(request.getSellByDate());

        skuService.getReadySku(request.getSkuId());

        item.setSkuId(request.getSkuId());
        item.setInductedQuantity(Quantity.of(0));
        item.setScheduledSupplyItemId(ScheduledSupplyItemId.generate());

        supply.getItems().add(item);

        ScheduledSupply afterSave = scheduledSupplyRepository.save(supply);
        return ScheduleSupplyResponse.builder()
                .responseCode(ResponseCode.ACCEPTED)
                .details(ScheduleSupplyResponseDetails.builder()
                        .scheduledSupply(afterSave).build())
                .build();

    }

    public BatchDisposeSkuBatchResponse batchDispose(BatchDisposeSkuBatchRequest batchDisposeSkuBatchRequest) {
        List<DisposeSkuBatchResponse> responses = batchDisposeSkuBatchRequest
                .getDisposeSkuBatchRequests()
                .stream()
                .map(this::dispose).collect(toList());

        return new BatchDisposeSkuBatchResponse(responses);
    }

    //TODO this needs smarter handling - what if we have reservation on disposed sku batches
    public DisposeSkuBatchResponse dispose(DisposeSkuBatchRequest disposeSkuBatchRequest) {
        List<SkuBatch> toBeDisposed = skuBatchService.findAllBySkuIdAndSellByDate(
                disposeSkuBatchRequest.getSkuId(),
                disposeSkuBatchRequest.getSellByDate()
        );

        toBeDisposed.stream().peek(it -> it.setState(SkuBatchState.DISPOSED)).forEach(skuBatchService::save);
        DisposeSkuBatchResponse disposeSkuBatchResponse = new DisposeSkuBatchResponse();
        disposeSkuBatchResponse.setResponseCode(ResponseCode.ACCEPTED);
        disposeSkuBatchResponse.setDetails(new DisposeSkuBatchResponseDetails(null, disposeSkuBatchRequest.getSkuId()));
        return disposeSkuBatchResponse;
    }

    public SupplyToteResponse requestTote(SupplyToteRequest request) {
        List<DistributionType> tempRegimes = request.getSkuIds()
                .stream()
                .map(skuService::getReadySku)
                .map(Sku::getDistributionType)
                .distinct().collect(toList());

        if (tempRegimes.size() != 1) {
            throw new MixedTemperatureRegimesException(request.getSkuIds());
        }

        DistributionType distributionType = tempRegimes.get(0);
        Tote tote = toteService
                .findToteWithEmptySlotsMatchingTempRegime(toTempRegime(distributionType))
                .orElseThrow(() -> new NoSpaceForStockException(request.getSkuIds().get(0)));

        machineOperatorService.deliverTote(tote.getToteId(), zoneService.getZone(ZoneFunction.LOADING_GATE).getZoneId());
        return new SupplyToteResponse(ResponseCode.ACCEPTED, new SupplyToteResponseDetails(tote.getToteId()));
    }

    public ListSupplyResponse list(ListSupplyRequest request) {
        List<ScheduledSupply> scheduledSupplies = request.isOnlyForToday()
                ? scheduledSupplyRepository.findAllByDeliveryDate(LocalDate.now(clock))
                : scheduledSupplyRepository.findAll();

        List<SupplyDetails> supplyDetails = scheduledSupplies.stream().map(this::createSupplyDetails).collect(toList());

        return ListSupplyResponse.builder().supply(supplyDetails).build();
    }

    private SupplyDetails createSupplyDetails(ScheduledSupply it) {
        return new SupplyDetails(it.getDeliveryTurn(), it.getDistributionType(), it.getSupplyId(), it.getDeliveryDate());
    }

    public ListSupplyItemsResponse list(ListSupplyItemsRequest request) {
        ScheduledSupply supply = scheduledSupplyRepository.findBySupplyId(request.getSupplyId());
        List<SupplyItemsDetails> supplyItemDetails = supply.getItems().stream().map(this::createSupplyItemDetails).collect(toList());
        return ListSupplyItemsResponse.builder().items(supplyItemDetails).build();
    }

    private SupplyItemsDetails createSupplyItemDetails(ScheduledSupplyItem item) {
        Sku sku = skuService.getReadySku(item.getSkuId());
        return SupplyItemsDetails.builder()
                .categoryId(sku.getCategory())
                .itemId(item.getScheduledSupplyItemId())
                .expectedQuantity(item.getQuantity())
                .inductedQuantity(item.getInductedQuantity())
                .skuName(sku.getName())
                .skuId(sku.getSkuId())
                .sellByDate(item.getSellByDate())
                .build();
    }

    public void updateItem(ScheduledSupplyItemId supplyItemId, Quantity quantity) {
        ScheduledSupplyItem supplyItem = scheduledSupplyItemRepository.findByScheduledSupplyItemId(supplyItemId);
        omniChannelService.updateInventory(supplyItem, quantity);
        supplyItem.setInductedQuantity(quantity.plus(supplyItem.getInductedQuantity()));
        scheduledSupplyItemRepository.save(supplyItem);
    }

    public SupplyItemsDetails getSupplyItemByItemId(ScheduledSupplyItemId supplyItemId) {
        ScheduledSupplyItem supplyItem = scheduledSupplyItemRepository.findByScheduledSupplyItemId(supplyItemId);
        return createSupplyItemDetails(supplyItem);
    }
}
