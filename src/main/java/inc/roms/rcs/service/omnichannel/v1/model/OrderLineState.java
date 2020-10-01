package inc.roms.rcs.service.omnichannel.v1.model;

import inc.roms.rcs.vo.common.Quantity;
import inc.roms.rcs.vo.sku.ExternalId;
import lombok.Data;

@Data
public class OrderLineState {

    private ExternalId skuId;
    private Quantity requestedQuantity;
    private Quantity pickedQuantity;
    private Quantity failedQuantity;

}
