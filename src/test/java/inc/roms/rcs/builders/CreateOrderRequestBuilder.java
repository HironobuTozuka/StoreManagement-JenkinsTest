package inc.roms.rcs.builders;

import inc.roms.rcs.service.order.request.CreateOrderRequest;
import inc.roms.rcs.vo.common.TransactionId;
import inc.roms.rcs.vo.common.UserId;
import inc.roms.rcs.vo.location.GateId;
import inc.roms.rcs.vo.order.OrderId;
import inc.roms.rcs.vo.order.OrderType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class CreateOrderRequestBuilder {
    private OrderId orderId;
    private GateId gateId;
    private OrderType orderType;
    private UserId userId;
    private TransactionId transactionId;
    private List<OrderLineModelBuilder> orderLineBuilders = new ArrayList<>();

    public static CreateOrderRequestBuilder orderRequest() {
        return new CreateOrderRequestBuilder();
    }

    public CreateOrderRequestBuilder orderId(OrderId order) {
        this.orderId = order;
        return this;
    }

    public CreateOrderRequestBuilder gateId(GateId gate) {
        this.gateId = gate;
        return this;
    }

    public CreateOrderRequestBuilder orderType(OrderType orderType) {
        this.orderType = orderType;
        return this;
    }

    public CreateOrderRequestBuilder userId(UserId user) {
        this.userId = user;
        return this;
    }

    public CreateOrderRequestBuilder orderLines(OrderLineModelBuilder... builders) {
        orderLineBuilders.addAll(Arrays.asList(builders));
        return this;
    }

    public CreateOrderRequestBuilder transactionId(TransactionId transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public CreateOrderRequest build() {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setOrderLines(orderLineBuilders.stream().map(OrderLineModelBuilder::build).collect(toList()));
        request.setUserId(userId);
        request.setOrderType(orderType);
        request.setGateId(gateId);
        request.setOrderId(orderId);
        return request;
    }
}
