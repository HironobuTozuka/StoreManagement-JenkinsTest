package inc.roms.rcs.service.inventory.response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import inc.roms.rcs.service.inventory.domain.model.DeliveryInventory;
import inc.roms.rcs.service.inventory.domain.model.Reservation;
import inc.roms.rcs.service.inventory.domain.model.SkuBatch;
import inc.roms.rcs.vo.order.OrderId;
import inc.roms.rcs.vo.sku.SkuId;
import lombok.Getter;

@Getter
public class DeliveryInventoryDetails {

    private OrderId orderId;
    private Map<SkuId, SkuBatchDetails> skuBatches = new HashMap<>();
    private List<ReservationDetails> reservations = new ArrayList<>();

    public static DeliveryInventoryDetails convert(DeliveryInventory deliveryInventory) {
        if (Objects.isNull(deliveryInventory)) {
            return null;
        }
        DeliveryInventoryDetails details = new DeliveryInventoryDetails();
        details.orderId = deliveryInventory.getOrderId();
        details.skuBatches = convertSkuBatches(deliveryInventory.getSkuBatches());
        details.reservations = convertReservations(deliveryInventory.getReservations());
        return details;
    }

    private static Map<SkuId, SkuBatchDetails> convertSkuBatches(Map<SkuId, SkuBatch> skuBatches) {
        Map<SkuId, SkuBatchDetails> map = new HashMap<>();
        skuBatches.entrySet().forEach(e -> {
            map.put(e.getKey(), SkuBatchDetails.convert(e.getValue()));
        });
        return map;
    }

    private static List<ReservationDetails> convertReservations(List<Reservation> reservations) {
        List<ReservationDetails> list = new ArrayList<>();
        for (Reservation reservation : reservations) {
            list.add(ReservationDetails.convert(reservation));
        }
        return list;
    }
}
