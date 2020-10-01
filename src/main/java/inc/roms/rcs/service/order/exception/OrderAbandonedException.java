package inc.roms.rcs.service.order.exception;

import inc.roms.rcs.api.error.model.RcsErrorCode;
import inc.roms.rcs.exception.IssueCreatingBusinessException;
import inc.roms.rcs.service.issue.IssueFactory;
import inc.roms.rcs.service.issue.request.CreateIssueRequest;
import inc.roms.rcs.vo.order.OrderId;
import inc.roms.rcs.vo.order.OrderStatus;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class OrderAbandonedException extends IssueCreatingBusinessException {
    private final OrderId orderId;

    public OrderAbandonedException(OrderId orderId, OrderStatus status) {
        super("Order with id " + orderId + " was " + status);
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
        return issueFactory.orderNotFound(orderId);
    }
}
