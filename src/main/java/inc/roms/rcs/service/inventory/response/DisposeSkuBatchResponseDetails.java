package inc.roms.rcs.service.inventory.response;

import inc.roms.rcs.vo.sku.SkuId;
import lombok.Data;

@Data
public class DisposeSkuBatchResponseDetails {

    private final SkuId notDisposedSku;
    private final SkuId disposedSku;

}
