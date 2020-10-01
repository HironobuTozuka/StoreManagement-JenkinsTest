package inc.roms.rcs.service.order.exception;

import inc.roms.rcs.api.error.model.RcsErrorCode;
import inc.roms.rcs.exception.BusinessException;
import inc.roms.rcs.exception.EntityNotFoundException;
import inc.roms.rcs.exception.IssueCreatingBusinessException;
import inc.roms.rcs.service.issue.IssueFactory;
import inc.roms.rcs.service.issue.request.CreateIssueRequest;
import inc.roms.rcs.vo.order.OrderId;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@EqualsAndHashCode(callSuper = true)
public class OrderAlreadyDeliveredException extends IssueCreatingBusinessException {
    private final OrderId orderId;

    public OrderAlreadyDeliveredException(OrderId orderId) {
        super("Order with id " + orderId + " was already delivered");
        this.orderId = orderId;
    }

    @Override
    public HttpStatus httpStatus() {
        return HttpStatus.CONFLICT;
    }

    @Override
    public RcsErrorCode rcsErrorCode() {
        return RcsErrorCode.REQUEST_NOT_VALID;
    }

    @Override
    public CreateIssueRequest toIssue(IssueFactory issueFactory) {
        return issueFactory.orderAlreadyDelivered(orderId);
    }
}
