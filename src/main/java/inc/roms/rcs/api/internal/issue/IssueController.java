package inc.roms.rcs.api.internal.issue;

import inc.roms.rcs.service.issue.IssueService;
import inc.roms.rcs.service.issue.request.IssueActionRequest;
import inc.roms.rcs.service.issue.request.ListIssueRequest;
import inc.roms.rcs.service.issue.response.IssueActionResponse;
import inc.roms.rcs.service.issue.response.IssueDetails;
import inc.roms.rcs.service.issue.response.IssueListResponse;
import inc.roms.rcs.vo.issue.IssueId;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class IssueController {

    private final IssueService issueService;

    @GetMapping("/api/internal/issue:list")
    public IssueListResponse list(ListIssueRequest request) {
        return issueService.list(request);
    }

    @PostMapping("/api/internal/issue:start")
    public IssueActionResponse start(@RequestBody IssueActionRequest request) {
        return issueService.start(request);
    }

    @GetMapping("/api/internal/issue:details")
    public IssueDetails details(@RequestParam("issue_id") IssueId issueId) {
        return issueService.getDetails(issueId);
    }

    @PostMapping("/api/internal/issue:close")
    public IssueActionResponse close(@RequestBody IssueActionRequest request) {
        return issueService.close(request);
    }
}
