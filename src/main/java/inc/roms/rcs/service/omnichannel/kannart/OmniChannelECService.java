package inc.roms.rcs.service.omnichannel.kannart;

import com.google.common.base.Strings;
import inc.roms.rcs.service.inventory.SkuService;
import inc.roms.rcs.service.inventory.domain.model.ScheduledSupplyItem;
import inc.roms.rcs.service.inventory.domain.model.Sku;
import inc.roms.rcs.service.inventory.domain.model.SkuBatch;
import inc.roms.rcs.service.issue.IssueFactory;
import inc.roms.rcs.service.issue.IssueService;
import inc.roms.rcs.service.issue.request.CreateIssueRequest;
import inc.roms.rcs.service.omnichannel.OmniChannelService;
import inc.roms.rcs.service.omnichannel.kannart.model.*;
import inc.roms.rcs.service.order.domain.OrderService;
import inc.roms.rcs.service.order.domain.model.Order;
import inc.roms.rcs.service.order.domain.model.OrderTransaction;
import inc.roms.rcs.service.order.domain.model.TransactionType;
import inc.roms.rcs.service.task.domain.model.TaskUpdateRequest;
import inc.roms.rcs.vo.common.Quantity;
import inc.roms.rcs.vo.common.RcsOperationId;
import inc.roms.rcs.vo.common.StoreId;
import inc.roms.rcs.vo.order.OrderStatus;
import inc.roms.rcs.vo.order.OrderType;
import inc.roms.rcs.vo.sku.SkuId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static inc.roms.rcs.api.external.v2_0.vo.JapanTimeHelper.nowInJapan;
import static inc.roms.rcs.service.omnichannel.OmniChannelProfiles.COMMERCE_PLATFORM;
import static inc.roms.rcs.vo.order.OrderStatus.IN_PICKUP_GATE;

@Slf4j
@Service
@Profile(COMMERCE_PLATFORM)
@RequiredArgsConstructor
@Primary
public class OmniChannelECService implements OmniChannelService {

    private final List<OrderStatus> statusesToBeReported = List.of(IN_PICKUP_GATE, OrderStatus.PREORDER_READY, OrderStatus.FAILED);

    private final OmniChannelECClient omniChannelECClient;
    private final SkuService skuService;
    private final OrderService orderService;

    private final IssueService issueService;
    private final IssueFactory issueFactory;

    @Value("${rcs.store.code:POC}")
    private StoreId storeCode;

    @Override
    public void updateInventory(Map<SkuId, Quantity> skuDiff) {
        skuDiff.entrySet().stream()
                .filter(it -> Objects.nonNull(it.getKey()))
                .filter(it -> !Strings.isNullOrEmpty(it.getKey().getSkuId()))
                .map(it -> toUpdateInventoryRequest(it.getKey(), it.getValue()))
                .forEach(this::updateInventory);
    }

    private void updateInventory(UpdateInventoryRequest updateInventoryRequest) {
        if (updateInventoryRequest.getDeliveryTurn() != null && updateInventoryRequest.getQuantity() != null && !updateInventoryRequest.getQuantity().equals(Quantity.of(0))) {
            omniChannelECClient.send(updateInventoryRequest);
        }
    }

    @Override
    public void orderStatusChanged(Order order) {
        try {
            if (!statusesToBeReported.contains(order.getOrderStatus())) {
                log.debug("Order in status: {}, don't have to report to EC!", order.getOrderStatus());
                return;
            }
            log.debug("Sending report for order: {}", order.getOrderId());
            OrderStatusChangedRequest request = convert(order, orderService.findTransaction(order, transactionType(order)));
            log.debug("Request to be sent: {}", request);
            omniChannelECClient.send(request);
        } catch (Exception ex) {
            log.error("Couldn't notify CP about order status!", ex);
            CreateIssueRequest cpNotWorking = issueFactory.orderStatusNotSent(order.getOrderId());
            issueService.create(cpNotWorking);
        }
    }

    @Override
    public void updateInventory(SkuBatch skuBatch, TaskUpdateRequest taskUpdateRequest, OrderType orderType) {
        log.info("No action required in case of integration with CP");
    }

    @Override
    public void updateInventory(ScheduledSupplyItem supplyItem, Quantity quantity) {
        Sku sku = skuService.getReadySku(supplyItem.getSkuId());
        UpdateInventoryRequest request = UpdateInventoryRequest.builder()
                .quantity(quantity)
                .storeCode(storeCode)
                .sku(sku.getSkuId())
                .deliveryDate(supplyItem.getScheduledSupply().getDeliveryDate())
                .updateDate(nowInJapan())
                .deliveryTurn(supplyItem.getScheduledSupply().getDeliveryTurn())
                .updateType(InventoryUpdateType.ACCEPT)
                .build();
        omniChannelECClient.send(request);
    }

    @Override
    public void updateInventory(SkuId skuId, Quantity quantity) {
        UpdateInventoryRequest request = toUpdateInventoryRequest(skuId, quantity);
        omniChannelECClient.send(request);
    }

    @Override
    public void skuUpdated(Sku it) {
        ProductReadyRequest request = new ProductReadyRequest();
        request.setRcsOperationId(RcsOperationId.generate());
        request.setSku(it.getSkuId());
        request.setStoreCode(storeCode);
        omniChannelECClient.send(request);
    }

    @Override
    public void removeInventory(SkuBatch skuBatch) {
        this.updateInventory(UpdateInventoryRequest.builder()
                .quantity(skuBatch.getQuantity().multiply(-1L))
                .storeCode(storeCode)
                .sku(skuBatch.getSkuId())
                .updateType(InventoryUpdateType.DISPOSE)
                .build());
    }

    @Override
    public void addInventory(SkuBatch skuBatch) {
        //TO BE DECIDED ON CP SIDE
//        this.updateInventory(UpdateInventoryRequest.builder()
//                .quantity(skuBatch.getQuantity())
//                .storeCode(storeCode)
//                .sku(skuBatch.getSkuId())
//                .updateType(InventoryUpdateType.ACCEPT)
//                .build());
    }

    private OrderStatusChangedRequest convert(Order order, OrderTransaction transaction) {
        return OrderStatusChangedRequest.builder()
                .orderNo(order.getOrderId())
                .actionCode(ActionCode.from(order.getOrderStatus()))
                .storeCode(storeCode)
                .exitCode(ExitCode.SUCCESS)
                .rcsOperationId(RcsOperationId.generate())
                .transactionId(transaction.getTransactionId())
                .build();
    }

    private TransactionType transactionType(Order order) {
        if (order.getOrderStatus().equals(IN_PICKUP_GATE)) return TransactionType.DELIVER;
        else return TransactionType.CREATE;
    }

    private UpdateInventoryRequest toUpdateInventoryRequest(SkuId skuId, Quantity quantity) {
        Sku sku = skuService.getReadySku(skuId);
        boolean gt = quantity.gt(0);
        InventoryUpdateType updateType = gt ? InventoryUpdateType.ACCEPT : InventoryUpdateType.DISPOSE;
        return UpdateInventoryRequest.builder()
                .quantity(quantity)
                .storeCode(storeCode)
                .sku(sku.getSkuId())
                .updateType(updateType)
                .build();
    }
}
