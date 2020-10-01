package inc.roms.rcs.api.external.v1_0;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import inc.roms.rcs.service.inventory.SkuService;
import inc.roms.rcs.service.inventory.request.AddSkuRequest;
import inc.roms.rcs.service.inventory.response.SkuDetailsResponse;
import inc.roms.rcs.service.inventory.response.SkuListResponse;
import inc.roms.rcs.vo.sku.SkuId;
import lombok.RequiredArgsConstructor;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SkuController {

    private final SkuService skuService;

    @GetMapping(value = "/api/1.0/sku:details")
    public SkuDetailsResponse details(@RequestParam(name = "sku_id") SkuId skuId) {
        SkuDetailsResponse response = skuService.details(skuId);
        return response;
    }

    @GetMapping(value = "/api/1.0/sku:list")
    public SkuListResponse list() {
        SkuListResponse response = skuService.list();
        return response;
    }

    @PostMapping(value = "/api/1.0/sku:add")
    public void add(@RequestBody AddSkuRequest addSkuRequest) {
        log.info("Add sku request received: {}", addSkuRequest);
        skuService.add(addSkuRequest);
    }

}
