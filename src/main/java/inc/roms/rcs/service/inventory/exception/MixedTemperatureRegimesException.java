package inc.roms.rcs.service.inventory.exception;

import inc.roms.rcs.api.error.model.RcsErrorCode;
import inc.roms.rcs.exception.BusinessException;
import inc.roms.rcs.service.issue.IssueFactory;
import inc.roms.rcs.service.issue.request.CreateIssueRequest;
import inc.roms.rcs.vo.sku.SkuId;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
public class MixedTemperatureRegimesException extends BusinessException {

    private final List<SkuId> skuIds;

    public MixedTemperatureRegimesException(List<SkuId> skuIds) {
        super("Provided sku have mixed temperature regimes: " + skuIds);
        this.skuIds = skuIds;
    }

    @Override
    public HttpStatus httpStatus() {
        return HttpStatus.UNPROCESSABLE_ENTITY;
    }

    @Override
    public RcsErrorCode rcsErrorCode() {
        return RcsErrorCode.REQUEST_NOT_VALID;
    }

}
