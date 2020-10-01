package inc.roms.rcs.service.omnichannel.kannart.model;

import inc.roms.rcs.vo.common.RcsOperationId;
import inc.roms.rcs.vo.common.StoreId;
import inc.roms.rcs.vo.common.TransactionId;
import inc.roms.rcs.vo.order.OrderId;
import inc.roms.rcs.vo.order.OrderStatus;
import lombok.Builder;
import lombok.Data;
import org.apache.logging.log4j.message.ExitMessage;

import java.util.List;

@Data
@Builder
public class OrderStatusChangedRequest {

    private OrderId orderNo;
    private TransactionId transactionId;
    private RcsOperationId rcsOperationId;
    private StoreId storeCode;

    private ActionCode actionCode;
    private ActionDetails actionDetails;

    private ExitCode exitCode;
    private ExitMessage exitMessage;

}
