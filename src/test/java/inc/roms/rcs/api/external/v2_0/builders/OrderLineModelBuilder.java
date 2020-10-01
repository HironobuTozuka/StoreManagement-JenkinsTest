package inc.roms.rcs.api.external.v2_0.builders;

import inc.roms.rcs.api.external.v2_0.order.OrderLineModel;
import inc.roms.rcs.vo.common.Quantity;
import inc.roms.rcs.vo.order.OrderLineId;
import inc.roms.rcs.vo.sku.SkuId;

import java.util.UUID;

public class OrderLineModelBuilder {
    private SkuId sku;

    private Quantity quantity;

    private OrderLineId orderLineNo;

    public static OrderLineModelBuilder orderLine() {
        return new OrderLineModelBuilder();
    }

    public static OrderLineModelBuilder randomOrderLine() {
        return new OrderLineModelBuilder()
                .quantity(10)
                .sku(SkuId.from(UUID.randomUUID().toString()))
                .orderLineNo(OrderLineId.generate())
                ;
    }

    public OrderLineModelBuilder sku(SkuId sku) {
        this.sku = sku;
        return this;
    }

    public OrderLineModelBuilder quantity(Quantity quantity) {
        this.quantity = quantity;
        return this;
    }

    public OrderLineModelBuilder quantity(Integer quantity) {
        this.quantity = Quantity.of(quantity);
        return this;
    }

    public OrderLineModelBuilder orderLineNo(OrderLineId orderLineNo) {
        this.orderLineNo = orderLineNo;
        return this;
    }

    public OrderLineModel build() {
        OrderLineModel orderLineModel = new OrderLineModel();
        orderLineModel.setOrderLineNo(orderLineNo);
        orderLineModel.setSku(sku);
        orderLineModel.setQuantity(quantity);
        return orderLineModel;
    }
}
