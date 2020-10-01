package inc.roms.rcs.service.inventory.response;

import inc.roms.rcs.vo.sku.DeliveryTurn;
import inc.roms.rcs.vo.sku.DistributionType;
import inc.roms.rcs.vo.supply.SupplyId;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class SupplyDetails {

    private final DeliveryTurn deliveryTurn;
    private final DistributionType distributionType;
    private final SupplyId supplyId;
    private final LocalDate deliveryDate;

}
