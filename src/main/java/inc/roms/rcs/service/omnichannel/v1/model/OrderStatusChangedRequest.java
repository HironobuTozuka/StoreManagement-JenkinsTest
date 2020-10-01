package inc.roms.rcs.service.omnichannel.v1.model;

import inc.roms.rcs.vo.order.OrderId;
import inc.roms.rcs.vo.order.OrderStatus;
import lombok.Data;

import java.util.List;

@Data
public class OrderStatusChangedRequest {

    private OrderId orderId;
    private OrderStatus status;
    private List<OrderLineState> orderLines;

}
