package inc.roms.rcs.service.issue.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import inc.roms.rcs.vo.issue.*;
import inc.roms.rcs.vo.order.OrderId;
import inc.roms.rcs.vo.tote.ToteId;
import lombok.Data;

import java.time.ZonedDateTime;

import static inc.roms.rcs.api.external.v2_0.vo.JapanTimeHelper.DATETIME_PATTERN;

@Data
public class IssueDetails {
    private final IssueId issueId;
    private final IssueAction issueAction;

    @JsonFormat(pattern = DATETIME_PATTERN)
    private final ZonedDateTime issueDeadline;
    private final IssueStatus issueStatus;
    private final IssueReason reason;
    private final ToteId toteId;
    private final OrderId orderId;
    private final Notes notes;
}
