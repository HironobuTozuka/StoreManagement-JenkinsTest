package inc.roms.rcs.web;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.annotations.ApiIgnore;

@Controller
@AllArgsConstructor
@Slf4j
@ApiIgnore
public class HmiPanelController {

    @GetMapping("/hmi/panel")
    public ModelAndView showPanel() {
        return new ModelAndView("hmi/panel");
    }
}
