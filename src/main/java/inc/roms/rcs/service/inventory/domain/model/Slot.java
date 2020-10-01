package inc.roms.rcs.service.inventory.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
//TODO slot czy storage_slot
public class Slot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private int ordinal;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private StorageInventory storageInventory;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private DeliveryInventory deliveryInventory;

    public Slot(int ordinal) {
        this.ordinal = ordinal;
    }

    @Transient
    public boolean isEmpty() {
        return (storageInventory == null || storageInventory.isEmpty()) && (deliveryInventory == null || deliveryInventory.isEmpty());
    }
}
