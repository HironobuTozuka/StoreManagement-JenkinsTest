package inc.roms.rcs.service.inventory.response;

import inc.roms.rcs.service.inventory.domain.model.Slot;
import inc.roms.rcs.service.inventory.domain.model.Tote;
import lombok.Data;

import static inc.roms.rcs.vo.tote.ToteOrientation.REVERSED;
import static inc.roms.rcs.vo.tote.TotePartitioning.BIPARTITE;

@Data
public class SlotDetails {

    private int ordinal;
    private int displayOrdinal;
    private StorageInventoryDetails storageInventory;
    private DeliveryInventoryDetails deliveryInventory;

    public static SlotDetails convert(Tote tote, Slot slot) {
        SlotDetails details = new SlotDetails();
        details.setOrdinal(slot.getOrdinal());
        details.setDisplayOrdinal(toDisplayOrdinal(tote, slot.getOrdinal()));
        details.setStorageInventory(StorageInventoryDetails.convert(slot.getStorageInventory()));
        details.setDeliveryInventory(DeliveryInventoryDetails.convert(slot.getDeliveryInventory()));
        return details;
    }

    public static int toDisplayOrdinal(Tote tote, int ordinal) {
        int o = ordinal;
        if (REVERSED.equals(tote.getToteOrientation())) {
            o = tote.getToteType().getTotePartitioning().getNumberOfSlots() - 1 - ordinal;
        }

        if(BIPARTITE.equals(tote.getToteType().getTotePartitioning())) {
            return 2 * o;
        } else {
            return o;
        }
    }
}
