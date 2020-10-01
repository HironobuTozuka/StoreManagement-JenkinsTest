package inc.roms.rcs.service.omnichannel.kannart;

import inc.roms.rcs.service.issue.domain.model.Issue;
import inc.roms.rcs.service.omnichannel.IssueReporterService;
import inc.roms.rcs.service.omnichannel.OmniChannelProfiles;
import inc.roms.rcs.service.omnichannel.kannart.model.*;
import inc.roms.rcs.vo.issue.IssueReason;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Profile(OmniChannelProfiles.COMMERCE_PLATFORM)
@Service
@RequiredArgsConstructor
@Slf4j
public class IssueReporterECService implements IssueReporterService {

    private final OmniChannelECClient omniChannelECClient;

    @Async
    @Override
    public void report(Issue issue) {
//        omniChannelECClient.send(
//                convert(issue)
//        );
    }

    private ErrorReport convert(Issue issue) {
        return ErrorReport.builder()
                .orderNo(issue.getOrderId())
                .actionCode(from(issue.getReason()))
                .exitCode(ExitCode.ERROR)
                .exitMessage(ExitMessage.builder()
                        .errorCode(issue.getReason())
                        .issueId(issue.getIssueId())
                        .sku(issue.getSkuId())
                        .build())
                .build();
    }

    private ActionCode from(IssueReason reason) {
        switch (reason) {
            case NO_SPACE_FOR_TOTE:
            case NO_SPACE_FOR_STOCK: {
                return ActionCode.RESUPPLY_FAILED;
            }
            case CANNOT_DELIVER:
            case CANNOT_PICK: {
                return ActionCode.ORDER_FAILED;
            }
            default:
                return ActionCode.SYSTEM_ERROR;
        }
    }
}
