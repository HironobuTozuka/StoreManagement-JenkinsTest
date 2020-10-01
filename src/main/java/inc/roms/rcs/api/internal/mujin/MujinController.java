package inc.roms.rcs.api.internal.mujin;

import inc.roms.rcs.service.mujin.MujinService;
import inc.roms.rcs.vo.sku.SkuId;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MujinController {

    private final MujinService mujinService;

    @GetMapping("/mujin/{skuId}/registered")
    public boolean isRegistered(@PathVariable SkuId skuId) {
        return mujinService.isRegistered(skuId);
    }

}
