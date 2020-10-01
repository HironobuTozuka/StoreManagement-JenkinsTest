package inc.roms.rcs.exception;

import inc.roms.rcs.service.issue.IssueFactory;
import inc.roms.rcs.service.issue.request.CreateIssueRequest;

public interface ConvertibleToIssue {

    CreateIssueRequest toIssue(IssueFactory issueFactory);

}
