package inc.roms.rcs.service.cubing.model.request;

import inc.roms.rcs.service.cubing.model.ReservedInventory;
import lombok.Data;

import java.util.List;

@Data
public class DeliveryDistributedBySlots {
    private final List<List<ReservedInventory>> reservedInventory;
}
