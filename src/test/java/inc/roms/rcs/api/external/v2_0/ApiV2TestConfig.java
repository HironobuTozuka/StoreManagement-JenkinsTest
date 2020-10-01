package inc.roms.rcs.api.external.v2_0;

import inc.roms.rcs.service.issue.IssueFactory;
import inc.roms.rcs.service.issue.IssueService;
import inc.roms.rcs.service.issue.domain.repository.IssueRepository;
import inc.roms.rcs.service.omnichannel.IssueReporterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

public class ApiV2TestConfig {

    @Bean
    public Clock clock() {
        return Clock.fixed(Instant.ofEpochMilli(200000000), ZoneOffset.UTC);
    }

    @Bean
    @Autowired
    public IssueFactory issueFactory(Clock clock) {
        return new IssueFactory(clock);
    }

    @Bean
    @Autowired
    public IssueService issueService(IssueReporterService issueReporterService, IssueRepository issueRepository) {
        return new IssueService(issueRepository, issueReporterService);
    }
}
