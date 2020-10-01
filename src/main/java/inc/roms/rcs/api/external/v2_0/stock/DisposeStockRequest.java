package inc.roms.rcs.api.external.v2_0.stock;

import inc.roms.rcs.validation.Validatable;
import inc.roms.rcs.validation.ValidationResult;
import inc.roms.rcs.vo.common.TransactionId;
import lombok.Data;

import java.util.List;

import static inc.roms.rcs.validation.validator.Validators.*;

@Data
public class DisposeStockRequest implements Validatable {

    private TransactionId transactionId;
    private List<Item> items;

    @Override
    public ValidationResult validate(ValidationResult.Builder result) {
        result.addResult(notEmpty(transactionId, "transaction_id"));
        result.addResult(notEmpty(items, "items"));
        result.addResults(validateCollection(items, "items"));
        return result.build();
    }
}
