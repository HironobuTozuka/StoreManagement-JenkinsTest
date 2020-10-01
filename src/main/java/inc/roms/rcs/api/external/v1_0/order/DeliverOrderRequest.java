package inc.roms.rcs.api.external.v1_0.order;

import inc.roms.rcs.vo.location.GateId;
import inc.roms.rcs.vo.order.OrderId;
import lombok.Data;

@Data
public class DeliverOrderRequest {
    private final OrderId orderId;
    private final GateId gateId;
}
