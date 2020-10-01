package inc.roms.rcs.service.inventory.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import inc.roms.rcs.service.inventory.domain.model.ScheduledSupply;
import inc.roms.rcs.service.order.response.DeliverOrderRejectReason;
import inc.roms.rcs.vo.common.ResponseCode;
import inc.roms.rcs.vo.issue.IssueId;
import inc.roms.rcs.vo.issue.IssueReason;
import lombok.Builder;
import lombok.Data;
import org.flywaydb.core.api.ErrorCode;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@Builder
public class ScheduleSupplyResponseDetails {

    @JsonInclude(NON_NULL)
    private ScheduledSupply scheduledSupply;
}
