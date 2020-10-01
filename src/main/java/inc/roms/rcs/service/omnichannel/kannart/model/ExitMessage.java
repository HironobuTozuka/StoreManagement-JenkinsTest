package inc.roms.rcs.service.omnichannel.kannart.model;

import inc.roms.rcs.vo.issue.IssueId;
import inc.roms.rcs.vo.issue.IssueReason;
import inc.roms.rcs.vo.sku.SkuId;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExitMessage {

    private IssueId issueId;
    private IssueReason errorCode;
    private SkuId sku;

}
