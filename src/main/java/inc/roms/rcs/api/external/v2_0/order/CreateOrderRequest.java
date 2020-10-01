package inc.roms.rcs.api.external.v2_0.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import inc.roms.rcs.validation.Validatable;
import inc.roms.rcs.validation.ValidationResult;
import inc.roms.rcs.vo.common.TransactionId;
import inc.roms.rcs.vo.location.GateId;
import inc.roms.rcs.vo.order.OrderId;
import inc.roms.rcs.vo.order.OrderType;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;

import static inc.roms.rcs.api.external.v2_0.vo.JapanTimeHelper.DATETIME_PATTERN;
import static inc.roms.rcs.validation.validator.Validators.*;

@Data
public class CreateOrderRequest implements Validatable {

    @JsonProperty(required = true)
    private TransactionId transactionId;

    @JsonProperty(required = true)
    private OrderId orderNo;

    @JsonProperty(required = true)
    private OrderType orderType;

    @JsonFormat(pattern = DATETIME_PATTERN)
    private ZonedDateTime orderTime;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonFormat(pattern = DATETIME_PATTERN)
    private ZonedDateTime pickupTime;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private GateId gate;

    @JsonProperty(required = true)
    private List<OrderLineModel> orderLines;

    public ValidationResult validate(ValidationResult.Builder resultBuilder) {
        resultBuilder.addResult(notEmpty(transactionId, "transaction_id"));
        resultBuilder.addResult(notEmpty(orderNo, "order_no"));
        resultBuilder.addResult(notNull(orderType, "order_type"));
        resultBuilder.addResult(notNull(orderTime, "order_time"));
        if(OrderType.ORDER.equals(orderType)) {
            resultBuilder.addResult(notEmpty(gate, "gate"));
        }
        resultBuilder.addResult(notEmpty(orderLines, "order_lines"));
        resultBuilder.addResults(validateCollection(orderLines, "order_lines"));
        return resultBuilder.build();
    }

}
