package inc.roms.rcs.api.error;

import inc.roms.rcs.api.error.model.ApiError;
import inc.roms.rcs.api.error.model.ApiErrorFactory;
import inc.roms.rcs.exception.BusinessException;
import inc.roms.rcs.validation.RequestNotValidException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseExceptionHandlingControllerAdvice extends ResponseEntityExceptionHandler {

    private final ApiErrorFactory apiErrorFactory;
    private final ExceptionMappings exceptionMappings;

    Logger stacktraceLogger = LoggerFactory.getLogger("stacktrace");

    @ExceptionHandler({BusinessException.class})
    public final ResponseEntity<Object> handleBusinessException(BusinessException businessException) {
        businessHandling(businessException);

        ApiError apiError = apiErrorFactory.getApiError(businessException.httpStatus(),
                businessException.getMessage(),
                businessException.rcsErrorCode());

        log.warn("Handling error: {}", apiError);
        stacktraceLogger.error("Handling error_id: " + apiError.getErrorId(), businessException);

        return new ResponseEntity<>(
                apiError,
                new HttpHeaders(),
                businessException.httpStatus());
    }

    @ExceptionHandler({AccessDeniedException.class})
    public final ResponseEntity<Object> handleSecurityException(AccessDeniedException ex) {
        HttpStatus httpStatus = HttpStatus.MOVED_PERMANENTLY;

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/login");

        return handleInternal(ex, httpStatus, headers);
    }

    @ExceptionHandler({Exception.class})
    public final ResponseEntity<Object> handleAll(Exception ex) {
        return handleInternal(ex, HttpStatus.INTERNAL_SERVER_ERROR, new HttpHeaders());
    }

    @Override
    protected final ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return handleInternal(ex, status, headers);
    }

    private ResponseEntity<Object> handleInternal(Exception ex, HttpStatus statusCode, HttpHeaders headers) {
        ApiError apiError = apiErrorFactory.getApiError(statusCode,
                exceptionMappings.getMessage(ex),
                exceptionMappings.getRcsErrorCode(ex));

        log.error("Handling error_id: " + apiError.getErrorId(), ex);

        return new ResponseEntity<>(
                apiError,
                headers,
                statusCode);
    }

    protected abstract void businessHandling(Exception ex);
}
