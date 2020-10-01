package inc.roms.rcs.security.model;

import java.util.Arrays;

import inc.roms.rcs.validation.Validatable;
import inc.roms.rcs.validation.ValidationResult;
import inc.roms.rcs.validation.ValidationResult.Builder;
import inc.roms.rcs.validation.validator.Validators;
import lombok.Data;

@Data
public class JwtInvalidateRequest implements Validatable {
    private String username;
    private String token;

    @Override
    public ValidationResult validate(Builder resultBuilder) {
        resultBuilder.addResult(Validators.notEmpty(Arrays.asList("username", "token"), username, token));
        return resultBuilder.build();
    }
}
