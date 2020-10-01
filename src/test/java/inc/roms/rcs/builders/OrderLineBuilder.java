package inc.roms.rcs.builders;

import inc.roms.rcs.vo.common.Quantity;
import inc.roms.rcs.vo.sku.SkuId;
import inc.roms.rcs.service.order.domain.model.OrderLine;
import inc.roms.rcs.vo.order.OrderLineId;

public class OrderLineBuilder {
    private OrderLineId orderLineId;
    private Quantity quantity;
    private SkuId skuId;
    private OrderBuilder order;

    public static OrderLineBuilder orderLine() {
        return new OrderLineBuilder()
                .orderLineId(OrderLineId.generate());
    }

    public OrderLineBuilder orderLineId(OrderLineId orderLineId) {
        this.orderLineId = orderLineId;
        return this;
    }

    public OrderLineBuilder quantity(Quantity quantity) {
        this.quantity = quantity;
        return this;
    }

    public OrderLineBuilder skuId(SkuId skuId) {
        this.skuId = skuId;
        return this;
    }

    public OrderLine build() {
        OrderLine orderLine = new OrderLine();
        orderLine.setSkuId(skuId);
        orderLine.setQuantity(quantity);
        orderLine.setOrderLineId(orderLineId);
        orderLine.setOrder(order.build());
        return orderLine;
    }

    public OrderLineBuilder order(OrderBuilder order) {
        this.order = order;
        return this;
    }
}
