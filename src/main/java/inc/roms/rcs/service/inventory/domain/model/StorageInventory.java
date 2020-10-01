package inc.roms.rcs.service.inventory.domain.model;

import inc.roms.rcs.vo.common.Quantity;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static inc.roms.rcs.service.inventory.domain.model.SkuBatchState.DISPOSED;

@Data
@Slf4j
@Entity
public class StorageInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @AttributeOverrides({@AttributeOverride(name="quantity", column=@Column(name="available"))})
    private Quantity available;

    @OneToMany(cascade = {CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    private List<Reservation> reservations = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private SkuBatch skuBatch;

    public boolean isEmpty() {
        return skuBatch == null || skuBatch.getQuantity() == null || Quantity.of(0).equals(skuBatch.getQuantity());
    }

    @PrePersist
    @PreUpdate
    public void recalculate() {
        Quantity newAvailable = this.calculateAvailable();
        log.debug("StorageInventory: {} , Calculating new available quantity, result: {}", id, newAvailable);
        this.available = newAvailable;
    }

    @Transient
    public Quantity getQuantity() {
        return skuBatch != null ? skuBatch.getQuantity() : Quantity.of(0);
    }

    public void addReservation(Reservation reservation) {
        this.reservations.add(reservation);
        this.available = calculateAvailable();
    }

    private Quantity calculateAvailable() {
        if(skuBatch != null && DISPOSED.equals(skuBatch.getState())) return Quantity.of(0);
        return getQuantity().minus(reservations.stream().map(Reservation::getQuantity).reduce(Quantity::plus).orElse(Quantity.of(0)));
    }
}
