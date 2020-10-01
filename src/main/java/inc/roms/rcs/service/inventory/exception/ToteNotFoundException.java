package inc.roms.rcs.service.inventory.exception;

import inc.roms.rcs.exception.EntityNotFoundException;
import inc.roms.rcs.service.issue.IssueFactory;
import inc.roms.rcs.service.issue.request.CreateIssueRequest;
import inc.roms.rcs.vo.tote.ToteId;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = true)
public class ToteNotFoundException extends EntityNotFoundException {

    private final ToteId toteId;

    public ToteNotFoundException(ToteId toteId) {
        super("Tote " + toteId + " not found!");
        this.toteId = toteId;
    }

    @Override
    public CreateIssueRequest toIssue(IssueFactory issueFactory) {
        return issueFactory.toteNotFound(toteId);
    }
}
