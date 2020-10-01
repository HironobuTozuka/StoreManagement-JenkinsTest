package inc.roms.rcs.service.issue;

import inc.roms.rcs.service.inventory.SkuService;
import inc.roms.rcs.service.inventory.ToteService;
import inc.roms.rcs.service.BaseIntegrationTest;
import inc.roms.rcs.service.issue.domain.model.Issue;
import inc.roms.rcs.service.issue.domain.repository.IssueRepository;
import inc.roms.rcs.service.issue.request.ListIssueRequest;
import inc.roms.rcs.service.issue.response.IssueListResponse;
import inc.roms.rcs.service.order.OrderManagementService;
import inc.roms.rcs.service.order.domain.OrderService;
import inc.roms.rcs.service.task.domain.TaskBundleService;
import inc.roms.rcs.vo.issue.*;
import inc.roms.rcs.vo.tote.ToteId;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.flywaydb.test.annotation.FlywayTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.*;

import static inc.roms.rcs.vo.issue.IssueAction.*;
import static inc.roms.rcs.vo.issue.IssueReason.*;
import static inc.roms.rcs.vo.issue.IssueStatus.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = { "zonky.test.database.postgres.client.properties.currentSchema=sm", "inc.roms.machineoperator.enabled=false", "inc.roms.omnichannel.enabled=false", "inc.roms.usb.enabled=false" })
@AutoConfigureEmbeddedDatabase
public class ListIssuesTest extends BaseIntegrationTest {

    public static final IssueId ISSUE_ID = IssueId.generate();
    public static final Clock FIXED_CLOCK = Clock.fixed(Instant.ofEpochMilli(10000000), ZoneOffset.UTC);
    public static final LocalDateTime DEADLINE = LocalDateTime.now(FIXED_CLOCK);
    public static final ZonedDateTime DEADLINE_UTC = LocalDateTime.now(FIXED_CLOCK).atZone(ZoneOffset.UTC);
    public static final ToteId TOTE_ID = ToteId.from("TOTE_1");
    public static final Notes NOTES = Notes.from("notes");
    private final IssueRepository issueRepository;
    private final IssueService issueService;

    @Autowired
    public ListIssuesTest(OrderManagementService orderManagementService, ToteService toteService, SkuService skuService, TaskBundleService taskBundleService, OrderService orderService, IssueRepository issueRepository, IssueService issueService) {
        super(orderManagementService, toteService, skuService, taskBundleService, orderService);
        this.issueRepository = issueRepository;
        this.issueService = issueService;
    }

    @Test
    @FlywayTest
    public void shouldReturnAllBasicData() {
        //given
        issue(ISSUE_ID, TO_DO, COULDNT_PICK, DEADLINE, TOTE_ID, CHECK_SKU_BATCH, NOTES);

        //when
        IssueListResponse list = issueService.list(ListIssueRequest.builder().build());

        //then
        assertThat(list.getIssues()).hasSize(1);
        assertThat(list.getIssues().get(0).getIssueAction()).isEqualTo(CHECK_SKU_BATCH);
        assertThat(list.getIssues().get(0).getIssueDeadline()).isEqualTo(DEADLINE_UTC);
        assertThat(list.getIssues().get(0).getIssueId()).isEqualTo(ISSUE_ID);
        assertThat(list.getIssues().get(0).getIssueStatus()).isEqualTo(TO_DO);
        assertThat(list.getIssues().get(0).getNotes()).isEqualTo(NOTES);
        assertThat(list.getIssues().get(0).getReason()).isEqualTo(COULDNT_PICK);
        assertThat(list.getIssues().get(0).getToteId()).isEqualTo(TOTE_ID);
    }

    @Test
    @FlywayTest
    public void shouldFilterByAction() {
        //given
        issue(ISSUE_ID, TO_DO, COULDNT_PICK, DEADLINE, TOTE_ID, CHECK_SKU_BATCH, NOTES);
        issue(IssueId.generate(), TO_DO, COULDNT_PICK, DEADLINE, TOTE_ID, DISPOSE_SKU, NOTES);

        //when
        IssueListResponse list = issueService.list(ListIssueRequest.builder().issueAction(CHECK_SKU_BATCH).build());

        //then
        assertThat(list.getIssues()).hasSize(1);
        assertThat(list.getIssues().get(0).getIssueId()).isEqualTo(ISSUE_ID);
    }

