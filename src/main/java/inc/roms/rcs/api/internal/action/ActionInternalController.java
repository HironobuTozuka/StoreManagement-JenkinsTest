package inc.roms.rcs.api.internal.action;

import inc.roms.rcs.service.operatorpanel.LoadingGateService;
import inc.roms.rcs.vo.common.TransactionId;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ActionInternalController {

    private final LoadingGateService loadingGateService;

    @PostMapping("/api/internal/loading-gate:open")
    public void openLoadingGate() {
        loadingGateService.openLoadingGate();
    }


    @PostMapping("/api/internal/loading-gate:close")
    public void closeLoadingGate() {
        loadingGateService.closeLoadingGate(TransactionId.generate());
    }

}
