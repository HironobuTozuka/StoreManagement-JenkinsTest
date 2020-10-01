package inc.roms.rcs.service.inventory.domain.model;

import inc.roms.rcs.vo.sku.DeliveryTurn;
import inc.roms.rcs.vo.sku.DistributionType;
import inc.roms.rcs.vo.supply.SupplyId;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class ScheduledSupply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private SupplyId supplyId;

    @Enumerated(EnumType.STRING)
    private DistributionType distributionType;

    private LocalDate deliveryDate;

    @Enumerated(EnumType.STRING)
    private DeliveryTurn deliveryTurn;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "scheduled_supply_id")
    private List<ScheduledSupplyItem> items = new ArrayList<>();

    public static ScheduledSupply createNew() {
        ScheduledSupply scheduledSupply = new ScheduledSupply();
        scheduledSupply.setSupplyId(SupplyId.generate());
        return scheduledSupply;
    }
}
