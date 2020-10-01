package inc.roms.rcs.service.order.response;

import inc.roms.rcs.vo.common.Quantity;
import inc.roms.rcs.vo.sku.SkuId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RejectedSku {

    private SkuId skuId;
    private Reason reason;
    private Quantity quantity;

}
