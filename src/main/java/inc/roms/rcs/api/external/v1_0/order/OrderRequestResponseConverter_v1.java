package inc.roms.rcs.api.external.v1_0.order;

import inc.roms.rcs.service.inventory.SkuService;
import inc.roms.rcs.service.inventory.exception.SkuNotFoundException;
import inc.roms.rcs.service.inventory.domain.model.Sku;
import inc.roms.rcs.vo.common.TransactionId;
import inc.roms.rcs.vo.order.OrderLineId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static inc.roms.rcs.api.external.v1_0.order.CreateOrderResponseDetails.details;
import static java.util.stream.Collectors.toList;

@Component
@RequiredArgsConstructor
class OrderRequestResponseConverter_v1 {

    private final SkuService skuService;

    public inc.roms.rcs.service.order.request.CreateOrderRequest toBaseRequest(CreateOrderRequest request) {
        inc.roms.rcs.service.order.request.CreateOrderRequest baseRequest = new inc.roms.rcs.service.order.request.CreateOrderRequest();
        baseRequest.setTransactionId(TransactionId.generate());
        baseRequest.setPickupTime(request.getPickupTime());
        baseRequest.setGateId(request.getGateId());
        baseRequest.setOrderId(request.getOrderId());
        baseRequest.setOrderType(request.getOrderType());
        baseRequest.setOrderLines(request.getOrderLines().stream().map(this::convert).collect(toList()));
        return baseRequest;
    }

    private inc.roms.rcs.service.order.request.OrderLineModel convert(OrderLineModel orderLineModel) {
        inc.roms.rcs.service.order.request.OrderLineModel baseModel = new inc.roms.rcs.service.order.request.OrderLineModel();
        Sku sku = skuService.findByExternalId(orderLineModel.getSkuId()).orElseThrow(() -> new SkuNotFoundException(orderLineModel.getSkuId()));
        baseModel.setSkuId(sku.getSkuId());
        baseModel.setOrderLineId(OrderLineId.generate());
        baseModel.setQuantity(orderLineModel.getQuantity());
        return baseModel;
    }

    public CreateOrderResponse convert(inc.roms.rcs.service.order.response.CreateOrderResponse createOrderResponse) {
        return CreateOrderResponse.createOrderResponse()
                .responseDetails(getDetails(createOrderResponse))
                .build();
    }

    private CreateOrderResponseDetails.Builder getDetails(inc.roms.rcs.service.order.response.CreateOrderResponse createOrderResponse) {
        CreateOrderResponseDetails.Builder builder = details()
                .rejectReason(createOrderResponse.getResponseDetails().getOrderRejectReason());
        if (createOrderResponse.getResponseDetails().getMissingSkuId() != null) {
            Sku sku = getMissingSku(createOrderResponse);
            builder.unknownSku(sku.getExternalId());
        }
        return builder
                .rejectedSkus(createOrderResponse.getResponseDetails().getRejectedSkus())
                .gateId(createOrderResponse.getResponseDetails().getGateId())
                .eta(createOrderResponse.getResponseDetails().getEta());
    }

    private Sku getMissingSku(inc.roms.rcs.service.order.response.CreateOrderResponse createOrderResponse) {
        return skuService.getReadySku(createOrderResponse.getResponseDetails().getMissingSkuId());
    }

    public inc.roms.rcs.service.order.request.DeliverOrderRequest toBaseRequest(DeliverOrderRequest request) {
        inc.roms.rcs.service.order.request.DeliverOrderRequest deliverOrderRequest = new inc.roms.rcs.service.order.request.DeliverOrderRequest();
        deliverOrderRequest.setGateId(request.getGateId());
        deliverOrderRequest.setOrderId(request.getOrderId());
        deliverOrderRequest.setTransactionId(TransactionId.generate());
        return deliverOrderRequest;
    }
}
