package inc.roms.rcs.service.operatorpanel.exception;

import inc.roms.rcs.api.error.model.RcsErrorCode;
import inc.roms.rcs.exception.BusinessException;
import inc.roms.rcs.exception.IssueCreatingBusinessException;
import inc.roms.rcs.service.issue.IssueFactory;
import inc.roms.rcs.service.issue.request.CreateIssueRequest;
import inc.roms.rcs.vo.tote.ToteId;
import inc.roms.rcs.vo.zones.ZoneId;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NotEnoughStoragePlacesForTote extends IssueCreatingBusinessException {
    private final ToteId toteId;
    private final ZoneId zoneId;

    public NotEnoughStoragePlacesForTote(ZoneId zoneId, ToteId toteId) {
        super("Not enough free places in " + zoneId + " to store "+ toteId);
        this.toteId = toteId;
        this.zoneId = zoneId;
    }

    @Override
    public HttpStatus httpStatus() {
        return HttpStatus.CONFLICT;
    }

    @Override
    public RcsErrorCode rcsErrorCode() {
        return RcsErrorCode.NO_SPACE_FOR_TOTE;
    }

    @Override
    public CreateIssueRequest toIssue(IssueFactory issueFactory) {
        return issueFactory.noEmptySpaceForTote(zoneId);
    }
}
