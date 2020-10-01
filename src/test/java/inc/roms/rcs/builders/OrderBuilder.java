package inc.roms.rcs.builders;

import inc.roms.rcs.service.order.domain.model.Order;
import inc.roms.rcs.vo.common.UserId;
import inc.roms.rcs.vo.location.GateId;
import inc.roms.rcs.vo.order.OrderId;
import inc.roms.rcs.vo.order.OrderStatus;
import inc.roms.rcs.vo.order.OrderType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static inc.roms.rcs.vo.order.OrderId.*;
import static java.util.stream.Collectors.toList;

public class OrderBuilder {

    private OrderId orderId;
    private OrderType orderType;
    private LocalDateTime pickupTime;
    private GateId gate;
    private UserId userId;
    private List<OrderLineBuilder> orderLines = new ArrayList<>();
    private OrderStatus orderStatus;

    public static OrderBuilder order() {
        return new OrderBuilder().orderId(generate());
    }

    public OrderBuilder orderId(OrderId orderId) {
        this.orderId = orderId;
        return this;
    }

    public OrderBuilder orderType(OrderType orderType) {
        this.orderType = orderType;
        return this;
    }

    public OrderBuilder pickupTime(LocalDateTime pickupTime) {
        this.pickupTime = pickupTime;
        return this;
    }

    public OrderBuilder gate(GateId gate) {
        this.gate = gate;
        return this;
    }

    public OrderBuilder userId(UserId userId) {
        this.userId = userId;
        return this;
    }

    public OrderBuilder orderLines(OrderLineBuilder... orderLines) {
        this.orderLines = Arrays.asList(orderLines);
        return this;
    }

    public OrderBuilder orderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
        return this;
    }

    public Order build() {
        Order order = new Order();
        order.setOrderStatus(orderStatus);
        order.setUserId(userId);
        order.setPickupTime(pickupTime);
        order.setOrderLines(orderLines.stream().map(OrderLineBuilder::build).collect(toList()));
        order.setOrderId(orderId);
        order.setGate(gate);
        order.setOrderType(orderType);
        return order;
    }
}
