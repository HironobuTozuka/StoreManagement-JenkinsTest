package inc.roms.rcs.service.order.exception;

import inc.roms.rcs.api.error.model.RcsErrorCode;
import inc.roms.rcs.exception.BusinessException;
import inc.roms.rcs.exception.IssueCreatingBusinessException;
import inc.roms.rcs.service.issue.IssueFactory;
import inc.roms.rcs.service.issue.request.CreateIssueRequest;
import inc.roms.rcs.vo.common.TemperatureRegime;
import inc.roms.rcs.vo.issue.IssueId;
import inc.roms.rcs.vo.order.OrderId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@EqualsAndHashCode(callSuper = true)
@Getter
public class NoEmptyTotesException extends IssueCreatingBusinessException {

    private TemperatureRegime temperatureRegime;

    public NoEmptyTotesException(TemperatureRegime temperatureRegime) {
        super("No available empty totes found in temp regime: " + temperatureRegime);
        this.temperatureRegime = temperatureRegime;
    }

    public NoEmptyTotesException() {
        super("No available empty totes found!");
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
        return issueFactory.noEmptyTotes();
    }
}