    @Test
    @FlywayTest
    public void shouldFilterByDeadline() {
        //given
        issue(ISSUE_ID, TO_DO, COULDNT_PICK, DEADLINE, TOTE_ID, CHECK_SKU_BATCH, NOTES);
        issue(IssueId.generate(), TO_DO, COULDNT_PICK, DEADLINE.plusDays(1), TOTE_ID, CHECK_SKU_BATCH, NOTES);

        //when
        IssueListResponse list = issueService.list(ListIssueRequest.builder().issueDeadline(DEADLINE).build());

        //then
        assertThat(list.getIssues()).hasSize(1);
        assertThat(list.getIssues().get(0).getIssueId()).isEqualTo(ISSUE_ID);
    }

    @Test
    @FlywayTest
    public void shouldFilterByIssueStatus() {
        //given
        issue(ISSUE_ID, TO_DO, COULDNT_PICK, DEADLINE, TOTE_ID, CHECK_SKU_BATCH, NOTES);
        issue(IssueId.generate(), COULDNT_DO, COULDNT_PICK, DEADLINE, TOTE_ID, CHECK_SKU_BATCH, NOTES);

        //when
        IssueListResponse list = issueService.list(ListIssueRequest.builder().issueStatus(TO_DO).build());

        //then
        assertThat(list.getIssues()).hasSize(1);
        assertThat(list.getIssues().get(0).getIssueId()).isEqualTo(ISSUE_ID);
    }


    @Test
    @FlywayTest
    public void shouldFilterByReason() {
        //given
        issue(ISSUE_ID, TO_DO, COULDNT_PICK, DEADLINE, TOTE_ID, CHECK_SKU_BATCH, NOTES);
        issue(IssueId.generate(), TO_DO, DISPOSE_REQUESTED, DEADLINE, TOTE_ID, CHECK_SKU_BATCH, NOTES);

        //when
        IssueListResponse list = issueService.list(ListIssueRequest.builder().issueReason(COULDNT_PICK).build());

        //then
        assertThat(list.getIssues()).hasSize(1);
        assertThat(list.getIssues().get(0).getIssueId()).isEqualTo(ISSUE_ID);
    }

    @Test
    @FlywayTest
    public void shouldFindOnlyIssuesMatchingAllConditions() {
        //given
        issue(ISSUE_ID, TO_DO, COULDNT_PICK, DEADLINE, TOTE_ID, CHECK_SKU_BATCH, NOTES);
        issue(IssueId.generate(), TO_DO, COULDNT_PICK, DEADLINE, TOTE_ID, DISPOSE_SKU, NOTES);
        issue(IssueId.generate(), TO_DO, COULDNT_PICK, DEADLINE.plusDays(1), TOTE_ID, CHECK_SKU_BATCH, NOTES);
        issue(IssueId.generate(), COULDNT_DO, COULDNT_PICK, DEADLINE, TOTE_ID, CHECK_SKU_BATCH, NOTES);
        issue(IssueId.generate(), TO_DO, DISPOSE_REQUESTED, DEADLINE, TOTE_ID, CHECK_SKU_BATCH, NOTES);

        //when
        IssueListResponse list = issueService.list(ListIssueRequest.builder()
                .issueAction(CHECK_SKU_BATCH)
                .issueDeadline(DEADLINE)
                .issueStatus(TO_DO)
                .issueReason(COULDNT_PICK)
                .build());

        //then
        assertThat(list.getIssues()).hasSize(1);
        assertThat(list.getIssues().get(0).getIssueId()).isEqualTo(ISSUE_ID);
    }

    private void issue(IssueId issueId, IssueStatus toDo, IssueReason couldntPick, LocalDateTime deadline, ToteId tote, IssueAction checkSku, Notes notes) {
        Issue issue = new Issue();
        issue.setIssueId(issueId);
        issue.setIssueStatus(toDo);
        issue.setReason(couldntPick);
        issue.setIssueDeadline(deadline);
        issue.setToteId(tote);
        issue.setIssueAction(checkSku);
        issue.setNotes(notes);
        issueRepository.save(issue);
    }
}
