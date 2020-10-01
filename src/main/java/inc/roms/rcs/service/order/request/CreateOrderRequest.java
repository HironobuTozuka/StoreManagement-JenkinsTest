package inc.roms.rcs.service.order.request;

import inc.roms.rcs.vo.common.TransactionId;
import inc.roms.rcs.vo.common.UserId;
import inc.roms.rcs.vo.location.GateId;
import inc.roms.rcs.vo.order.OrderId;
import inc.roms.rcs.vo.order.OrderType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Data
public class CreateOrderRequest {

    private TransactionId transactionId;
    private OrderId orderId;
    private OrderType orderType;
    private LocalDateTime pickupTime;
    private GateId gateId;
    private UserId userId;
    private List<OrderLineModel> orderLines;

    public void validate() {
        if(Objects.isNull(transactionId)) throw new IllegalStateException();
        if(Objects.isNull(orderId)) throw new IllegalStateException();
        if(Objects.isNull(orderType)) throw new IllegalStateException();
        if(Objects.isNull(pickupTime) && orderType.equals(OrderType.PREORDER)) throw new IllegalStateException();
        if(Objects.isNull(gateId) && orderType.equals(OrderType.ORDER)) throw new IllegalStateException();
        if(Objects.isNull(orderLines)) throw new IllegalStateException();
        orderLines.forEach(OrderLineModel::validate);
    }

}
