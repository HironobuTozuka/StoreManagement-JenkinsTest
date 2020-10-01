package inc.roms.rcs.api.external.v2_0.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import inc.roms.rcs.api.external.v2_0.vo.AcceptCode;
import inc.roms.rcs.api.external.v2_0.vo.AcceptMessage;
import inc.roms.rcs.vo.common.StoreId;
import inc.roms.rcs.vo.location.GateId;
import lombok.Data;

import java.time.ZonedDateTime;

import static inc.roms.rcs.api.external.v2_0.vo.JapanTimeHelper.DATETIME_PATTERN;

@Data
public class DeliverOrderResponse {

    @JsonFormat(pattern = DATETIME_PATTERN)
    private ZonedDateTime receiveTime;
    private StoreId storeCode;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private GateId gate;
    private AcceptCode acceptCode;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private AcceptMessage acceptMessage;

}
