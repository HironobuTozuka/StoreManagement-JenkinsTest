package inc.roms.rcs.service.inventory.exception;

import inc.roms.rcs.exception.EntityNotFoundException;
import inc.roms.rcs.service.issue.IssueFactory;
import inc.roms.rcs.service.issue.request.CreateIssueRequest;
import inc.roms.rcs.vo.sku.ExternalId;
import inc.roms.rcs.vo.sku.SkuId;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.NOT_FOUND)
@EqualsAndHashCode(callSuper = true)
public class SkuNotReadyException extends EntityNotFoundException {

    private SkuId skuId;
    private ExternalId externalId;

    public SkuNotReadyException(SkuId skuId) {
        super("Sku with skuId: " + skuId + " not ready yet! Check if this sku was already scanned in scanning cell");
        this.skuId = skuId;
    }

    public SkuNotReadyException(ExternalId externalId) {
        super("Sku with externalId: " + externalId + " not ready yet! Check if this sku was already scanned in scanning cell");
        this.externalId = externalId;
    }

    @Override
    public CreateIssueRequest toIssue(IssueFactory issueFactory) {
        return issueFactory.skuNotFound(skuId);
    }
}
