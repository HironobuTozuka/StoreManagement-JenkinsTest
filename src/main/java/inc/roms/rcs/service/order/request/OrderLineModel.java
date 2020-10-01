package inc.roms.rcs.service.order.request;

import inc.roms.rcs.vo.common.Quantity;
import inc.roms.rcs.vo.order.OrderLineId;
import inc.roms.rcs.vo.sku.SkuId;
import lombok.Data;

import java.util.Objects;

@Data
public class OrderLineModel {

    private SkuId skuId;
    private OrderLineId orderLineId;
    private Quantity quantity;

    public void validate() {
        //TODO rozwazyc slowne info, np.: throw new IllegalStateException("skuId must not be null");
        if(Objects.isNull(skuId)) throw new IllegalStateException();
        if(Objects.isNull(quantity) || !quantity.gt(0)) throw new IllegalStateException();
        if(Objects.isNull(orderLineId)) throw new IllegalStateException();
    }
}
