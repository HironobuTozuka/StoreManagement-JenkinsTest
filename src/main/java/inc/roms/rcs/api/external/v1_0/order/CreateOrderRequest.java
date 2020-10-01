package inc.roms.rcs.api.external.v1_0.order;

import inc.roms.rcs.vo.common.UserId;
import inc.roms.rcs.vo.location.GateId;
import inc.roms.rcs.vo.order.OrderId;
import inc.roms.rcs.vo.order.OrderType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
class CreateOrderRequest {

    private OrderId orderId;
    private OrderType orderType;
    private LocalDateTime pickupTime;
    private GateId gateId;
    private UserId userId;
    private List<OrderLineModel> orderLines;

}
