package inc.roms.rcs.service.order.exception;

import inc.roms.rcs.api.error.model.RcsErrorCode;
import inc.roms.rcs.exception.BusinessException;
import inc.roms.rcs.exception.IssueCreatingBusinessException;
import inc.roms.rcs.service.issue.IssueFactory;
import inc.roms.rcs.service.issue.request.CreateIssueRequest;
import inc.roms.rcs.service.order.response.RejectedSku;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
@EqualsAndHashCode(callSuper = true)
public class NotEnoughSkuToFulfillOrderException extends IssueCreatingBusinessException {
    private final List<RejectedSku> rejectedSkus;

    public NotEnoughSkuToFulfillOrderException(List<RejectedSku> rejectedSkus) {
        super("Not enough sku: " + rejectedSkus);
        this.rejectedSkus = rejectedSkus;
    }

    @Override
    public HttpStatus httpStatus() {
        return HttpStatus.FORBIDDEN;
    }

    @Override
    public RcsErrorCode rcsErrorCode() {
        return RcsErrorCode.RESOURCE_NOT_AVAILABLE;
    }

    @Override
    public CreateIssueRequest toIssue(IssueFactory issueFactory) {
        return issueFactory.outOfStock(rejectedSkus.get(0).getSkuId());
    }
}
