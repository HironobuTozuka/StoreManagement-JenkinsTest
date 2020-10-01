package inc.roms.rcs.service.operatorpanel;

import inc.roms.rcs.service.inventory.ToteManagementService;
import inc.roms.rcs.service.inventory.ToteService;
import inc.roms.rcs.service.inventory.domain.model.Tote;
import inc.roms.rcs.service.inventory.domain.model.ToteRequest;
import inc.roms.rcs.service.inventory.exception.ToteNotFoundException;
import inc.roms.rcs.service.location.LocationService;
import inc.roms.rcs.service.location.model.Location;
import inc.roms.rcs.service.operatorpanel.request.ToteNotificationRequest;
import inc.roms.rcs.service.operatorpanel.response.ToteNotificationResponse;
import inc.roms.rcs.service.order.domain.OrderProgressService;
import inc.roms.rcs.vo.location.LocationId;
import inc.roms.rcs.vo.tote.ToteStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoToteHandler {

    private final ToteService toteService;
    private final ToteManagementService toteManagementService;
    private final LocationService locationService;

    public ToteNotificationResponse handle(ToteNotificationRequest request) {
        Location location = locationService.getLocation(LocationId.LOADING_GATE);
        if(location.getCurrentTote() != null) {
            toteManagementService.clean(ToteRequest.from(request.getToteId()));
            toteService.markAsFailing(location.getCurrentTote().getToteId(), ToteStatus.REMOVED);
        }
        locationService.removeToteFromLocation(LocationId.LOADING_GATE);
        return ToteNotificationResponse.holdTote(request.getToteId());
    }
}
