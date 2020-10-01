package inc.roms.rcs.service.operatorpanel.request;

import inc.roms.rcs.vo.sku.SkuId;
import inc.roms.rcs.vo.supply.SupplyId;
import lombok.Data;

import java.util.List;

@Data
public class SupplyToteRequest {
    private List<SkuId> skuIds;
}
