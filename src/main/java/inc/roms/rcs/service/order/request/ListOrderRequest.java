package inc.roms.rcs.service.order.request;

import inc.roms.rcs.vo.order.OrderId;
import inc.roms.rcs.vo.order.OrderStatus;
import inc.roms.rcs.vo.sku.SkuId;
import inc.roms.rcs.vo.tote.ToteId;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ListOrderRequest {
    private SkuId skuId;
    private OrderId orderId;
    private ToteId storageToteId;
    private ToteId deliveryToteId;
    private OrderStatus orderStatus;
}
