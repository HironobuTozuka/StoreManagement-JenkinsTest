package inc.roms.rcs.builders;

import inc.roms.rcs.service.order.request.OrderLineModel;
import inc.roms.rcs.vo.common.Quantity;
import inc.roms.rcs.vo.order.OrderLineId;
import inc.roms.rcs.vo.sku.SkuId;

public class OrderLineModelBuilder {
    private OrderLineId orderLineId;
    private Quantity quantity;
    private SkuId skuId;

    public static OrderLineModelBuilder orderLineModel() {
        return new OrderLineModelBuilder().orderLineId(OrderLineId.generate());
    }

    public OrderLineModelBuilder orderLineId(OrderLineId orderLineId) {
        this.orderLineId = orderLineId;
        return this;
    }

    public OrderLineModelBuilder quantity(Quantity quantity) {
        this.quantity = quantity;
        return this;
    }

    public OrderLineModelBuilder skuId(SkuId skuId) {
        this.skuId = skuId;
        return this;
    }

    public OrderLineModel build() {
        OrderLineModel orderLine = new OrderLineModel();
        orderLine.setSkuId(skuId);
        orderLine.setQuantity(quantity);
        orderLine.setOrderLineId(orderLineId);
        return orderLine;
    }
}
