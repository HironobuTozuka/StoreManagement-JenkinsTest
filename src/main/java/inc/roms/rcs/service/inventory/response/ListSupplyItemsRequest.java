package inc.roms.rcs.service.inventory.response;

import inc.roms.rcs.vo.supply.SupplyId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListSupplyItemsRequest {
    private SupplyId supplyId;
}
