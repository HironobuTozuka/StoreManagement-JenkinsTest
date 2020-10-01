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
public class SkuNotFoundException extends EntityNotFoundException {

    private SkuId skuId;
    private ExternalId externalId;

    public SkuNotFoundException(SkuId skuId) {
        super("Sku with skuId: " + skuId + " not found!");
        this.skuId = skuId;
    }

    public SkuNotFoundException(ExternalId externalId) {
        super("Sku with externalId: " + externalId + " not found!");
        this.externalId = externalId;
    }

    @Override
    public CreateIssueRequest toIssue(IssueFactory issueFactory) {
        return issueFactory.skuNotFound(skuId);
    }
}
