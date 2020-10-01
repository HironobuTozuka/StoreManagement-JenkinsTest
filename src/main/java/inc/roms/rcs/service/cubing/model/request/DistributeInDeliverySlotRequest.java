package inc.roms.rcs.service.cubing.model.request;

import inc.roms.rcs.service.cubing.model.ReservedInventory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DistributeInDeliverySlotRequest {
    private List<ReservedInventory> reservedInventory;
}
