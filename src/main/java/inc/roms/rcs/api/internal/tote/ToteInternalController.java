package inc.roms.rcs.api.internal.tote;


import inc.roms.rcs.api.error.annotations.ReportIssueOnBusinessException;
import inc.roms.rcs.service.inventory.ToteManagementService;
import inc.roms.rcs.service.inventory.domain.model.ToteRequest;
import inc.roms.rcs.service.operatorpanel.LoadingGateService;
import inc.roms.rcs.service.operatorpanel.ToteNotificationService;
import inc.roms.rcs.service.operatorpanel.request.InductRequest;
import inc.roms.rcs.service.operatorpanel.request.ToteNotificationRequest;
import inc.roms.rcs.service.operatorpanel.response.InductResponse;
import inc.roms.rcs.service.operatorpanel.response.ToteNotificationResponse;
import inc.roms.rcs.vo.common.TransactionId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static inc.roms.rcs.vo.zones.ZoneFunction.LOADING_GATE;

@RestController
@RequiredArgsConstructor
@Slf4j
@ReportIssueOnBusinessException
//FIXME add response / requests tests
//FIXME add validation
public class ToteInternalController {

    private final LoadingGateService loadingGateService;
    private final ToteNotificationService toteNotificationService;
    private final ToteManagementService toteManagementService;

    @PostMapping("/api/internal/tote:notification")
    public ToteNotificationResponse toteNotification(@RequestBody ToteNotificationRequest request) {
        log.info("ToteNotificationRequest {}", request);
        return toteNotificationService.handle(request);
    }

    @PostMapping("/api/internal/tote:induct")
    public InductResponse induct(@RequestBody InductRequest inductRequest) {
        log.info("Induct request: {}", inductRequest);
        InductResponse induct = loadingGateService.induct(inductRequest);
        log.info("Induct response: {}", induct);
        return induct;
    }

    @PostMapping("/api/internal/tote:summon")
    public void summon(@RequestBody ToteRequest toteRequest) {
        loadingGateService.deliverTote(toteRequest.getToteId(), LOADING_GATE);
    }

    @PostMapping("/api/internal/tote:return")
    public void returnTote() {
        loadingGateService.closeLoadingGate(TransactionId.generate());
    }

    @PostMapping("/api/internal/tote:clean")
    public void clean(@RequestBody ToteRequest toteRequest) {
        toteManagementService.clean(toteRequest);
    }
}
