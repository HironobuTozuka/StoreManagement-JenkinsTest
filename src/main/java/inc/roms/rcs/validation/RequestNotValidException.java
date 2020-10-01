package inc.roms.rcs.validation;

import inc.roms.rcs.api.error.model.RcsErrorCode;
import inc.roms.rcs.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class RequestNotValidException extends BusinessException {

    public RequestNotValidException(ValidationResult validationResult) {
        super("Validation failed, encountered problems: " + validationResult);
    }

    public RequestNotValidException(String validationProblem) {
        super("Validation failed, encountered problems: " + validationProblem);
    }

    @Override
    public HttpStatus httpStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public RcsErrorCode rcsErrorCode() {
        return RcsErrorCode.REQUEST_NOT_VALID;
    }
}
