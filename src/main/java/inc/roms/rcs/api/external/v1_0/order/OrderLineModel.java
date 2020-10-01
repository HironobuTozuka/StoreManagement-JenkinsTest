package inc.roms.rcs.api.external.v1_0.order;

import inc.roms.rcs.vo.common.Quantity;
import inc.roms.rcs.vo.sku.ExternalId;
import lombok.Data;

@Data
class OrderLineModel {

    private ExternalId skuId;
    private Quantity quantity;

}
