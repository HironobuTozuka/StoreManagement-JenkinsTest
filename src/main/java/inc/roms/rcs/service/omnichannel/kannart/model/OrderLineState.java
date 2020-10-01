package inc.roms.rcs.service.omnichannel.kannart.model;

import inc.roms.rcs.vo.common.Quantity;
import inc.roms.rcs.vo.sku.SkuId;
import lombok.Data;

@Data
public class OrderLineState {

    private SkuId skuId;
    private Quantity requestedQuantity;
    private Quantity pickedQuantity;
    private Quantity failedQuantity;

}
