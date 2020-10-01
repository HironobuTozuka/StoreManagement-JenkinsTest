package inc.roms.rcs.service.operatorpanel;

import inc.roms.rcs.service.location.LocationService;
import inc.roms.rcs.service.inventory.ToteService;
import inc.roms.rcs.service.inventory.domain.model.Tote;
import inc.roms.rcs.vo.location.LocationId;
import inc.roms.rcs.vo.tote.ToteStatus;
import inc.roms.rcs.vo.tote.ToteType;
import inc.roms.rcs.service.operatorpanel.request.ToteNotificationRequest;
import inc.roms.rcs.service.operatorpanel.response.ToteNotificationResponse;
import inc.roms.rcs.service.inventory.domain.model.ToteResponse;
import inc.roms.rcs.websocket.WebsocketService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class LoadingGateUnknownToteHandler {

    private final ToteService toteService;
    private final LocationService locationService;
    private final WebsocketService websocketService;

    public ToteNotificationResponse handle(ToteNotificationRequest request) {
        Tote totePrototype = toteService.findToteByToteId(request.getToteId()).orElse(new Tote());
        totePrototype.setToteId(request.getToteId());
        totePrototype.setToteType(new ToteType(request.getTotePartitioning(), request.getToteHeight()));
        totePrototype.setToteOrientation(request.getToteOrientation());
        totePrototype.setToteStatus(ToteStatus.INDUCT_IN_PROGRESS);
        Tote tote = toteService.updateTote(totePrototype);

        locationService.relocateTote(tote, request.getLocation());

        websocketService.send(ToteResponse.from(tote));

        return ToteNotificationResponse.holdTote(request.getToteId());
    }
}
