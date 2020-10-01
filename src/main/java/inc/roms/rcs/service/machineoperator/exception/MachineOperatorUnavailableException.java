package inc.roms.rcs.service.machineoperator.exception;

import inc.roms.rcs.api.error.model.RcsErrorCode;
import inc.roms.rcs.exception.IssueCreatingBusinessException;
import inc.roms.rcs.service.issue.IssueFactory;
import inc.roms.rcs.service.issue.request.CreateIssueRequest;
import org.springframework.http.HttpStatus;

public class MachineOperatorUnavailableException extends IssueCreatingBusinessException {
    public MachineOperatorUnavailableException(Exception rootCause) {
        super(rootCause);
    }

    @Override
    public HttpStatus httpStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    @Override
    public RcsErrorCode rcsErrorCode() {
        return RcsErrorCode.MHE_EXCEPTION;
    }

    @Override
    public CreateIssueRequest toIssue(IssueFactory issueFactory) {
        return issueFactory.mheOutOfService();
    }
}
