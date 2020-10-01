package inc.roms.rcs.service.operatorpanel.model;

import inc.roms.rcs.service.inventory.domain.model.SkuBatch;
import inc.roms.rcs.vo.common.Quantity;
import inc.roms.rcs.vo.sku.SkuId;
import lombok.Data;

@Data
public class SkuBatchModel {

    private final SkuId skuId;
    private final Quantity quantity;

    public SkuBatchModel(SkuBatch it) {
        skuId = it.getSkuId();
        quantity = it.getQuantity();
    }

}
