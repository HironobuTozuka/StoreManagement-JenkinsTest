package inc.roms.rcs.service.omnichannel.kannart.model;

import inc.roms.rcs.vo.common.RcsOperationId;
import inc.roms.rcs.vo.common.StoreId;
import inc.roms.rcs.vo.sku.SkuId;
import lombok.Data;

@Data
public class ProductReadyRequest {

    private StoreId storeCode;
    private SkuId sku;
    private RcsOperationId rcsOperationId;

}
