package inc.roms.rcs.api.error.message;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import java.util.Arrays;
import java.util.Objects;

public class HttpMessageNotReadableExceptionMessageProvider implements MessageProvider {
    @Override
    public String getMessage(Exception ex) {
        if(ex.getCause() instanceof InvalidFormatException) {
            InvalidFormatException ife = (InvalidFormatException)ex.getCause();
            String message = "Problem parsing value: " + ife.getValue() + " of field: " + ife.getPath().get(0).getFieldName();
            Object[] enumConstants = ife.getTargetType().getEnumConstants();
            if(Objects.nonNull(enumConstants)) {
                message = message + " Accepted values are: " + Arrays.toString(enumConstants);
            }
            return message;
        }

        return "Body could not be parsed, please check request!";
    }
}
