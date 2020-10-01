package inc.roms.rcs.service.inventory.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListSupplyRequest {

    private boolean onlyForToday = true;

}
