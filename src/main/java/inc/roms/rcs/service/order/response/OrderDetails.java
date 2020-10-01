package inc.roms.rcs.service.order.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import inc.roms.rcs.service.inventory.response.OrderLineDetails;
import inc.roms.rcs.service.order.domain.model.Order;
import inc.roms.rcs.service.order.domain.model.OrderLine;
import inc.roms.rcs.vo.common.UserId;
import inc.roms.rcs.vo.location.GateId;
import inc.roms.rcs.vo.order.OrderId;
import inc.roms.rcs.vo.order.OrderLineId;
import inc.roms.rcs.vo.order.OrderStatus;
import inc.roms.rcs.vo.order.OrderType;
import inc.roms.rcs.vo.tote.ToteId;
import lombok.Data;
import lombok.Getter;

import static java.util.stream.Collectors.*;

@Data
public class OrderDetails {
    private final OrderId orderId;
    private final OrderType orderType;
    private final LocalDateTime pickupTime;
    private final GateId gate;
    private final UserId userId;
    private final List<OrderLineDetails> orderLines;
    private final OrderStatus orderStatus;
    private final List<ToteId> deliveryTotes;

    @Getter
    public static class Builder {

        private final OrderId orderId;
        private final OrderType orderType;
        private final LocalDateTime pickupTime;
        private final GateId gate;
        private final UserId userId;
        private final Map<OrderLineId, OrderLineDetails.Builder> orderLineBuilders;
        private final OrderStatus orderStatus;
        private List<ToteId> deliveryTotes;

        public Builder(Order order) {
            this.orderId = order.getOrderId();
            this.orderType = order.getOrderType();
            this.pickupTime = order.getPickupTime();
            this.gate = order.getGate();
            this.userId = order.getUserId();
            this.orderStatus = order.getOrderStatus();
            this.orderLineBuilders = order.getOrderLines()
                    .stream()
                    .collect(toMap(OrderLine::getOrderLineId, OrderLineDetails::builder));
        }

        public Builder deliveryTotes(List<ToteId> deliveryTotes) {
            this.deliveryTotes = deliveryTotes;
            return this;
        }

        public OrderDetails build() {
            return new OrderDetails(
                    orderId,
                    orderType,
                    pickupTime,
                    gate,
                    userId,
                    orderLineBuilders.values().stream().map(OrderLineDetails.Builder::build).collect(toList()),
                    orderStatus,
                    deliveryTotes
            );
        }

        public Builder storageTotes(OrderLineId orderLineId, List<ToteId> toteIds) {
            this.orderLineBuilders.get(orderLineId).storageTotes(toteIds);
            return this;
        }
    }
}
