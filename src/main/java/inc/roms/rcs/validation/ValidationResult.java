package inc.roms.rcs.validation;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@RequiredArgsConstructor
public class ValidationResult {

    private final List<ValidationProblem> problems;

    public static Builder builder() {
        return new Builder();
    }

    public void throwIfNotValid() {
        boolean empty = problems.isEmpty();
        if(empty) return;

        throw new RequestNotValidException(this);
    }

    @Override
    public String toString() {
        return problems.toString();
    }

    public static class Builder {

        List<ValidationProblem> problems = new ArrayList<>();

        public ValidationProblem.Builder problemOnField(String fieldName) {
            return ValidationProblem.builder(fieldName, this);
        }

        public Builder addResult(ValidationProblem problem) {
            if(problem == null) return this;
            problems.add(problem);
            return this;
        }

        public Builder addResults(List<ValidationProblem> problems) {
            if(problems == null) return this;
            this.problems.addAll(problems);
            return this;
        }

        public ValidationResult build() {
            return new ValidationResult(problems);
        }
    }
}
