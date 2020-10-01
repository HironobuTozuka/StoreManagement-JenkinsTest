package inc.roms.rcs.api.error.message;

import org.springframework.web.HttpRequestMethodNotSupportedException;

import java.util.Arrays;

public class HttpRequestMethodNotSupportedExceptionMessageProvider implements MessageProvider {

    @Override
    public String getMessage(Exception ex) {
        HttpRequestMethodNotSupportedException exception = (HttpRequestMethodNotSupportedException)ex;
        return "Request Method: " + exception.getMethod() + " is not supported, supported methods: " + Arrays.toString(exception.getSupportedMethods());
    }
}
