package inc.roms.rcs.service.order.exception;

import inc.roms.rcs.api.error.model.RcsErrorCode;
import inc.roms.rcs.exception.IssueCreatingBusinessException;
import inc.roms.rcs.service.issue.IssueFactory;
import inc.roms.rcs.service.issue.request.CreateIssueRequest;
import inc.roms.rcs.service.order.config.GateProperties;
import inc.roms.rcs.vo.location.GateId;
import inc.roms.rcs.vo.order.OrderId;
import lombok.Getter;
import org.springframework.http.HttpStatus;

public class UnknownGateException extends IssueCreatingBusinessException {

    @Getter
    private final GateId gateId;

    @Getter
    private final GateProperties gateProperties;

    @Getter
    private final OrderId orderId;

    public UnknownGateException(OrderId orderId, GateId gateId, GateProperties gateProperties) {
        super("Unknown gate: " + gateId + " requested for order " + orderId + " Available gates: " + gateProperties.availableOrderGateIds());
        this.gateId = gateId;
        this.gateProperties = gateProperties;
        this.orderId = orderId;
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
        return issueFactory.gateNotFound(orderId, gateId, gateProperties.availableOrderGateIds());
    }
}
