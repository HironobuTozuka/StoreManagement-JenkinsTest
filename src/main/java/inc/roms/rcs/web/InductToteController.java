package inc.roms.rcs.web;

import inc.roms.rcs.service.inventory.domain.model.Tote;
import inc.roms.rcs.service.machineoperator.exception.MachineOperatorException;
import inc.roms.rcs.service.machineoperator.exception.MachineOperatorUnavailableException;
import inc.roms.rcs.service.operatorpanel.LoadingGateService;
import inc.roms.rcs.service.operatorpanel.request.InductRequest;
import inc.roms.rcs.service.operatorpanel.response.InductResponse;
import inc.roms.rcs.web.model.Notifications;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.annotations.ApiIgnore;

@Controller
@AllArgsConstructor
@Slf4j
@ApiIgnore
public class InductToteController {

    private final LoadingGateService loadingGateService;

    @GetMapping("/induct/tote")
    public ModelAndView initInduct() {
        try {
            log.info("Initializing tote induct");
            loadingGateService.openLoadingGate();
            return new ModelAndView("induct/tote-scan", "totePrototype", new Tote());
        } catch (MachineOperatorUnavailableException moue) {
            return new ModelAndView("home", "notifications", Notifications.error("Cant communicate with Machine Operator, contact support!"));
        } catch (MachineOperatorException moe) {
            return new ModelAndView("home", "notifications", Notifications.error("There was a problem with requesting gate open, contact support!"));
        }
    }

    @PostMapping("/induct/tote-scan")
    public ModelAndView toteScan(@ModelAttribute Tote totePrototype) {
        Tote tote = loadingGateService.findToteByToteId(totePrototype.getToteId());
        log.info("Tote in loading gate: {}", tote);
        return new ModelAndView("induct/sku-scan", "totePrototype", tote);
    }

    @PostMapping("/induct/sku-scan")
    public ModelAndView skuScan(@ModelAttribute InductRequest inductRequest) {
        log.info("Induct request: {}", inductRequest);
        InductResponse inductResponse = loadingGateService.induct(inductRequest);
        return new ModelAndView("home", "notifications", Notifications.success("Tote " + inductResponse.getDetails().getToteId() + " successfully inducted! Tap 'Induct Tote' to induct next one." ));
    }
}
