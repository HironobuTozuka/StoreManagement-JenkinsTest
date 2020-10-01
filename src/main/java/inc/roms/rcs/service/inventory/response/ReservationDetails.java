package inc.roms.rcs.service.inventory.response;

import inc.roms.rcs.service.inventory.domain.model.Reservation;
import inc.roms.rcs.vo.common.Quantity;
import inc.roms.rcs.vo.order.OrderId;
import inc.roms.rcs.vo.sku.SkuId;
import lombok.Getter;

@Getter
public class ReservationDetails {

    private OrderLineDetails orderLine;
    private Quantity quantity;
    private SkuId skuId;
    private OrderId orderId;

    public static ReservationDetails convert(Reservation reservation) {
        ReservationDetails details = new ReservationDetails();
        details.orderLine = OrderLineDetails.builder(reservation.getOrderLine()).build();
        details.quantity = reservation.getQuantity();
        details.skuId = reservation.getSkuId();
        details.orderId = reservation.getOrderLine().getOrder().getOrderId();
        return details;
    }

}
