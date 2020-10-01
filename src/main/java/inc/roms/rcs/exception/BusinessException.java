package inc.roms.rcs.exception;

import inc.roms.rcs.api.error.model.RcsErrorCode;
import org.springframework.http.HttpStatus;

public abstract class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(Throwable cause) {
        super(cause);
    }

    public abstract HttpStatus httpStatus();

    public abstract RcsErrorCode rcsErrorCode();
}
