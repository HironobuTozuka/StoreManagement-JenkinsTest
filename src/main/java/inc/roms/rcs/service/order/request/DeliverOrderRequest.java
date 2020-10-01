package inc.roms.rcs.service.order.request;

import inc.roms.rcs.vo.common.TransactionId;
import inc.roms.rcs.vo.location.GateId;
import inc.roms.rcs.vo.order.OrderId;
import lombok.Data;

@Data
public class DeliverOrderRequest {

    private OrderId orderId;
    private GateId gateId;
    private TransactionId transactionId;

}
