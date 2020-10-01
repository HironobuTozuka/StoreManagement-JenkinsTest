package inc.roms.rcs.api.external.health;

import com.fasterxml.jackson.annotation.JsonFormat;
import inc.roms.rcs.vo.common.StoreId;
import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

import static inc.roms.rcs.api.external.v2_0.vo.JapanTimeHelper.DATETIME_PATTERN;

//TODO move to service package when corresponding service is ready.
@Data
@Builder
public class HealthCheckRsp {

    @JsonFormat(pattern = DATETIME_PATTERN)
    private ZonedDateTime receiveTime;
    private StoreId storeCode;
    private String osVersion;
    private String apiVersions;
    private String hardwareVersion;
    private RcsStatus statusCode;

    private Details statusDetails;
}
