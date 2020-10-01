package inc.roms.rcs.service.inventory.response;

import java.time.LocalDateTime;

import inc.roms.rcs.service.inventory.domain.model.SkuBatch;
import inc.roms.rcs.service.inventory.domain.model.SkuBatchState;
import inc.roms.rcs.vo.common.Quantity;
import inc.roms.rcs.vo.sku.SkuId;
import lombok.Data;

@Data
public class SkuBatchDetails {

    private SkuId skuId;
    private Quantity quantity;
    private LocalDateTime sellByDate;
    private SkuBatchState state;

    public static SkuBatchDetails convert(SkuBatch skuBatch) {
        SkuBatchDetails details = new SkuBatchDetails();
        details.skuId = skuBatch.getSkuId();
        details.quantity = skuBatch.getQuantity();
        details.sellByDate = skuBatch.getSellByDate();
        details.state = skuBatch.getState();
        return details;
    }

}
