package inc.roms.rcs.vo.types;

import inc.roms.rcs.validation.ValidatableField;
import inc.roms.rcs.validation.ValidationProblem;
import inc.roms.rcs.validation.ValidationResult;
import org.apache.logging.log4j.util.Strings;

import javax.persistence.Transient;

public interface StringVO extends ValidatableField {


    @Transient
    String value();

    //remove somehow...
    default ValidationResult validate() {
        ValidationResult.Builder builder = ValidationResult.builder();
        if (Strings.isEmpty(value())) {
            builder.addResult(new ValidationProblem("", value(), "Value cannot be empty!"));
        }
        return builder.build();
    }


}
