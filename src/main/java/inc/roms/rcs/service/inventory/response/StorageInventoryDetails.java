package inc.roms.rcs.service.inventory.response;

import java.util.ArrayList;
import java.util.List;

import inc.roms.rcs.service.inventory.domain.model.Reservation;
import inc.roms.rcs.service.inventory.domain.model.StorageInventory;
import inc.roms.rcs.vo.common.Quantity;
import lombok.Getter;

@Getter
public class StorageInventoryDetails {

    private Quantity available;
    private List<ReservationDetails> reservations = new ArrayList<>();
    private SkuBatchDetails skuBatch;

    public static StorageInventoryDetails convert(StorageInventory storageInventory) {
        if (storageInventory == null)
            return null;
        StorageInventoryDetails details = new StorageInventoryDetails();
        details.available = storageInventory.getAvailable();
        details.reservations = convertReservations(storageInventory.getReservations());
        details.skuBatch = SkuBatchDetails.convert(storageInventory.getSkuBatch());
        return details;
    }

    private static List<ReservationDetails> convertReservations(List<Reservation> reservations) {
        List<ReservationDetails> list = new ArrayList<>();
        for (Reservation reservation : reservations) {
            list.add(ReservationDetails.convert(reservation));
        }
        return list;
    }

}
