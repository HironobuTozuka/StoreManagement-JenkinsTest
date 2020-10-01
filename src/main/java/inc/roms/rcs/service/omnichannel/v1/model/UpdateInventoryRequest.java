package inc.roms.rcs.service.omnichannel.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import inc.roms.rcs.vo.common.Quantity;
import inc.roms.rcs.vo.common.StoreId;
import inc.roms.rcs.vo.common.UserId;
import inc.roms.rcs.vo.sku.ExternalId;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UpdateInventoryRequest {

    @JsonProperty("sku_id")
    private ExternalId skuId;

    @JsonProperty("delta")
    private Quantity delta;

    private StoreId storeId;

    @JsonProperty("operator_id")
    private UserId operatorId;

    @JsonProperty("update_date")
    private LocalDateTime updateDate;

}
