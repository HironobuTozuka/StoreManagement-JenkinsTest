package inc.roms.rcs.api.external.v2_0.order;

import inc.roms.rcs.api.external.v2_0.vo.*;
import inc.roms.rcs.service.order.request.BatchOrderActionRequest;
import inc.roms.rcs.service.order.response.BatchOrderActionResponse;
import inc.roms.rcs.service.order.response.CreateOrderResponseDetails;
import inc.roms.rcs.vo.common.StoreId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

import static inc.roms.rcs.api.external.v2_0.vo.AcceptCode.ERROR;
import static inc.roms.rcs.api.external.v2_0.vo.AcceptCode.SUCCESS;
import static inc.roms.rcs.api.external.v2_0.vo.JapanTimeHelper.japan;
import static inc.roms.rcs.api.external.v2_0.vo.JapanTimeHelper.nowInJapan;
import static java.util.stream.Collectors.toList;

@Component
final class OrderRequestResponseConverter_v2 {

    private final StoreId storeCode;

    OrderRequestResponseConverter_v2(@Value("${rcs.store.code:POC}") StoreId storeCode) {
        this.storeCode = storeCode;
    }

    inc.roms.rcs.service.order.request.CreateOrderRequest toBaseRequest(CreateOrderRequest createOrderRequest) {
        inc.roms.rcs.service.order.request.CreateOrderRequest baseRequest = new inc.roms.rcs.service.order.request.CreateOrderRequest();
        baseRequest.setTransactionId(createOrderRequest.getTransactionId());
        baseRequest.setPickupTime(JapanTimeHelper.toUtc(createOrderRequest.getPickupTime()));
        baseRequest.setGateId(createOrderRequest.getGate());
        baseRequest.setOrderId(createOrderRequest.getOrderNo());
        baseRequest.setOrderType(createOrderRequest.getOrderType());
        baseRequest.setOrderLines(createOrderRequest.getOrderLines().stream().map(this::convert).collect(toList()));
        baseRequest.validate();
        return baseRequest;
    }

    private inc.roms.rcs.service.order.request.OrderLineModel convert(OrderLineModel orderLineModel) {
        inc.roms.rcs.service.order.request.OrderLineModel baseModel = new inc.roms.rcs.service.order.request.OrderLineModel();
        baseModel.setSkuId(orderLineModel.getSku());
        baseModel.setQuantity(orderLineModel.getQuantity());
        baseModel.setOrderLineId(orderLineModel.getOrderLineNo());
        return baseModel;
    }

    CreateOrderResponse convert(inc.roms.rcs.service.order.response.CreateOrderResponse createOrderResponse) {
        return CreateOrderResponse.builder()
                .acceptCode(AcceptCode.from(createOrderResponse.getResponseCode()))
                .receiveTime(nowInJapan())
                .estimatedCompletionTime(convertEta(createOrderResponse))
                .gate(createOrderResponse.getResponseDetails().getGateId())
                .storeCode(storeCode)
                .acceptMessage(convert(createOrderResponse.getResponseDetails())).build();
    }

    private ZonedDateTime convertEta(inc.roms.rcs.service.order.response.CreateOrderResponse createOrderResponse) {
        if (createOrderResponse.getResponseDetails().getEta() == null) return null;
        return createOrderResponse.getResponseDetails().getEta().atZone(japan());
    }

    private AcceptMessage convert(CreateOrderResponseDetails responseDetails) {
        AcceptMessage acceptMessage = AcceptMessage.builder().build();
        if (acceptMessage.isEmpty()) return null;
        return acceptMessage;
    }

    BatchOrderActionRequest toBatchActionRequest(OrdersActionRequest_v2 ordersActionRequestV2) {
        BatchOrderActionRequest batchOrderActionRequest = new BatchOrderActionRequest();
        batchOrderActionRequest.setOrderIds(ordersActionRequestV2.getOrderNos());
        batchOrderActionRequest.setTransactionId(ordersActionRequestV2.getTransactionId());
        return batchOrderActionRequest;
    }

    OrdersActionResponse convert(BatchOrderActionResponse batchOrderActionResponse) {
        OrdersActionResponse orderActionResponse = new OrdersActionResponse();
        orderActionResponse.setReceiveTime(nowInJapan());
        orderActionResponse.setStoreCode(storeCode);
        if (batchOrderActionResponse.getFailed() == null || batchOrderActionResponse.getFailed().isEmpty() ) {
            orderActionResponse.setAcceptCode(SUCCESS);
        } else {
            orderActionResponse.setAcceptCode(ERROR);
            orderActionResponse.setAcceptMessage(OrderActionAcceptMessage.builder()
                    .errorCode(OrderActionRejectReason.ORDER_NOT_FOUND)
                    .missingOrderNos(batchOrderActionResponse.getFailed())
                    .build());
        }
        return orderActionResponse;
    }

    inc.roms.rcs.service.order.request.DeliverOrderRequest convert(DeliverOrderRequest request) {
        inc.roms.rcs.service.order.request.DeliverOrderRequest deliverOrderRequest = new inc.roms.rcs.service.order.request.DeliverOrderRequest();
        deliverOrderRequest.setTransactionId(request.getTransactionId());
        deliverOrderRequest.setOrderId(request.getOrderNo());
        deliverOrderRequest.setGateId(request.getGate());
        return deliverOrderRequest;
    }

    DeliverOrderResponse convert(inc.roms.rcs.service.order.response.DeliverOrderResponse response, DeliverOrderRequest request) {
        DeliverOrderResponse deliverOrderResponse = new DeliverOrderResponse();
        deliverOrderResponse.setAcceptCode(AcceptCode.from(response.getResponseCode()));
        deliverOrderResponse.setGate(request.getGate());
        deliverOrderResponse.setReceiveTime(nowInJapan());
        deliverOrderResponse.setStoreCode(storeCode);
        deliverOrderResponse.setAcceptMessage(null);
        return deliverOrderResponse;
    }
}
