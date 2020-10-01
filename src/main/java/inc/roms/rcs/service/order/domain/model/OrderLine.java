package inc.roms.rcs.service.order.domain.model;

import inc.roms.rcs.service.inventory.domain.model.Reservation;
import inc.roms.rcs.vo.common.Quantity;
import inc.roms.rcs.vo.order.OrderId;
import inc.roms.rcs.vo.order.OrderLineId;
import inc.roms.rcs.vo.sku.SkuId;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@ToString(exclude = {"order"})
public class OrderLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private SkuId skuId;

    private OrderLineId orderLineId;

    @ManyToOne
    @JoinColumn(name = "customer_order_id")
    private Order order;

    private Quantity quantity;

    @AttributeOverrides({@AttributeOverride(name="quantity", column=@Column(name="picked"))})
    private Quantity picked;

    @AttributeOverrides({@AttributeOverride(name="quantity", column=@Column(name="failed"))})
    private Quantity failed;

    public void addFailed(Quantity failed) {
        this.failed = failed.plus(this.failed);
    }

    public void addPicked(Quantity picked) {
        this.picked = picked.plus(this.picked);
    }
}
