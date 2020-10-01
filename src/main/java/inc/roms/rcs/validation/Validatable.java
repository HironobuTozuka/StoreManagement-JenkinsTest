package inc.roms.rcs.validation;

public interface Validatable {
    ValidationResult validate(ValidationResult.Builder result);
}
