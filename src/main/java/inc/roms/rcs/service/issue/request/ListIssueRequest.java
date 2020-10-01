package inc.roms.rcs.service.issue.request;

import inc.roms.rcs.vo.issue.IssueAction;
import inc.roms.rcs.vo.issue.IssueReason;
import inc.roms.rcs.vo.issue.IssueStatus;
import inc.roms.rcs.vo.order.OrderId;
import inc.roms.rcs.vo.sku.SkuId;
import inc.roms.rcs.vo.tote.ToteId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListIssueRequest {

    private IssueAction issueAction;
    private LocalDateTime issueDeadline;
    private IssueStatus issueStatus;
    private IssueReason issueReason;
    private ToteId toteId;
    private OrderId orderId;
    private SkuId skuId;

}
