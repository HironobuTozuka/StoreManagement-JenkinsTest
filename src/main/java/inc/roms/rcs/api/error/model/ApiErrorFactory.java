package inc.roms.rcs.api.error.model;

import inc.roms.rcs.vo.common.StoreId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.UUID;

@Component
public class ApiErrorFactory {

    @Value("${rcs.store.code:POC}")
    private StoreId storeCode;

    public ApiError getApiError(HttpStatus httpStatus, String errorMessage, RcsErrorCode errorCode) {
        return new ApiError(
                httpStatus.toString(),
                storeCode,
                UUID.randomUUID().toString(),
                ZonedDateTime.now(ZoneOffset.UTC),
                errorMessage,
                errorCode
        );
    }

}
