package inc.roms.rcs.api.internal.sku;

import inc.roms.rcs.service.inventory.SkuManagementService;
import inc.roms.rcs.service.inventory.SkuService;
import inc.roms.rcs.service.inventory.request.ImportSkusRequest;
import inc.roms.rcs.service.inventory.response.ImportSkusResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequiredArgsConstructor
public class SkuInternalController {

    private final SkuManagementService skuService;

    @PostMapping(value = "/api/internal/sku:upload", consumes = "text/csv")
    public ImportSkusResponse importInputStream(@RequestBody InputStream body) {
        return skuService.importSkus(new ImportSkusRequest(body));
    }

    @PostMapping(value = "/api/internal/sku:upload", consumes = "multipart/form-data")
    public ImportSkusResponse importFile(@RequestParam("file") MultipartFile file) throws IOException {
        return skuService.importSkus(new ImportSkusRequest(file.getInputStream()));
    }

}
