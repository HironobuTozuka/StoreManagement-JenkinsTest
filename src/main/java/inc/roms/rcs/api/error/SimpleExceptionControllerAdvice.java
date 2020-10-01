package inc.roms.rcs.api.error;

import inc.roms.rcs.api.error.model.ApiErrorFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;

@Slf4j
@ControllerAdvice
public class SimpleExceptionControllerAdvice extends BaseExceptionHandlingControllerAdvice {

    @Autowired
    public SimpleExceptionControllerAdvice(ApiErrorFactory apiErrorFactory, ExceptionMappings exceptionMappings) {
        super(apiErrorFactory, exceptionMappings);
    }

    @Override
    protected void businessHandling(Exception ex) {
        log.debug("No specific exception handling defined, skipping...");
    }
}
