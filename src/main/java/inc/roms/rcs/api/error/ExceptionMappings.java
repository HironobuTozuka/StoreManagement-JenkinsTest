package inc.roms.rcs.api.error;

import inc.roms.rcs.api.error.message.HttpMessageNotReadableExceptionMessageProvider;
import inc.roms.rcs.api.error.message.HttpRequestMethodNotSupportedExceptionMessageProvider;
import inc.roms.rcs.api.error.message.MessageProvider;
import inc.roms.rcs.api.error.model.RcsErrorCode;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;

import java.util.HashMap;
import java.util.Map;

@Component
public class ExceptionMappings {

    private static final MessageProvider DEFAULT_MESSAGE_PROVIDER = e -> "System exception";
    private final Map<Class<?>, RcsErrorCode> errorCodeMap = new HashMap<>();
    private final Map<Class<?>, MessageProvider> errorMessages = new HashMap<>();

    public ExceptionMappings() {
        /*
         * Error Codes
         */
        errorCodeMap.put(HttpRequestMethodNotSupportedException.class, RcsErrorCode.METHOD_NOT_ALLOWED);
        errorCodeMap.put(HttpMessageNotReadableException.class, RcsErrorCode.REQUEST_NOT_VALID);
        errorCodeMap.put(HttpMediaTypeNotSupportedException.class, RcsErrorCode.MEDIA_TYPE_NOT_SUPPORTED);
        errorCodeMap.put(MissingServletRequestParameterException.class, RcsErrorCode.REQUEST_NOT_VALID);
        errorCodeMap.put(AccessDeniedException.class, RcsErrorCode.UNAUTHORIZED);

        /*
         * Error Messages
         */
        errorMessages.put(HttpMediaTypeNotSupportedException.class, Throwable::getMessage);
        errorMessages.put(HttpRequestMethodNotSupportedException.class, new HttpRequestMethodNotSupportedExceptionMessageProvider());
        errorMessages.put(MissingServletRequestParameterException.class, Throwable::getMessage);
        errorMessages.put(HttpMessageNotReadableException.class, new HttpMessageNotReadableExceptionMessageProvider());
        errorMessages.put(AccessDeniedException.class, (e) -> "Unauthorized");
    }

    public String getMessage(Exception ex) {
        return errorMessages.getOrDefault(ex.getClass(), DEFAULT_MESSAGE_PROVIDER).getMessage(ex);
    }

    public RcsErrorCode getRcsErrorCode(Exception ex) {
        return errorCodeMap.getOrDefault(ex.getClass(), RcsErrorCode.SYSTEM_EXCEPTION);
    }

}
