package inc.roms.rcs.api.external.v2_0.order;

import inc.roms.rcs.validation.Validatable;
import inc.roms.rcs.validation.ValidationResult;
import inc.roms.rcs.vo.common.TransactionId;
import inc.roms.rcs.vo.order.OrderId;
import lombok.Data;

import java.util.List;

import static inc.roms.rcs.validation.validator.Validators.*;

@Data
public class OrdersActionRequest_v2 implements Validatable {
    private TransactionId transactionId;
    private List<OrderId> orderNos;

    public ValidationResult validate(ValidationResult.Builder resultBuilder) {
        resultBuilder.addResult(notEmpty(transactionId, "transaction_id"));
        resultBuilder.addResult(notEmpty(orderNos, "order_nos"));
        resultBuilder.addResults(validateCollection(orderNos, "order_nos"));
        return resultBuilder.build();
    }
}
