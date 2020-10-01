package inc.roms.rcs.builders;

import inc.roms.rcs.service.inventory.domain.model.DeliveryInventory;
import inc.roms.rcs.service.inventory.domain.model.SkuBatch;
import inc.roms.rcs.service.inventory.domain.model.Slot;
import inc.roms.rcs.vo.order.OrderId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class DeliverySlotBuilder {
    private List<SkuBatchBuilder> skuBatches = new ArrayList<>();
    private int ordinal;
    private OrderId orderId;

    public static DeliverySlotBuilder deliverySlot() {
        return new DeliverySlotBuilder();
    }

    public DeliverySlotBuilder skuBatches(SkuBatchBuilder... builders) {
        this.skuBatches = Arrays.asList(builders);
        return this;
    }

    public DeliverySlotBuilder orderId(OrderId orderId) {
        this.orderId = orderId;
        return this;
    }

    public DeliverySlotBuilder ordinal(int ordinal) {
        this.ordinal = ordinal;
        return this;
    }

    public Slot build() {
        Slot slot = new Slot();
        DeliveryInventory deliveryInventory = new DeliveryInventory();
        List<SkuBatch> skuBatches = this.skuBatches.stream()
                .map(SkuBatchBuilder::build).collect(toList());
        deliveryInventory.addSkuBatches(skuBatches);
        deliveryInventory.setOrderId(this.orderId);
        slot.setDeliveryInventory(deliveryInventory);
        slot.setOrdinal(ordinal);
        return slot;
    }
}
