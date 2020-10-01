package inc.roms.rcs.api.external.v2_0.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import inc.roms.rcs.api.external.v2_0.vo.AcceptCode;
import inc.roms.rcs.api.external.v2_0.vo.AcceptMessage;
import inc.roms.rcs.vo.common.StoreId;
import inc.roms.rcs.vo.location.GateId;
import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

import static inc.roms.rcs.api.external.v2_0.vo.JapanTimeHelper.DATETIME_PATTERN;

@Data
@Builder
class CreateOrderResponse {

    @JsonFormat(pattern = DATETIME_PATTERN)
    private ZonedDateTime receiveTime;

    private StoreId storeCode;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private GateId gate;

    @JsonFormat(pattern = DATETIME_PATTERN)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("eta")
    private ZonedDateTime estimatedCompletionTime;

    private AcceptCode acceptCode;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private AcceptMessage acceptMessage;

}
