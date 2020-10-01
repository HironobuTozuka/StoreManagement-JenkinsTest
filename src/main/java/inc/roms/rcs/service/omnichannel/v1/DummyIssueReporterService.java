package inc.roms.rcs.service.omnichannel.v1;

import inc.roms.rcs.service.issue.domain.model.Issue;
import inc.roms.rcs.service.omnichannel.IssueReporterService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import static inc.roms.rcs.service.omnichannel.OmniChannelProfiles.NOT_COMMERCE_PLATFORM;

@Service
@Profile(NOT_COMMERCE_PLATFORM)
public class DummyIssueReporterService implements IssueReporterService {
    @Override
    public void report(Issue issue) {

    }
}
