package inc.roms.rcs.service.order.exception;

import inc.roms.rcs.exception.EntityNotFoundException;
import inc.roms.rcs.service.issue.IssueFactory;
import inc.roms.rcs.service.issue.request.CreateIssueRequest;
import inc.roms.rcs.vo.order.OrderId;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = true)
public class OrderNotFoundException extends EntityNotFoundException {
    private final OrderId orderId;

    public OrderNotFoundException(OrderId orderId) {
        super("Order with id " + orderId + " could not be found");
        this.orderId = orderId;
    }

    @Override
    public CreateIssueRequest toIssue(IssueFactory issueFactory) {
        return issueFactory.orderNotFound(orderId);
    }
}
