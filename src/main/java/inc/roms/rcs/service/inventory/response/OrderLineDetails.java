package inc.roms.rcs.service.inventory.response;

import inc.roms.rcs.service.order.domain.model.OrderLine;
import inc.roms.rcs.vo.common.Quantity;
import inc.roms.rcs.vo.order.OrderLineId;
import inc.roms.rcs.vo.sku.SkuId;
import inc.roms.rcs.vo.tote.ToteId;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderLineDetails {

    private final SkuId skuId;
    private final OrderLineId orderLineId;
    private final Quantity quantity;
    private final Quantity picked;
    private final Quantity failed;
    private final List<ToteId> storageTotes;

    public static Builder builder(OrderLine orderLine) {
        return new Builder(orderLine);
    }

    public static class Builder {
        private final SkuId skuId;
        private final OrderLineId orderLineId;
        private final Quantity quantity;
        private final Quantity picked;
        private final Quantity failed;
        private List<ToteId> storageTotes;

        public Builder(OrderLine orderLine) {
            this.skuId = orderLine.getSkuId();
            this.orderLineId = orderLine.getOrderLineId();
            this.quantity = orderLine.getQuantity();
            this.picked = orderLine.getPicked();
            this.failed = orderLine.getFailed();
        }

        public Builder storageTotes(List<ToteId> storageTotes) {
            this.storageTotes = storageTotes;
            return this;
        }

        public OrderLineDetails build() {
            return new OrderLineDetails(skuId, orderLineId, quantity, picked, failed, storageTotes);
        }
    }

}
