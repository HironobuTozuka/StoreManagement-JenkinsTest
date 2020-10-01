package inc.roms.rcs.api.external.v2_0.action;

import com.fasterxml.jackson.annotation.JsonInclude;
import inc.roms.rcs.vo.location.GateId;
import lombok.Data;

@Data
public class ActionAcceptMessage {

    private ActionErrorCode errorCode;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private GateId gate;
}
