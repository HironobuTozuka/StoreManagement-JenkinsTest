package inc.roms.rcs.service.order.request;

import inc.roms.rcs.vo.order.OrderId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderActionRequest {
    private OrderId orderId;
}
