package inc.roms.rcs.api.external.v2_0.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import inc.roms.rcs.validation.ValidatableField;
import inc.roms.rcs.validation.ValidationResult;
import inc.roms.rcs.vo.common.Quantity;
import inc.roms.rcs.vo.order.OrderLineId;
import inc.roms.rcs.vo.sku.SkuId;
import lombok.Data;

import static inc.roms.rcs.validation.validator.Validators.*;

@Data
public class OrderLineModel implements ValidatableField {

    @JsonProperty(required = true)
    private SkuId sku;

    @JsonProperty(required = true)
    private Quantity quantity;

    @JsonProperty(required = true)
    private OrderLineId orderLineNo;

    public ValidationResult validate() {
        ValidationResult.Builder resultBuilder = ValidationResult.builder();
        resultBuilder.addResult(notEmpty(sku, "sku"));
        resultBuilder.addResult(greaterThan(quantity, "quantity", 0));
        resultBuilder.addResult(notEmpty(orderLineNo, "order_line_no"));
        return resultBuilder.build();
    }
}
