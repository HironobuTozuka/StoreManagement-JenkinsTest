package inc.roms.rcs.web.model;

import inc.roms.rcs.vo.common.Quantity;
import inc.roms.rcs.vo.sku.ExternalId;
import lombok.Data;

@Data
public class InvItem {

    private ExternalId id;
    private String unit = "pcs";
    private Quantity qty;
}
