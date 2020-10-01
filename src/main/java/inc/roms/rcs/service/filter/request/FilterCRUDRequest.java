package inc.roms.rcs.service.filter.request;

import inc.roms.rcs.validation.Validatable;
import inc.roms.rcs.validation.ValidationResult;
import inc.roms.rcs.validation.ValidationResult.Builder;
import inc.roms.rcs.validation.validator.Validators;
import inc.roms.rcs.service.filter.domain.FilterDetails;

public class FilterCRUDRequest extends FilterDetails implements Validatable {

    @Override
    public ValidationResult validate(Builder resultBuilder) {
        resultBuilder.addResult(Validators.notNull(super.filterId, "filter_id"));
        resultBuilder.addResult(Validators.notNull(super.target, "target"));
        resultBuilder.addResult(Validators.notNull(super.fields, "fields"));
        return resultBuilder.build();
    }

}
