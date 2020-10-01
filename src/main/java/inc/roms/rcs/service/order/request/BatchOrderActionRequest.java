package inc.roms.rcs.service.order.request;

import inc.roms.rcs.vo.common.TransactionId;
import inc.roms.rcs.vo.order.OrderId;
import lombok.Data;

import java.util.List;

@Data
public class BatchOrderActionRequest {

    private TransactionId transactionId;
    private List<OrderId> orderIds;

}
