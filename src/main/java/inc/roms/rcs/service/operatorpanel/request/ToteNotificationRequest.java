package inc.roms.rcs.service.operatorpanel.request;

import inc.roms.rcs.vo.location.LocationId;
import inc.roms.rcs.vo.tote.*;
import lombok.Data;

@Data
public class ToteNotificationRequest {

    private final ToteId toteId;
    private final TotePartitioning totePartitioning;
    private final ToteHeight toteHeight;
    private final ToteOrientation toteOrientation;
    private final ToteStatus toteStatus;

    private final LocationId location;
}
