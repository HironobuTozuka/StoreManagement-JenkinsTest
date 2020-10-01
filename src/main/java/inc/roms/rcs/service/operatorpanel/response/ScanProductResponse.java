package inc.roms.rcs.service.operatorpanel.response;

import inc.roms.rcs.vo.sku.SkuId;
import lombok.Data;

@Data
public class ScanProductResponse {

    private final SkuId skuId;

    public static ScanProductResponse from(String barcode) {
        return new ScanProductResponse(SkuId.from(barcode));
    }
}
