package inc.roms.rcs.builders;

import inc.roms.rcs.service.inventory.domain.model.SkuBatch;
import inc.roms.rcs.vo.common.Quantity;
import inc.roms.rcs.vo.sku.SkuId;

public class SkuBatchBuilder {
    private SkuId skuId;
    private int quantity;

    public SkuBatchBuilder skuId(SkuId skuId) {
        this.skuId = skuId;
        return this;
    }

    public SkuBatchBuilder quantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    public SkuBatch build() {
        return new SkuBatch(skuId, Quantity.of(quantity));
    }

}
