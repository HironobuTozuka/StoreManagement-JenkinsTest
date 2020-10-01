package inc.roms.rcs.service.issue;

import inc.roms.rcs.service.BaseIntegrationTest;
import inc.roms.rcs.service.inventory.SkuService;
import inc.roms.rcs.service.inventory.ToteService;
import inc.roms.rcs.service.issue.domain.model.Issue;
import inc.roms.rcs.service.issue.domain.repository.IssueRepository;
import inc.roms.rcs.service.issue.request.CreateIssueRequest;
import inc.roms.rcs.service.issue.request.IssueActionRequest;
import inc.roms.rcs.service.order.OrderManagementService;
import inc.roms.rcs.service.order.domain.OrderService;
import inc.roms.rcs.service.task.domain.TaskBundleService;
import inc.roms.rcs.vo.issue.*;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.flywaydb.test.annotation.FlywayTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static inc.roms.rcs.service.issue.request.CreateIssueRequest.issue;
import static inc.roms.rcs.vo.common.ResponseCode.ACCEPTED;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = { "zonky.test.database.postgres.client.properties.currentSchema=sm", "inc.roms.machineoperator.enabled=false", "inc.roms.omnichannel.enabled=false", "inc.roms.usb.enabled=false" })
@AutoConfigureEmbeddedDatabase
class IssueServiceTest extends BaseIntegrationTest {

    private final Clock clock = Clock.fixed(Instant.ofEpochMilli(1000000000), ZoneOffset.UTC);

    private final IssueService issueService;
    private final IssueRepository issueRepository;

    @Autowired
    public IssueServiceTest(OrderManagementService orderManagementService, ToteService toteService, SkuService skuService, TaskBundleService taskBundleService, OrderService orderService, IssueService issueService, IssueRepository issueRepository) {
        super(orderManagementService, toteService, skuService, taskBundleService, orderService);
        this.issueService = issueService;
        this.issueRepository = issueRepository;
    }

    @Test
    @FlywayTest
    public void shouldCreateIssue() {
        CreateIssueRequest createIssueRequest = issue()
                .issueAction(IssueAction.CHECK_SKU_BATCH)
                .issueDeadline(LocalDateTime.now(clock))
                .reason(IssueReason.COULDNT_PICK)
                .notes(Notes.from("Notes"))
                .toteId(STORAGE_TOTE_ID_1)
                .build();

        CreateIssueResponse createIssueResponse = issueService.createAndReport(createIssueRequest);

        assertThat(createIssueResponse.getResponseCode()).isEqualTo(ACCEPTED);
        assertThat(createIssueResponse.getDetails().getIssueId()).isNotNull();
        assertThat(issueRepository.findByIssueId(createIssueResponse.getDetails().getIssueId())).isPresent();
    }

    @Test
    @FlywayTest
    public void shouldBeAbleToStartIssue() {
        CreateIssueRequest createIssueRequest = issue()
                .issueAction(IssueAction.CHECK_SKU_BATCH)
                .issueDeadline(LocalDateTime.now(clock))
                .reason(IssueReason.COULDNT_PICK)
                .notes(Notes.from("Notes"))
                .toteId(STORAGE_TOTE_ID_1)
                .build();

        CreateIssueResponse createIssueResponse = issueService.create(createIssueRequest);

        assertThat(createIssueResponse.getResponseCode()).isEqualTo(ACCEPTED);

        IssueId issueId = createIssueResponse.getDetails().getIssueId();

        assertThat(issueId).isNotNull();
        assertThat(issueRepository.findByIssueId(issueId)).isPresent();

        IssueActionRequest issueActionRequest = new IssueActionRequest(issueId, IssueStatus.IN_PROGRESS, null);
        issueService.start(issueActionRequest);

        Optional<Issue> issue = issueRepository.findByIssueId(issueId);

        assertThat(issue).isPresent();
        assertThat(issue.get().getIssueStatus()).isEqualTo(IssueStatus.IN_PROGRESS);
    }

    @Test
    @FlywayTest
    public void shouldBeAbleToCloseIssue() {
        CreateIssueRequest createIssueRequest = issue()
                .issueAction(IssueAction.CHECK_SKU_BATCH)
                .issueDeadline(LocalDateTime.now(clock))
                .reason(IssueReason.COULDNT_PICK)
                .notes(Notes.from("Notes"))
                .toteId(STORAGE_TOTE_ID_1)
                .build();

        CreateIssueResponse createIssueResponse = issueService.create(createIssueRequest);

        assertThat(createIssueResponse.getResponseCode()).isEqualTo(ACCEPTED);

        IssueId issueId = createIssueResponse.getDetails().getIssueId();

        assertThat(issueId).isNotNull();
        assertThat(issueRepository.findByIssueId(issueId)).isPresent();

        IssueActionRequest issueActionRequest = new IssueActionRequest(issueId, IssueStatus.DONE, null);
        issueService.close(issueActionRequest);

        Optional<Issue> issue = issueRepository.findByIssueId(issueId);

        assertThat(issue).isPresent();
        assertThat(issue.get().getIssueStatus()).isEqualTo(IssueStatus.DONE);
    }

    @Test
    @FlywayTest
    public void shouldBeAbleToCloseIssueWithStatusCouldntDo() {
        CreateIssueRequest createIssueRequest = issue()
                .issueAction(IssueAction.CHECK_SKU_BATCH)
                .issueDeadline(LocalDateTime.now(clock))
                .reason(IssueReason.COULDNT_PICK)
                .notes(Notes.from("Notes"))
                .toteId(STORAGE_TOTE_ID_1)
                .build();

        CreateIssueResponse createIssueResponse = issueService.create(createIssueRequest);

        assertThat(createIssueResponse.getResponseCode()).isEqualTo(ACCEPTED);

        IssueId issueId = createIssueResponse.getDetails().getIssueId();

        assertThat(issueId).isNotNull();
        assertThat(issueRepository.findByIssueId(issueId)).isPresent();

        IssueActionRequest issueActionRequest = new IssueActionRequest(issueId, IssueStatus.COULDNT_DO, null);
        issueService.close(issueActionRequest);

        Optional<Issue> issue = issueRepository.findByIssueId(issueId);

        assertThat(issue).isPresent();
        assertThat(issue.get().getIssueStatus()).isEqualTo(IssueStatus.COULDNT_DO);
    }
}