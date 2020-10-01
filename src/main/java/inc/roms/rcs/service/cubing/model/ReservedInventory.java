package inc.roms.rcs.service.cubing.model;

import inc.roms.rcs.vo.common.Quantity;
import inc.roms.rcs.service.inventory.domain.model.Sku;
import inc.roms.rcs.service.inventory.domain.model.Reservation;
import inc.roms.rcs.service.inventory.domain.model.Tote;
import lombok.Data;

@Data
public class ReservedInventory {

    private Sku sku;
    private Quantity quantity;
    private Reservation reservation;
    private Tote sourceTote;
    private Integer sourceSlotOrdinal;

    public Double volume() {
        return sku.getDimensions().volume() * quantity.getQuantity();
    }
}
