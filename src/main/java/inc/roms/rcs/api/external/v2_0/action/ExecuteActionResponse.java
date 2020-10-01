package inc.roms.rcs.api.external.v2_0.action;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import inc.roms.rcs.api.external.v2_0.vo.AcceptCode;
import inc.roms.rcs.vo.common.StoreId;
import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

import static inc.roms.rcs.api.external.v2_0.vo.JapanTimeHelper.DATETIME_PATTERN;

@Data
@Builder
public class ExecuteActionResponse {
    @JsonFormat(pattern = DATETIME_PATTERN)
    private ZonedDateTime receiveTime;

    private StoreId storeCode;
    private AcceptCode acceptCode;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ActionAcceptMessage acceptMessage;
}
