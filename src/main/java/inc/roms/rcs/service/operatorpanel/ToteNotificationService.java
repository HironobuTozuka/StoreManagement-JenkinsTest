package inc.roms.rcs.service.operatorpanel;

import inc.roms.rcs.service.operatorpanel.request.ToteNotificationRequest;
import inc.roms.rcs.service.operatorpanel.response.ToteNotificationResponse;
import inc.roms.rcs.vo.tote.ToteStatus;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import static inc.roms.rcs.vo.location.LocationId.LOADING_GATE;
import static inc.roms.rcs.vo.location.LocationId.TECHNICAL;

@Service
@AllArgsConstructor
public class ToteNotificationService {

    private final LoadingGateUnknownToteHandler loadingGateUnknownToteHandler;
    private final NoReadNotificationHandler noReadNotificationHandler;
    private final TechnicalLocationToteHandler technicalLocationToteHandler;
    private final NoToteHandler noToteHandler;

    public final static String NOTOTE = "NOTOTE";

    public ToteNotificationResponse handle(ToteNotificationRequest request) {
        if(NOTOTE.equalsIgnoreCase(request.getToteId().getToteId())) return noToteHandler.handle(request);
        if(LOADING_GATE.equals(request.getLocation())) return loadingGateUnknownToteHandler.handle(request);
        if(ToteStatus.NO_READ.equals(request.getToteStatus())) return noReadNotificationHandler.handle(request);
        if(TECHNICAL.equals(request.getLocation())) return technicalLocationToteHandler.handle(request);

        return ToteNotificationResponse.rejectTote(request.getToteId());
    }
}
