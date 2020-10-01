package inc.roms.rcs.service.operatorpanel.response;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import inc.roms.rcs.vo.location.LocationId;
import inc.roms.rcs.vo.tote.ToteId;
import lombok.Data;

@Data
public class ToteNotificationResponse {


    @JsonUnwrapped
    private final ToteId toteId;
    private final Action action;
    private final LocationId targetLocation;

    public static ToteNotificationResponse holdTote(ToteId toteId) {
        return new ToteNotificationResponse(toteId, Action.HOLD, null);
    }

    public static ToteNotificationResponse rejectTote(ToteId toteId) {
        return new ToteNotificationResponse(toteId, Action.REJECT, null);
    }
}
