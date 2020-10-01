package inc.roms.rcs.api.external.v2_0.builders;

import inc.roms.rcs.api.external.v2_0.order.CreateOrderRequest;
import inc.roms.rcs.vo.common.TransactionId;
import inc.roms.rcs.vo.location.GateId;
import inc.roms.rcs.vo.order.OrderId;
import inc.roms.rcs.vo.order.OrderType;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;


public class CreateOrderRequestBuilder {
    private TransactionId transactionId;

    private OrderId orderNo;

    private OrderType orderType;

    private ZonedDateTime orderTime;

    private ZonedDateTime pickupTime;

    private GateId gate;

    private List<OrderLineModelBuilder> orderLines = new ArrayList<>();

    public static CreateOrderRequestBuilder order() {
        return new CreateOrderRequestBuilder();
    }

    public static CreateOrderRequestBuilder randomOrder() {
        return new CreateOrderRequestBuilder()
                .transactionId(TransactionId.generate())
                .orderNo(OrderId.generate())
                .pickupTime(ZonedDateTime.now().plus(1, ChronoUnit.DAYS))
                .orderTime(ZonedDateTime.now())
                .orderType(OrderType.ORDER)
                .gate(GateId.from("c_gate_001"))
                .orderLine(OrderLineModelBuilder.randomOrderLine())
                ;
    }

    public CreateOrderRequest build() {
        CreateOrderRequest createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setGate(gate);
        createOrderRequest.setOrderLines(orderLines.stream().map(OrderLineModelBuilder::build).collect(toList()));
        createOrderRequest.setOrderNo(orderNo);
        createOrderRequest.setOrderTime(orderTime);
        createOrderRequest.setPickupTime(pickupTime);
        createOrderRequest.setOrderType(orderType);
        createOrderRequest.setTransactionId(transactionId);
        return createOrderRequest;
    }

    public CreateOrderRequestBuilder transactionId(TransactionId transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public CreateOrderRequestBuilder orderNo(OrderId orderNo) {
        this.orderNo = orderNo;
        return this;
    }

    public CreateOrderRequestBuilder orderType(OrderType orderType) {
        this.orderType = orderType;
        return this;
    }

    public CreateOrderRequestBuilder orderTime(ZonedDateTime orderTime) {
        this.orderTime = orderTime;
        return this;
    }

    public CreateOrderRequestBuilder pickupTime(ZonedDateTime pickupTime) {
        this.pickupTime = pickupTime;
        return this;
    }

    public CreateOrderRequestBuilder gate(GateId gate) {
        this.gate = gate;
        return this;
    }

    public CreateOrderRequestBuilder orderLine(OrderLineModelBuilder orderLine) {
        orderLines.add(orderLine);
        return this;
    }

    public CreateOrderRequestBuilder orderLines(List<OrderLineModelBuilder> orderLines) {
        this.orderLines = orderLines;
        return this;
    }
}
