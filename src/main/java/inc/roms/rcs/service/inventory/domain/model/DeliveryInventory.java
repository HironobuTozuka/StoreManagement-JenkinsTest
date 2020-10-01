package inc.roms.rcs.service.inventory.domain.model;

import inc.roms.rcs.vo.common.Quantity;
import inc.roms.rcs.vo.order.OrderId;
import inc.roms.rcs.vo.sku.SkuId;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.*;

@Data
@Entity
@ToString(exclude="reservations")
public class DeliveryInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private OrderId orderId;

    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
    @MapKey(name = "skuId")
    private Map<SkuId, SkuBatch> skuBatches = new HashMap<>();

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.DETACH}, fetch = FetchType.LAZY)
    private List<Reservation> reservations = new ArrayList<>();

    public boolean isEmpty() {
        return skuBatches.values().stream().allMatch(this::isEmpty);
    }

    private boolean isEmpty(SkuBatch skuBatch) {
        return skuBatch.getQuantity() == null || skuBatch.getQuantity().equals(Quantity.of(0));
    }

    public SkuBatch getOrCreateSkuBatch(SkuId skuId) {
        SkuBatch skuBatch = skuBatches.get(skuId);
        if(skuBatch == null) {
            skuBatch = new SkuBatch(skuId);
            skuBatches.put(skuId, skuBatch);
        }
        return skuBatch;
    }

    public void addSkuBatches(List<SkuBatch> skuBatches) {
        if(this.skuBatches == null) this.skuBatches = new HashMap<>();
        skuBatches.forEach(it -> this.skuBatches.put(it.getSkuId(), it));
    }

    public void addReservation(Reservation reservation) {
        if (this.reservations == null) this.reservations = new ArrayList<>();
        this.reservations.add(reservation);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeliveryInventory that = (DeliveryInventory) o;
        if(id != null  || that.id != null)
            return Objects.equals(id, that.id);
        else
            return super.equals(that);
    }

    @Override
    public int hashCode() {
        if(id != null)
            return Objects.hash(id);
        else
            return super.hashCode();
    }
}
