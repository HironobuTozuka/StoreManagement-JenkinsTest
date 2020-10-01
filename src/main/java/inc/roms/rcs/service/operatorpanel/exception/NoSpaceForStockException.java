package inc.roms.rcs.service.operatorpanel.exception;

import inc.roms.rcs.api.error.model.RcsErrorCode;
import inc.roms.rcs.exception.BusinessException;
import inc.roms.rcs.exception.IssueCreatingBusinessException;
import inc.roms.rcs.service.issue.IssueFactory;
import inc.roms.rcs.service.issue.request.CreateIssueRequest;
import inc.roms.rcs.vo.common.Quantity;
import inc.roms.rcs.vo.sku.SkuId;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NoSpaceForStockException extends IssueCreatingBusinessException {
    private SkuId skuId;
    private Quantity left;
    private Integer numberOfTotes;

    public NoSpaceForStockException(SkuId skuId) {
        super("No more slots available for sku: " + skuId);
        this.skuId = skuId;
    }

    public NoSpaceForStockException(SkuId skuId, Quantity left, Integer numberOfTotes, String message) {
        super(message);
        this.skuId = skuId;
        this.left = left;
        this.numberOfTotes = numberOfTotes;
    }

    @Override
    public HttpStatus httpStatus() {
        return HttpStatus.NOT_FOUND;
    }

    @Override
    public RcsErrorCode rcsErrorCode() {
        return RcsErrorCode.RESOURCE_NOT_AVAILABLE;
    }

    @Override
    public CreateIssueRequest toIssue(IssueFactory issueFactory) {
        return issueFactory.noSpaceForStock(skuId);
    }
}
