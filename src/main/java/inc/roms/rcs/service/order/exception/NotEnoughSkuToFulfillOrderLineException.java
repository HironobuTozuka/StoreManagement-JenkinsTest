package inc.roms.rcs.service.order.exception;

import inc.roms.rcs.vo.common.Quantity;
import inc.roms.rcs.service.order.domain.model.OrderLine;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class NotEnoughSkuToFulfillOrderLineException extends RuntimeException {
    private OrderLine orderLine;
    private Quantity missingQuantity;

    public NotEnoughSkuToFulfillOrderLineException(OrderLine orderLine, Quantity missingQuantity) {
        super("Not enought sku: " + orderLine.getSkuId() + "to fulfill order line: " + orderLine.getOrderLineId() + ", RCS is lacking:" + missingQuantity + " eaches");
        this.orderLine = orderLine;
        this.missingQuantity = missingQuantity;
    }
}
