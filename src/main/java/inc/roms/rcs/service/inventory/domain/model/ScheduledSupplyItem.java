package inc.roms.rcs.service.inventory.domain.model;

import inc.roms.rcs.vo.common.Quantity;
import inc.roms.rcs.vo.sku.SkuId;
import inc.roms.rcs.vo.supply.ScheduledSupplyItemId;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
public class ScheduledSupplyItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private SkuId skuId;

    private Quantity quantity;

    private ScheduledSupplyItemId scheduledSupplyItemId;

    @AttributeOverrides({@AttributeOverride(name="quantity", column=@Column(name="inducted_quantity"))})
    private Quantity inductedQuantity;

    private LocalDateTime sellByDate;

    @ManyToOne
    private ScheduledSupply scheduledSupply;
}
