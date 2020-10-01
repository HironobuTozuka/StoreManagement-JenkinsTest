package inc.roms.rcs.service.omnichannel.v1;

import com.google.common.base.Strings;
import inc.roms.rcs.service.inventory.SkuService;
import inc.roms.rcs.service.inventory.domain.model.ScheduledSupplyItem;
import inc.roms.rcs.service.inventory.domain.model.Sku;
import inc.roms.rcs.service.inventory.domain.model.SkuBatch;
import inc.roms.rcs.service.omnichannel.OmniChannelService;
import inc.roms.rcs.service.omnichannel.v1.model.OrderLineState;
import inc.roms.rcs.service.omnichannel.v1.model.OrderStatusChangedRequest;
import inc.roms.rcs.service.omnichannel.v1.model.UpdateInventoryRequest;
import inc.roms.rcs.service.order.domain.model.Order;
import inc.roms.rcs.service.order.domain.model.OrderLine;
import inc.roms.rcs.service.task.domain.model.TaskUpdateRequest;
import inc.roms.rcs.vo.common.Quantity;
import inc.roms.rcs.vo.common.StoreId;
import inc.roms.rcs.vo.common.UserId;
import inc.roms.rcs.vo.order.OrderType;
import inc.roms.rcs.vo.sku.SkuId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Objects;

import static inc.roms.rcs.service.omnichannel.OmniChannelProfiles.NOT_COMMERCE_PLATFORM;
import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@Profile(NOT_COMMERCE_PLATFORM)
@RequiredArgsConstructor
@Primary
public class OmniChannel3EService implements OmniChannelService {

    private final OmniChannel3EClient omniChannel3EClient;
    private final SkuService skuService;

    @Override
    public void updateInventory(Map<SkuId, Quantity> skuDiff) {
        log.info("Sending update inventory with sku diff: {}", skuDiff);
        skuDiff.entrySet().stream()
                .filter(it -> Objects.nonNull(it.getKey()))
                .filter(it -> !Strings.isNullOrEmpty(it.getKey().getSkuId()))
                .map(it -> toUpdateInventoryRequest(it.getKey(), it.getValue()))
                .forEach(this::updateInventory);
    }

    public void updateInventory(UpdateInventoryRequest updateInventoryRequest) {
        if(updateInventoryRequest.getDelta() != null && !updateInventoryRequest.getDelta().equals(Quantity.of(0))) {
            log.info("UpdateInventoryRequest: {}", updateInventoryRequest);
            omniChannel3EClient.send(updateInventoryRequest);
        }
    }

    @Override
    public void orderStatusChanged(Order order) {
        log.info("Sending report for order: {}", order.getOrderId());
        OrderStatusChangedRequest request = convert(order);
        log.info("Request to be sent: {}", request);
        omniChannel3EClient.send(request);
    }

    @Override
    public void updateInventory(SkuBatch skuBatch, TaskUpdateRequest taskUpdateRequest, OrderType orderType) {
        log.info("No action required in case of integration with 3e");
    }

    @Override
    public void updateInventory(ScheduledSupplyItem supplyItem, Quantity quantity) {
        log.info("No action required in case of integration with 3e");
    }

    @Override
    public void updateInventory(SkuId skuId, Quantity quantity) {

    }

    @Override
    public void skuUpdated(Sku it) {

    }

    @Override
    public void removeInventory(SkuBatch skuBatch) {
        this.updateInventory(Map.of(skuBatch.getSkuId(), skuBatch.getQuantity().multiply(-1L)));
    }

    @Override
    public void addInventory(SkuBatch skuBatch) {
        this.updateInventory(Map.of(skuBatch.getSkuId(), skuBatch.getQuantity()));
    }

    private OrderStatusChangedRequest convert(Order order) {
        OrderStatusChangedRequest request = new OrderStatusChangedRequest();

        request.setOrderId(order.getOrderId());
        request.setStatus(order.getOrderStatus());
        request.setOrderLines(order.getOrderLines().stream().map(this::toOrderLineState).collect(toList()));

        return request;
    }

    private OrderLineState toOrderLineState(OrderLine orderLine) {
        OrderLineState orderLineState = new OrderLineState();
        Sku sku = skuService.getReadySku(orderLine.getSkuId());
        orderLineState.setSkuId(sku.getExternalId());
        orderLineState.setRequestedQuantity(orderLine.getQuantity() != null ? orderLine.getQuantity() : Quantity.of(0));
        orderLineState.setFailedQuantity(orderLine.getFailed() != null ? orderLine.getFailed() : Quantity.of(0));
        orderLineState.setPickedQuantity(orderLine.getPicked() != null ? orderLine.getPicked() : Quantity.of(0));
        return orderLineState;
    }

    private UpdateInventoryRequest toUpdateInventoryRequest(SkuId skuId, Quantity quantity) {
        Sku sku = skuService.getReadySku(skuId);
        return UpdateInventoryRequest.builder()
                .delta(quantity)
                .storeId(StoreId.from("2de9a0c3-4b21-407c-83d1-031ea0735eb3"))
                .updateDate(LocalDateTime.now(ZoneOffset.UTC))
                .skuId(sku.getExternalId())
                .operatorId(UserId.from("LoadingGateOperator"))
                .build();
    }

}
