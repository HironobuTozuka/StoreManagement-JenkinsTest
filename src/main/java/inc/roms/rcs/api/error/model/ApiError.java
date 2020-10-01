package inc.roms.rcs.api.error.model;

import inc.roms.rcs.vo.common.StoreId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiError {

    private String httpStatus;
    private StoreId storeCode;
    private String errorId;
    private ZonedDateTime errorTime;
    private String errorMessage;
    private RcsErrorCode errorCode;

}
