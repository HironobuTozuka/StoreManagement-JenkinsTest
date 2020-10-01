package inc.roms.rcs.exception;

import inc.roms.rcs.api.error.model.RcsErrorCode;
import org.springframework.http.HttpStatus;

public abstract class EntityNotFoundException extends IssueCreatingBusinessException {
    public EntityNotFoundException(String message) {
        super(message);
    }

    @Override
    public HttpStatus httpStatus() {
        return HttpStatus.NOT_FOUND;
    }

    @Override
    public RcsErrorCode rcsErrorCode() {
        return RcsErrorCode.RESOURCE_NOT_FOUND;
    }
}
