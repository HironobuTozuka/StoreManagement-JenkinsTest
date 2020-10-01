package inc.roms.rcs.validation;

import lombok.Data;
import org.apache.logging.log4j.util.Strings;

@Data
public class ValidationProblem {

    private final String field;
    private final String value;
    private final String problem;

    @Override
    public String toString() {
        String result = "{field: '" + field + '\'';
        if (Strings.isNotEmpty(value)) {
            result = result +
                    ", value: '" + value + '\'';
        }
        return result +
                ", problem: '" + problem + '\'' + "}";
    }

    public ValidationProblem copyWithPrefix(String prefix) {
        return new ValidationProblem(prefix + "." + field, value, problem);
    }

    public static Builder builder(String fieldName, ValidationResult.Builder builder) {
        return new Builder(fieldName, builder);
    }

    public static class Builder {
        private final String field;
        private final ValidationResult.Builder masterBuilder;
        private String value;

        private Builder(String field, ValidationResult.Builder masterBuilder) {
            this.field = field;
            this.masterBuilder = masterBuilder;
        }

        public Builder value(String value) {
            this.value = value;
            return this;
        }

        public ValidationResult.Builder description(String description) {
            ValidationProblem problem = new ValidationProblem(field, value, description);
            return masterBuilder.addResult(problem);
        }
    }
}
