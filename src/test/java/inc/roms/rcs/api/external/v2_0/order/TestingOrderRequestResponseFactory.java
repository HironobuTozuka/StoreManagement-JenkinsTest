package inc.roms.rcs.api.external.v2_0.order;

import inc.roms.rcs.api.external.v2_0.vo.AcceptCode;
import inc.roms.rcs.service.order.request.BatchOrderActionRequest;
import inc.roms.rcs.service.order.response.CreateOrderResponseDetails;
import inc.roms.rcs.vo.common.Quantity;
import inc.roms.rcs.vo.common.StoreId;
import inc.roms.rcs.vo.common.TransactionId;
import inc.roms.rcs.vo.location.GateId;
import inc.roms.rcs.vo.order.OrderId;
import inc.roms.rcs.vo.order.OrderLineId;
import inc.roms.rcs.vo.order.OrderType;
import inc.roms.rcs.vo.sku.SkuId;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import static inc.roms.rcs.api.external.v2_0.vo.JapanTimeHelper.japan;

public class TestingOrderRequestResponseFactory {

    public static final GateId GATE_ID = GateId.from("GATE_1");
    public static final OrderId ORDER_ID = OrderId.generate();
    public static final ZonedDateTime ORDER_TIME = ZonedDateTime.of(2020, 1, 10, 10, 10, 10, 0, japan());
    public static final ZonedDateTime PICKUP_TIME = ZonedDateTime.of(2020, 1, 10, 10, 15, 10, 0, japan());

    public static final TransactionId TRANSACTION_ID = TransactionId.generate();
    public static final OrderLineId ORDER_LINE_ID = OrderLineId.generate();
    public static final SkuId SKU_1 = SkuId.from("SKU_1");
    public static final Quantity QUANTITY = Quantity.of(10);
    public static final LocalDateTime ETA = LocalDateTime.of(2020, 1, 1, 10, 10, 10);
    public static final Instant NOW = LocalDateTime.of(2019, 1, 1, 10, 10, 10).toInstant(ZoneOffset.UTC);
    public static final StoreId STORE_CODE = StoreId.from("POC");


    public CreateOrderResponse createExpectedOrderResponse() {
        return CreateOrderResponse.builder()
                .gate(GATE_ID)
                .estimatedCompletionTime(ETA.atZone(japan()))
                .acceptCode(AcceptCode.SUCCESS)
                .receiveTime(NOW.atZone(japan()))
                .storeCode(STORE_CODE)
                .build();
    }

    public inc.roms.rcs.service.order.response.CreateOrderResponse createBaseOrderResponse() {
        return inc.roms.rcs.service.order.response.CreateOrderResponse.createOrderResponse()
                .responseDetails(CreateOrderResponseDetails.details()
                        .eta(ETA)
                        .gateId(GATE_ID)
                ).build();
    }

    public CreateOrderRequest createOrderRequest() {
        CreateOrderRequest createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setGate(GATE_ID);
        createOrderRequest.setOrderNo(ORDER_ID);
        createOrderRequest.setOrderType(OrderType.ORDER);
        createOrderRequest.setOrderTime(ORDER_TIME);
        createOrderRequest.setPickupTime(PICKUP_TIME);
        createOrderRequest.setTransactionId(TRANSACTION_ID);
        OrderLineModel orderLine = new OrderLineModel();
        orderLine.setOrderLineNo(ORDER_LINE_ID);
        orderLine.setSku(SKU_1);
        orderLine.setQuantity(QUANTITY);
        createOrderRequest.setOrderLines(List.of(orderLine));
        return createOrderRequest;
    }

    public inc.roms.rcs.service.order.request.CreateOrderRequest createBaseOrderRequest() {
        inc.roms.rcs.service.order.request.CreateOrderRequest createOrderRequest = new inc.roms.rcs.service.order.request.CreateOrderRequest();
        createOrderRequest.setGateId(GATE_ID);
        createOrderRequest.setOrderId(ORDER_ID);
        createOrderRequest.setOrderType(OrderType.ORDER);
        createOrderRequest.setPickupTime(convertToUtc());
        inc.roms.rcs.service.order.request.OrderLineModel orderLine = new inc.roms.rcs.service.order.request.OrderLineModel();
        orderLine.setOrderLineId(ORDER_LINE_ID);
        orderLine.setSkuId(SKU_1);
        orderLine.setQuantity(QUANTITY);
        createOrderRequest.setOrderLines(List.of(orderLine));
        createOrderRequest.setTransactionId(TRANSACTION_ID);
        return createOrderRequest;
    }

    private LocalDateTime convertToUtc() {
        return LocalDateTime.ofInstant(PICKUP_TIME.toInstant(), ZoneOffset.UTC);
    }

    public OrdersActionRequest_v2 ordersActionRequest() {
        OrdersActionRequest_v2 ordersActionRequestV2 = new OrdersActionRequest_v2();
        ordersActionRequestV2.setOrderNos(List.of(ORDER_ID));
        ordersActionRequestV2.setTransactionId(TransactionId.generate());
        return ordersActionRequestV2;
    }

    public BatchOrderActionRequest createBatchOrderActionRequest(TransactionId transactionId) {
        BatchOrderActionRequest batchOrderActionRequest = new BatchOrderActionRequest();
        batchOrderActionRequest.setOrderIds(List.of(ORDER_ID));
        batchOrderActionRequest.setTransactionId(transactionId);
        return batchOrderActionRequest;
    }
}
