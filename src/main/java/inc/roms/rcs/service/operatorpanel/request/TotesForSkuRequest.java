package inc.roms.rcs.service.operatorpanel.request;

import inc.roms.rcs.vo.common.Quantity;
import inc.roms.rcs.vo.sku.SkuId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TotesForSkuRequest {

    private SkuId skuId;
    private Quantity quantity;

}
