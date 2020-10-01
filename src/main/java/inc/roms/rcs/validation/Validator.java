package inc.roms.rcs.validation;

import org.springframework.stereotype.Component;

@Component
public class Validator {

    public void validate(Validatable validatable) {
        ValidationResult.Builder resultBuilder = ValidationResult.builder();
        ValidationResult result = validatable.validate(resultBuilder);
        result.throwIfNotValid();
    }

}
