package inc.roms.rcs.builders;

import inc.roms.rcs.service.inventory.domain.model.Reservation;
import inc.roms.rcs.service.inventory.domain.model.SkuBatch;
import inc.roms.rcs.service.inventory.domain.model.Slot;
import inc.roms.rcs.service.inventory.domain.model.StorageInventory;
import inc.roms.rcs.vo.common.Quantity;
import inc.roms.rcs.vo.sku.SkuId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StorageSlotBuilder {
    private SkuId skuId;
    private int quantity;
    private int ordinal;
    private List<Reservation> reservations;

    public static StorageSlotBuilder storageSlot() {
        return new StorageSlotBuilder();
    }

    public StorageSlotBuilder skuId(SkuId skuId) {
        this.skuId = skuId;
        return this;
    }

    public StorageSlotBuilder quantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    public StorageSlotBuilder ordinal(int ordinal) {
        this.ordinal = ordinal;
        return this;
    }

    public StorageSlotBuilder reservations(Reservation... reservations) {
        this.reservations = new ArrayList<>();
        this.reservations.addAll(Arrays.asList(reservations));
        return this;
    }

    public Slot build() {
        Slot slot = new Slot();
        StorageInventory storageInventory = new StorageInventory();
        SkuBatch skuBatch = new SkuBatch();
        skuBatch.setQuantity(Quantity.of(quantity));
        skuBatch.setSkuId(skuId);
        storageInventory.setSkuBatch(skuBatch);
        if(reservations != null)
            storageInventory.setReservations(reservations);

        slot.setStorageInventory(storageInventory);
        slot.setOrdinal(ordinal);
        return slot;
    }
}
