package inc.roms.rcs.api.external.health;

import lombok.Data;

@Data
public class Details {
    private boolean acceptingOrders;
    private boolean acceptingPreorders;
    private boolean deliveringPreorders;
}
