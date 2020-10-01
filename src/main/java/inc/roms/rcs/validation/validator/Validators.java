package inc.roms.rcs.validation.validator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;

import inc.roms.rcs.validation.ValidatableField;
import inc.roms.rcs.validation.ValidationProblem;
import inc.roms.rcs.validation.ValidationResult;
import inc.roms.rcs.vo.common.Quantity;
import inc.roms.rcs.vo.types.StringVO;

public class Validators {

    /*
     * Object must be empty
     */
    public static ValidationProblem isNull(Object value, String fieldName) {
        if (Objects.isNull(value)) {
            return null;
        }

        return new ValidationProblem(fieldName, null, "field must be null");
    }

    /*
     * String must not be empty
     */
    public static ValidationProblem notEmpty(String string, String fieldName) {
        if (Objects.isNull(string) || Strings.isEmpty(string)) {
            return new ValidationProblem(fieldName, null, "field cannot be null or empty");
        }
        return null;
    }

    public static ValidationProblem notEmpty(StringVO stringValue, String fieldName) {
        if (Objects.isNull(stringValue) || Strings.isEmpty(stringValue.value())) {
            return new ValidationProblem(fieldName, null, "field cannot be null or empty");
        }
        return null;
    }

    public static ValidationProblem notEmpty(List<String> fieldsNames, Object... fields) {
        boolean allEmpty = true;
        for (Object field : fields) {
            if (field != null && !field.toString().isBlank()) {
                allEmpty = true;
                break;
            }
        }
        if (allEmpty) {
            return new ValidationProblem(StringUtils.join(fieldsNames), null, "one of the fields must not be empty");
        }
        return null;
    }

    public static ValidationProblem notNull(Object object, String fieldName) {
        if (Objects.isNull(object)) {
            return new ValidationProblem(fieldName, null, "field cannot be null!");
        }
        return null;
    }

    public static ValidationProblem greaterThan(Quantity quantity, String fieldName, Integer value) {
        if (Objects.isNull(quantity)) {
            return new ValidationProblem(fieldName, null, "field cannot be null!");
        }
        if (!quantity.gt(value)) {
            return new ValidationProblem(fieldName, String.valueOf(quantity.getQuantity()),
                    "field should be greater than '" + value + "'!");
        }
        return null;
    }

    public static ValidationProblem notEmpty(Collection<?> collection, String fieldName) {
        if (Objects.isNull(collection) || collection.isEmpty()) {
            return new ValidationProblem(fieldName, null, "field cannot be null or empty");
        }
        return null;
    }

    public static List<ValidationProblem> validateCollection(Collection<? extends ValidatableField> collection,
            String fieldName) {
        if (Objects.isNull(collection) || collection.isEmpty()) {
            return new ArrayList<>();
        }
        List<ValidationProblem> problems = new ArrayList<>();

        List<ValidationResult> validationResults = collection.stream().map(ValidatableField::validate)
                .collect(Collectors.toList());
        for (int i = 0; i < validationResults.size(); i++) {
            String prefix = fieldName + "[" + i + "]";
            List<ValidationProblem> fieldProblems = validationResults.get(i).getProblems().stream()
                    .map(it -> it.copyWithPrefix(prefix)).collect(Collectors.toList());
            problems.addAll(fieldProblems);
        }

        return problems;
    }

}
