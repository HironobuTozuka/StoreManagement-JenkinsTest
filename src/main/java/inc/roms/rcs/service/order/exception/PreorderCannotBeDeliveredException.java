package inc.roms.rcs.service.order.exception;

import inc.roms.rcs.api.error.model.RcsErrorCode;
import inc.roms.rcs.exception.IssueCreatingBusinessException;
import inc.roms.rcs.service.issue.IssueFactory;
import inc.roms.rcs.service.issue.request.CreateIssueRequest;
import inc.roms.rcs.vo.order.OrderId;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class PreorderCannotBeDeliveredException extends IssueCreatingBusinessException {
    private final OrderId orderId;

    public PreorderCannotBeDeliveredException(OrderId orderId) {
        super("Order with id " + orderId + " cannot be delivered!");
        this.orderId = orderId;
    }

    @Override
    public HttpStatus httpStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    @Override
    public RcsErrorCode rcsErrorCode() {
        return RcsErrorCode.RESOURCE_NOT_AVAILABLE;
    }

    @Override
    public CreateIssueRequest toIssue(IssueFactory issueFactory) {
        return issueFactory.cannotDeliverPreorder(orderId);
    }
}
