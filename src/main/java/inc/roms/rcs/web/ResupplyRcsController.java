package inc.roms.rcs.web;

import inc.roms.rcs.service.inventory.exception.SkuNotFoundException;
import inc.roms.rcs.service.inventory.domain.model.Tote;
import inc.roms.rcs.service.operatorpanel.exception.NoSpaceForStockException;
import inc.roms.rcs.service.operatorpanel.LoadingGateService;
import inc.roms.rcs.service.operatorpanel.request.TotesForSkuRequest;
import inc.roms.rcs.vo.common.Quantity;
import inc.roms.rcs.web.model.Notifications;
import inc.roms.rcs.web.model.ResupplyModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
@ApiIgnore
public class ResupplyRcsController {

    private final LoadingGateService loadingGateService;

    @GetMapping("/resupply/init")
    public ModelAndView init() {
        return new ModelAndView("/resupply/scan-product", "totesForSku", new TotesForSkuRequest());
    }

    @PostMapping("/resupply/wait-for-tote")
    public ModelAndView waitForTote(@ModelAttribute TotesForSkuRequest totesForSku) {
        try {
            Map<Tote, Quantity> toteQuantityMap = loadingGateService.requestTotesWithAssignedSkuOrGetEmptyTotes(totesForSku);
            return new ModelAndView("/resupply/wait-for-tote", "numberOfTotes", toteQuantityMap.size());
        } catch (NoSpaceForStockException nete) {
            if(nete.getNumberOfTotes() > 0) {
                ModelAndView modelAndView = new ModelAndView("/resupply/wait-for-tote");
                modelAndView.getModel().put("numberOfTotes", nete.getNumberOfTotes());
                modelAndView.getModel().put("notifications", Notifications.error("Not enough empty totes found in RCS, induct more empty totes! Requested " + nete.getNumberOfTotes()));
                return modelAndView;
            } else {
                return new ModelAndView("home", "notifications", Notifications.error("No empty totes in RCS, free some totes before resupplying RCS"));
            }
        } catch (SkuNotFoundException snfe) {
            ModelAndView modelAndView = new ModelAndView("/resupply/scan-product");
            modelAndView.getModel().put("totesForSku", new TotesForSkuRequest());
            modelAndView.getModel().put("notifications", Notifications.error("Sku " + totesForSku.getSkuId() + " does not exist in RCS db!"));
            return modelAndView;
        }
    }

    @PostMapping("/resupply/tote-arrived")
    public ModelAndView scanSku(@ModelAttribute ResupplyModel resupplyModel) {
        Tote tote = loadingGateService.findToteByToteId(resupplyModel.getInductRequest().getToteId());
        loadingGateService.turnLightsOnOverEmptySlots(tote);

        Integer numberOfTotesLeft = resupplyModel.getNumberOfTotesLeft() - 1;
        ModelAndView modelAndView = new ModelAndView("/resupply/empty-slots");
        modelAndView.getModel().put("tote", tote);
        modelAndView.getModel().put("numberOfTotesLeft", numberOfTotesLeft);
        return modelAndView;
    }

    @PostMapping(value = "/resupply/tote-loaded", params = "action=next-tote")
    public ModelAndView inductToteAndWaitForNext(@ModelAttribute ResupplyModel resupplyModel) {
        loadingGateService.induct(resupplyModel.getInductRequest());
        ModelAndView modelAndView = new ModelAndView("/resupply/wait-for-tote");
        modelAndView.getModel().put("notifications", Notifications.success("Rcs resupplied successfully"));
        modelAndView.getModel().put("numberOfTotes", resupplyModel.getNumberOfTotesLeft());
        return modelAndView;
    }

    @PostMapping(value = "/resupply/tote-loaded", params = "action=finish")
    public ModelAndView inductToteAndFinish(@ModelAttribute ResupplyModel resupplyModel) {
        loadingGateService.induct(resupplyModel.getInductRequest());
        return new ModelAndView("home", "notifications", Notifications.success("Rcs resupplied successfully"));
    }

    @PostMapping(value = "/resupply/tote-loaded", params = "action=continue")
    public ModelAndView inductToteAndContinue(@ModelAttribute ResupplyModel resupplyModel) {
        loadingGateService.induct(resupplyModel.getInductRequest());
        ModelAndView modelAndView = new ModelAndView("/resupply/scan-product", "totesForSku", new TotesForSkuRequest());
        modelAndView.getModel().put("notifications", Notifications.success("Rcs resupplied successfully"));
        return modelAndView;
    }

}
