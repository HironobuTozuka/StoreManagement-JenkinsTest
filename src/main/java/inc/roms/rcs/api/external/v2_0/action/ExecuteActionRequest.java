package inc.roms.rcs.api.external.v2_0.action;

import inc.roms.rcs.validation.ValidationResult;
import inc.roms.rcs.api.external.v2_0.vo.ActionDetails;
import inc.roms.rcs.api.external.v2_0.vo.ActionType;
import inc.roms.rcs.validation.Validatable;
import inc.roms.rcs.vo.common.TransactionId;
import lombok.Data;
import org.apache.logging.log4j.util.Strings;

import static inc.roms.rcs.api.external.v2_0.vo.ActionType.GATE_CLOSE;

@Data
public class ExecuteActionRequest implements Validatable {

    private TransactionId transactionId;
    private ActionType actionType;
    private ActionDetails actionDetails;

    public ValidationResult validate(ValidationResult.Builder resultBuilder) {
        if (transactionId == null || Strings.isEmpty(transactionId.getTransactionId()))
            resultBuilder
                    .problemOnField("transaction_id")
                    .description("cannot be null or empty");
        if (actionType == null) {
            resultBuilder
                    .problemOnField("action_type")
                    .description("cannot be null or empty");
        }
        if (actionDetails == null) {
            resultBuilder
                    .problemOnField("action_details")
                    .description("cannot be null or empty");
        }
        if(GATE_CLOSE.equals(actionType)) {
            if(actionDetails != null) {
                if (actionDetails.getGate() == null || Strings.isEmpty(actionDetails.getGate().getGateId())) {
                    resultBuilder.problemOnField("action_details.gate")
                            .value(actionDetails.getGate().getGateId())
                            .description("For action type: " + actionType + " action_details have to contain valid gate_id!");
                }
            }
        }
        return resultBuilder.build();
    }

}
