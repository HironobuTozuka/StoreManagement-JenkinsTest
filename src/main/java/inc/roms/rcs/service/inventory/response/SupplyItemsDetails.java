package inc.roms.rcs.service.inventory.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import inc.roms.rcs.vo.common.Quantity;
import inc.roms.rcs.vo.sku.Category;
import inc.roms.rcs.vo.sku.Name;
import inc.roms.rcs.vo.sku.SkuId;
import inc.roms.rcs.vo.supply.ScheduledSupplyItemId;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SupplyItemsDetails {

    private ScheduledSupplyItemId itemId;
    private SkuId skuId;
    private Name skuName;
    private Category categoryId;

    @JsonProperty("expected_quantity")
    private Quantity expectedQuantity;

    @JsonProperty("inducted_quantity")
    private Quantity inductedQuantity;

    private LocalDateTime sellByDate;
}
