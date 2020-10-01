package inc.roms.rcs.service.operatorpanel.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import inc.roms.rcs.vo.common.Quantity;
import inc.roms.rcs.vo.sku.SkuId;
import inc.roms.rcs.vo.supply.ScheduledSupplyItemId;
import lombok.Data;

@Data
public class StorageSlotModel {

    @JsonProperty(required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ScheduledSupplyItemId supplyItemId;

    private Quantity quantity;
    private SkuId skuId;
    private int ordinal;
    private int displayOrdinal;
}
