package inc.roms.rcs.service.inventory.request;

import inc.roms.rcs.vo.order.OrderId;
import inc.roms.rcs.vo.sku.SkuId;
import inc.roms.rcs.vo.tote.ToteHeight;
import inc.roms.rcs.vo.tote.ToteId;
import inc.roms.rcs.vo.tote.TotePartitioning;
import inc.roms.rcs.vo.tote.ToteStatus;
import lombok.Data;

@Data
public class ToteListRequest {
    private SkuId sku;
    private TotePartitioning partitioning;
    private ToteHeight height;
    private ToteStatus status;
    private ToteId toteId;
    private OrderId orderId;
    private ToteId deliveryToteId;
    private Boolean hasDeliveryReservations;
    private Boolean onlyDisposedStock;
}
