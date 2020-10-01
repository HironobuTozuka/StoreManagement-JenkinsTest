package inc.roms.rcs.service.issue;

import inc.roms.rcs.service.issue.domain.model.Issue;
import inc.roms.rcs.service.issue.domain.repository.IssueRepository;
import inc.roms.rcs.service.issue.request.CreateIssueRequest;
import inc.roms.rcs.service.issue.request.IssueActionRequest;
import inc.roms.rcs.service.issue.request.ListIssueRequest;
import inc.roms.rcs.service.issue.response.IssueActionResponse;
import inc.roms.rcs.service.issue.response.IssueDetails;
import inc.roms.rcs.service.issue.response.IssueListResponse;
import inc.roms.rcs.service.omnichannel.IssueReporterService;
import inc.roms.rcs.vo.common.ListResponseMetaDetails;
import inc.roms.rcs.vo.common.ResponseCode;
import inc.roms.rcs.vo.issue.IssueAction;
import inc.roms.rcs.vo.issue.IssueId;
import inc.roms.rcs.vo.issue.IssueReason;
import inc.roms.rcs.vo.issue.IssueStatus;
import inc.roms.rcs.vo.order.OrderId;
import inc.roms.rcs.vo.sku.SkuId;
import inc.roms.rcs.vo.tote.ToteId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.NoSuchElementException;

import static java.util.stream.Collectors.toList;
import static org.springframework.data.jpa.domain.Specification.where;

@Service
@RequiredArgsConstructor
public class IssueService {

    private final IssueRepository issueRepository;
    private final IssueReporterService issueReporterService;

    public CreateIssueResponse createAndReport(CreateIssueRequest request) {
        Issue issue = createNew(request);
        issueReporterService.report(issue);
        return new CreateIssueResponse(ResponseCode.ACCEPTED, new CreateIssueResponseDetails(issue.getIssueId()));
    }


    public CreateIssueResponse create(CreateIssueRequest request) {
        Issue issue = createNew(request);
        return new CreateIssueResponse(ResponseCode.ACCEPTED, new CreateIssueResponseDetails(issue.getIssueId()));
    }

    private Issue createNew(CreateIssueRequest request) {
        Issue issue = new Issue();
        issue.setNotes(request.getNotes());
        issue.setIssueStatus(IssueStatus.TO_DO);
        issue.setToteId(request.getToteId());
        issue.setSkuId(request.getSkuId());
        issue.setIssueAction(request.getIssueAction());
        issue.setIssueDeadline(request.getIssueDeadline());
        issue.setReason(request.getReason());
        issue.setIssueId(IssueId.generate());
        issueRepository.save(issue);
        return issue;
    }

    public IssueActionResponse start(IssueActionRequest request) {
        Issue issue = issueRepository.findByIssueId(request.getIssueId()).orElseThrow();
        issue.setIssueStatus(IssueStatus.IN_PROGRESS);
        issueRepository.save(issue);

        return new IssueActionResponse(ResponseCode.ACCEPTED);
    }

    public IssueActionResponse close(IssueActionRequest request) {
        Issue issue = issueRepository.findByIssueId(request.getIssueId()).orElseThrow();
        issue.setIssueStatus(request.getIssueStatus());
        issue.setNotes(request.getNotes());
        issueRepository.save(issue);

        return new IssueActionResponse(ResponseCode.ACCEPTED);
    }


    public IssueListResponse list(ListIssueRequest request) {
        List<Issue> foundIssues = issueRepository.findAll(buildSpec(request));
        List<IssueDetails> issueDetails = foundIssues.stream().map(this::createDetails).collect(toList());
        return new IssueListResponse(issueDetails, new ListResponseMetaDetails(foundIssues.size()));
    }

    private IssueDetails createDetails(Issue it) {
        return new IssueDetails(
                it.getIssueId(),
                it.getIssueAction(),
                it.getIssueDeadline().atZone(ZoneOffset.UTC),
                it.getIssueStatus(),
                it.getReason(),
                it.getToteId(),
                it.getOrderId(),
                it.getNotes()
        );
    }

    private static Specification<Issue> buildSpec(ListIssueRequest request) {
        return where(reasonIsEqualTo(request.getIssueReason()))
                .and(actionIsEqualTo(request.getIssueAction()))
                .and(statusIsEqualTo(request.getIssueStatus()))
                .and(deadlineIsEqualTo(request.getIssueDeadline()))
                .and(toteIdIsEqualTo(request.getToteId()))
                .and(skuIdIsEqualTo(request.getSkuId()))
                .and(orderIdIsEqualTo(request.getOrderId()));
    }

    private static Specification<Issue> orderIdIsEqualTo(OrderId orderId) {
        return fieldEquals(orderId, "orderId");
    }

    private static Specification<Issue> skuIdIsEqualTo(SkuId skuId) {
        return fieldEquals(skuId, "orderId");
    }

    private static Specification<Issue> toteIdIsEqualTo(ToteId toteId) {
        return fieldEquals(toteId, "toteId");
    }

    private static Specification<Issue> deadlineIsEqualTo(LocalDateTime issueDeadline) {
        return fieldEquals(issueDeadline, "issueDeadline");
    }

    private static Specification<Issue> statusIsEqualTo(IssueStatus issueStatus) {
        return fieldEquals(issueStatus, "issueStatus");
    }

    private static Specification<Issue> actionIsEqualTo(IssueAction issueAction) {
        return fieldEquals(issueAction, "issueAction");
    }

    private static Specification<Issue> reasonIsEqualTo(IssueReason reason) {
        return fieldEquals(reason, "reason");
    }

    private static Specification<Issue> fieldEquals(Object value, String fieldName) {
        return (root, query, criteriaBuilder) -> {
            if (value == null) {
                return null;
            }

            return criteriaBuilder.equal(root.get(fieldName), value);
        };
    }

    public IssueDetails getDetails(IssueId issueId) {
        return issueRepository.findByIssueId(issueId).map(this::createDetails).orElseThrow(NoSuchElementException::new);
    }
}
