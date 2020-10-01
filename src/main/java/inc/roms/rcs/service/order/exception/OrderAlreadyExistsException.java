package inc.roms.rcs.service.order.exception;

import inc.roms.rcs.api.error.model.RcsErrorCode;
import inc.roms.rcs.exception.BusinessException;
import inc.roms.rcs.service.issue.IssueFactory;
import inc.roms.rcs.service.issue.request.CreateIssueRequest;
import inc.roms.rcs.vo.order.OrderId;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class OrderAlreadyExistsException extends BusinessException {

    private final OrderId orderId;

    public OrderAlreadyExistsException(OrderId orderId) {
        super("Order with id " + orderId + " already exists");
        this.orderId = orderId;
    }

    @Override
    public HttpStatus httpStatus() {
        return HttpStatus.CONFLICT;
    }

    @Override
    public RcsErrorCode rcsErrorCode() {
        return RcsErrorCode.RESOURCE_ALREADY_EXISTS;
    }

}
