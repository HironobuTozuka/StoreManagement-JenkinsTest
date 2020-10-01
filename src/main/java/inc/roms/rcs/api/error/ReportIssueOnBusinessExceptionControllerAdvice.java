package inc.roms.rcs.api.error;

import inc.roms.rcs.api.error.annotations.ReportIssueOnBusinessException;
import inc.roms.rcs.api.error.model.ApiErrorFactory;
import inc.roms.rcs.exception.IssueCreatingBusinessException;
import inc.roms.rcs.service.issue.IssueFactory;
import inc.roms.rcs.service.issue.IssueService;
import inc.roms.rcs.service.issue.request.CreateIssueRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice(annotations = ReportIssueOnBusinessException.class)
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ReportIssueOnBusinessExceptionControllerAdvice extends BaseExceptionHandlingControllerAdvice {

    private final IssueFactory issueFactory;
    private final IssueService issueService;

    @Autowired
    public ReportIssueOnBusinessExceptionControllerAdvice(ApiErrorFactory apiErrorFactory, ExceptionMappings exceptionMappings, IssueFactory issueFactory, IssueService issueService) {
        super(apiErrorFactory, exceptionMappings);
        this.issueFactory = issueFactory;
        this.issueService = issueService;
    }

    @Override
    protected void businessHandling(Exception ex) {
        if(ex instanceof IssueCreatingBusinessException) {
            IssueCreatingBusinessException be = (IssueCreatingBusinessException)ex;
            CreateIssueRequest issue = be.toIssue(issueFactory);
            if(issue != null) {
                issueService.createAndReport(issue);
            }
        }
        log.debug("No specific exception handling defined, skipping...");
    }
}
