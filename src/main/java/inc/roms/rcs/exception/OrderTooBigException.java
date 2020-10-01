package inc.roms.rcs.exception;

import inc.roms.rcs.api.error.model.RcsErrorCode;
import inc.roms.rcs.service.issue.IssueFactory;
import inc.roms.rcs.service.issue.request.CreateIssueRequest;
import inc.roms.rcs.vo.common.Quantity;
import inc.roms.rcs.vo.order.OrderId;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class OrderTooBigException extends IssueCreatingBusinessException {

    private final OrderId orderId;
    private final Integer maxOrderSize;
    private final Quantity actualOrderSize;

    public OrderTooBigException(OrderId orderId, Integer maxOrderSize, Quantity actualOrderSize) {
        super("Order " + orderId + " is too big! Requested" + actualOrderSize + " while max possible items is " + maxOrderSize);
        this.orderId = orderId;
        this.maxOrderSize = maxOrderSize;
        this.actualOrderSize = actualOrderSize;

    }

    @Override
    public HttpStatus httpStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public RcsErrorCode rcsErrorCode() {
        return RcsErrorCode.REQUEST_NOT_VALID;
    }

    @Override
    public CreateIssueRequest toIssue(IssueFactory issueFactory) {
        return issueFactory.orderToBig(orderId, actualOrderSize, maxOrderSize);
    }
}
