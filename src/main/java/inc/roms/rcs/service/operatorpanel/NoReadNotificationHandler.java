package inc.roms.rcs.service.operatorpanel;

import inc.roms.rcs.service.inventory.ToteService;
import inc.roms.rcs.service.inventory.exception.ToteNotFoundException;
import inc.roms.rcs.service.issue.IssueFactory;
import inc.roms.rcs.service.issue.IssueService;
import inc.roms.rcs.service.issue.request.CreateIssueRequest;
import inc.roms.rcs.service.omnichannel.OmniChannelService;
import inc.roms.rcs.service.operatorpanel.request.ToteNotificationRequest;
import inc.roms.rcs.service.operatorpanel.response.ToteNotificationResponse;
import inc.roms.rcs.vo.tote.ToteId;
import inc.roms.rcs.vo.tote.ToteStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoReadNotificationHandler {

    private final ToteService toteService;
    private final IssueFactory issueFactory;
    private final IssueService issueService;

    public ToteNotificationResponse handle(ToteNotificationRequest request) {
        if(!ToteId.NOREAD.equals(request.getToteId())) {
            toteService.markAsFailing(request.getToteId(), ToteStatus.NO_READ);
        }
        CreateIssueRequest noReadIssue = issueFactory.toteNoRead(request.getToteId());
        issueService.createAndReport(noReadIssue);
        return ToteNotificationResponse.rejectTote(request.getToteId());
    }

}
