package inc.roms.rcs.api.external.v2_0.stock;

import com.fasterxml.jackson.annotation.JsonFormat;
import inc.roms.rcs.validation.Validatable;
import inc.roms.rcs.validation.ValidationResult;
import inc.roms.rcs.vo.common.Quantity;
import inc.roms.rcs.vo.common.TransactionId;
import inc.roms.rcs.vo.sku.DeliveryTurn;
import inc.roms.rcs.vo.sku.DistributionType;
import inc.roms.rcs.vo.sku.SkuId;
import lombok.Data;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import static inc.roms.rcs.api.external.v2_0.vo.JapanTimeHelper.DATETIME_PATTERN;
import static inc.roms.rcs.api.external.v2_0.vo.JapanTimeHelper.DATE_PATTERN;
import static inc.roms.rcs.validation.validator.Validators.*;

@Data
public class ScheduleSupplyRequest implements Validatable {

    private TransactionId transactionId;
    private DistributionType distributionType;

    @JsonFormat(pattern = DATE_PATTERN)
    private LocalDate deliveryDate;
    private DeliveryTurn deliveryTurn;
    private SkuId sku;
    private Quantity quantity;

    @JsonFormat(pattern = DATETIME_PATTERN)
    private ZonedDateTime sellByDate;


    @Override
    public ValidationResult validate(ValidationResult.Builder result) {
        result.addResult(notEmpty(transactionId, "transaction_id"));
        result.addResult(notEmpty(sku, "sku"));
        result.addResult(notNull(quantity, "quantity"));
        result.addResult(greaterThan(quantity, "quantity", 0));
        result.addResult(notNull(deliveryDate, "delivery_date"));
        result.addResult(notNull(sellByDate, "sell_by_date"));
        return result.build();
    }
}
