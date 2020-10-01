package inc.roms.rcs.api.external.v2_0;

import inc.roms.rcs.service.inventory.request.ImportSkusRequest;
import inc.roms.rcs.service.inventory.SkuManagementService;
import inc.roms.rcs.service.inventory.request.AddSkuRequest;
import inc.roms.rcs.service.inventory.response.ImportSkusResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SkuController_v2 {

    private final SkuManagementService skuService;

    @PostMapping(value = "/api/2.0/sku:add")
    public void add(@RequestBody AddSkuRequest addSkuRequest) {
        skuService.add(addSkuRequest);
    }

    @PostMapping(value = "/api/2.0/sku:upload", consumes = "text/csv")
    public ImportSkusResponse importInputStream(@RequestBody InputStream body) {
        return skuService.importSkus(new ImportSkusRequest(body));
    }

    @PostMapping(value = "/api/2.0/sku:upload", consumes = "multipart/form-data")
    public ImportSkusResponse importFile(@RequestParam("file") MultipartFile file) throws IOException {
        return skuService.importSkus(new ImportSkusRequest(file.getInputStream()));
    }
}
