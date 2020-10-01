package inc.roms.rcs.service.inventory.domain.model;

import inc.roms.rcs.service.order.domain.model.OrderLine;
import inc.roms.rcs.vo.common.Quantity;
import inc.roms.rcs.vo.sku.SkuId;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Data
@ToString(exclude = "deliveryInventory")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private OrderLine orderLine;

    private Quantity quantity;

    private SkuId skuId;

    @ManyToOne
    @JoinTable(name = "delivery_inventory_reservations",
            joinColumns = @JoinColumn(
                    name = "reservations_id",
                    referencedColumnName = "id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "delivery_inventory_id",
                    referencedColumnName = "id"
            ))
    private DeliveryInventory deliveryInventory;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        if(id != null || that.id != null)
            return Objects.equals(id, that.id);
        else
            return super.equals(o);
    }

    @Override
    public int hashCode() {
        if(id != null)
            return Objects.hash(id);
        else
            return super.hashCode();
    }
}
