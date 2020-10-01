package inc.roms.rcs.api.external.v2_0.order;

import com.fasterxml.jackson.annotation.JsonInclude;
import inc.roms.rcs.validation.Validatable;
import inc.roms.rcs.validation.ValidationResult;
import inc.roms.rcs.vo.common.TransactionId;
import inc.roms.rcs.vo.location.GateId;
import inc.roms.rcs.vo.order.OrderId;
import lombok.Data;

import static inc.roms.rcs.validation.validator.Validators.notEmpty;

@Data
public class DeliverOrderRequest implements Validatable {

    private TransactionId transactionId;
    private OrderId orderNo;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private GateId gate;

    @Override
    public ValidationResult validate(ValidationResult.Builder builder) {
        builder.addResult(notEmpty(gate, "gate"));
        builder.addResult(notEmpty(transactionId, "transaction_id"));
        builder.addResult(notEmpty(orderNo, "order_no"));
        return builder.build();
    }
}
