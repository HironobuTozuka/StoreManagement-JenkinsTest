package inc.roms.rcs.api.external.v2_0.stock;

import com.fasterxml.jackson.annotation.JsonFormat;
import inc.roms.rcs.validation.ValidatableField;
import inc.roms.rcs.validation.ValidationResult;
import inc.roms.rcs.vo.sku.SkuId;
import lombok.Data;

import java.time.ZonedDateTime;

import static inc.roms.rcs.api.external.v2_0.vo.JapanTimeHelper.DATETIME_PATTERN;
import static inc.roms.rcs.validation.validator.Validators.notEmpty;
import static inc.roms.rcs.validation.validator.Validators.notNull;

@Data
public class Item implements ValidatableField {

    @JsonFormat(pattern = DATETIME_PATTERN)
    private ZonedDateTime sellByDate;
    private SkuId sku;

    @Override
    public ValidationResult validate() {
        ValidationResult.Builder builder = ValidationResult.builder();
        builder.addResult(notNull(sellByDate, "sell_by_date"));
        builder.addResult(notEmpty(sku, "sku"));
        return builder.build();
    }
}
