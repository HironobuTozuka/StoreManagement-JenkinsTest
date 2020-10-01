package inc.roms.rcs.service.omnichannel.kannart.model;

import inc.roms.rcs.vo.order.OrderId;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorReport {

    private ActionCode actionCode;
    private ActionDetails actionDetails;
    private OrderId orderNo;
    private ExitCode exitCode;
    private ExitMessage exitMessage;

}
