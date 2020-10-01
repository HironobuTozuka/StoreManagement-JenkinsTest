package inc.roms.rcs.exception;

import inc.roms.rcs.api.error.model.RcsErrorCode;
import inc.roms.rcs.service.issue.IssueFactory;
import inc.roms.rcs.service.issue.request.CreateIssueRequest;
import inc.roms.rcs.vo.order.OrderId;
import org.springframework.http.HttpStatus;

import java.util.List;

public class BatchOrderActionFailedException extends IssueCreatingBusinessException {
    private final List<OrderId> notFound;

    public BatchOrderActionFailedException(List<OrderId> notFound) {
        super("Couldn't execute action for orders!");
        this.notFound = notFound;
    }

    @Override
    public HttpStatus httpStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    @Override
    public RcsErrorCode rcsErrorCode() {
        return RcsErrorCode.SYSTEM_EXCEPTION;
    }

    @Override
    public CreateIssueRequest toIssue(IssueFactory issueFactory) {
        return issueFactory.orderNotFound(notFound.get(0));
    }
}
