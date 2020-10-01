package inc.roms.rcs.api.external.v1_0.order;

import inc.roms.rcs.service.inventory.exception.SkuNotFoundException;
import inc.roms.rcs.service.order.OrderManagementService;
import inc.roms.rcs.service.order.request.ListOrderRequest;
import inc.roms.rcs.service.order.request.OrderActionRequest;
import inc.roms.rcs.service.order.response.DeliverOrderResponse;
import inc.roms.rcs.service.order.response.ListOrderResponse;
import inc.roms.rcs.service.order.response.OrderActionResponse;
import inc.roms.rcs.service.order.response.OrderRejectReason;
import inc.roms.rcs.vo.order.OrderId;
import inc.roms.rcs.vo.order.OrderStatus;
import inc.roms.rcs.vo.sku.SkuId;
import inc.roms.rcs.vo.tote.ToteId;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static inc.roms.rcs.api.external.v1_0.order.CreateOrderResponse.createOrderResponse;
import static inc.roms.rcs.api.external.v1_0.order.CreateOrderResponseDetails.details;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OrderManagementController_v1 {

    private final OrderManagementService orderService;
    private final OrderRequestResponseConverter_v1 converter;

    @PostMapping("/api/1.0/order:create")
    public CreateOrderResponse create(@RequestBody CreateOrderRequest request) {
        try {
            log.info("CreateOrderRequest: {}", request);
            CreateOrderResponse createOrderResponse = converter.convert(orderService.create(converter.toBaseRequest(request)));
            log.info("CreateOrderResponse: {}", createOrderResponse);
            return createOrderResponse;
        } catch (SkuNotFoundException snfe) {
            return createOrderResponse().responseDetails(details().rejectReason(OrderRejectReason.SKU_NOT_FOUND).unknownSku(snfe.getExternalId())).build();
        }
    }

    @PostMapping("/api/1.0/order:deliver")
    public DeliverOrderResponse deliver(@RequestBody DeliverOrderRequest request) {
        log.info("DeliverOrderRequest: {}", request);
        DeliverOrderResponse deliver = orderService.deliver(converter.toBaseRequest(request));
        log.info("DeliverOrderResponse: {}", deliver);
        return deliver;
    }

    @PostMapping("/api/1.0/order:pick")
    public OrderActionResponse picks(@RequestBody OrderActionRequest request) {
        log.info("pick action: OrderActionRequest: {}", request);
        OrderActionResponse orderActionResponse = orderService.pickOrder(request.getOrderId());
        log.info("pick action: OrderActionResponse: {}", orderActionResponse);
        return orderActionResponse;
    }

    @PostMapping("/api/1.0/order:cancel")
    public OrderActionResponse cancel(@RequestBody OrderActionRequest request) {
        log.info("cancel action: OrderActionRequest: {}", request);
        OrderActionResponse orderActionResponse = orderService.cancel(request);
        log.info("cancel action: OrderActionResponse: {}", orderActionResponse);
        return orderActionResponse;
    }

    @GetMapping("/api/1.0/order:list")
    public ListOrderResponse list(ListRequestParameters request) {
        log.info("list orders: {}", request);
        ListOrderResponse listOrderResponse = orderService.list(request.toRequest());
        log.info("list orders response, size: {}", listOrderResponse.getOrders().size());
        return listOrderResponse;
    }

    @Data
    public static class ListRequestParameters {

        private SkuId skuId;
        private OrderId orderId;
        private ToteId storageToteId;
        private ToteId deliveryToteId;
        private OrderStatus orderStatus;

        public ListOrderRequest toRequest() {
            return ListOrderRequest.builder()
            .orderId(orderId)
            .skuId(skuId)
            .deliveryToteId(deliveryToteId)
            .orderStatus(orderStatus)
            .storageToteId(storageToteId)
            .build();
        }

    }

}
