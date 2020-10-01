package inc.roms.rcs.service.issue.request;

import inc.roms.rcs.vo.issue.IssueId;
import inc.roms.rcs.vo.issue.IssueStatus;
import inc.roms.rcs.vo.issue.Notes;
import lombok.Data;

@Data
public class IssueActionRequest {

    private final IssueId issueId;
    private final IssueStatus issueStatus;
    private final Notes notes;

}
