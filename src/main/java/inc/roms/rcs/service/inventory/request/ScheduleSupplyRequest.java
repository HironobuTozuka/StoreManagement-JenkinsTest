package inc.roms.rcs.service.inventory.request;

import inc.roms.rcs.vo.common.Quantity;
import inc.roms.rcs.vo.sku.DeliveryTurn;
import inc.roms.rcs.vo.sku.DistributionType;
import inc.roms.rcs.vo.sku.SkuId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleSupplyRequest {

    private LocalDate deliveryDate;
    private DeliveryTurn deliveryTurn;
    private DistributionType distributionType;
    private SkuId skuId;
    private Quantity quantity;
    private LocalDateTime sellByDate;

}
