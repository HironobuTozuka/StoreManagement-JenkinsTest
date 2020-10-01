package inc.roms.rcs.service.operatorpanel.response;

import inc.roms.rcs.vo.common.ResponseCode;
import lombok.Data;

@Data
public class InductResponse {

    private final ResponseCode responseCode;
    private final InductResponseDetails details;

}
