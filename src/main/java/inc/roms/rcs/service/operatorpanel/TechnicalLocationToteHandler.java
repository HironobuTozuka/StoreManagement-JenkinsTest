package inc.roms.rcs.service.operatorpanel;

import inc.roms.rcs.service.inventory.ToteService;
import inc.roms.rcs.service.issue.IssueFactory;
import inc.roms.rcs.service.issue.IssueService;
import inc.roms.rcs.service.issue.request.CreateIssueRequest;
import inc.roms.rcs.service.operatorpanel.request.ToteNotificationRequest;
import inc.roms.rcs.service.operatorpanel.response.ToteNotificationResponse;
import inc.roms.rcs.vo.tote.ToteId;
import inc.roms.rcs.vo.tote.ToteStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TechnicalLocationToteHandler {

    private final ToteService toteService;
    private final IssueFactory issueFactory;
    private final IssueService issueService;

    public ToteNotificationResponse handle(ToteNotificationRequest request) {
        ToteId toteId = request.getToteId();
        ToteStatus toteStatus = request.getToteStatus();

        CreateIssueRequest toteOnTechnicalLocation = issueFactory.toteOnTechnicalLocation(toteId, toteStatus);
        issueService.create(toteOnTechnicalLocation);

        if (!toteId.equals(ToteId.NOREAD) && !toteId.equals(ToteId.UNKNOWN)) {
            toteService.markAsFailing(toteId, toteStatus);
        }

        return ToteNotificationResponse.holdTote(toteId);
    }
}
