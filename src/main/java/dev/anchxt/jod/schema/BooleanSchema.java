package dev.anchxt.jod.schema;

import dev.anchxt.jod.core.Schema;
import dev.anchxt.jod.core.ValidationError;
import dev.anchxt.jod.core.ValidationResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Schema for validating Boolean values.
 * Supports isTrue() and isFalse() constraints.
 */
public class BooleanSchema implements Schema<Boolean> {

    private final List<Constraint> constraints = new ArrayList<>();

    /**
     * Requires the value to be true.
     */
    public BooleanSchema isTrue() {
        constraints.add(new Constraint(
                value -> value,
                "Value must be true",
                "NOT_TRUE"));
        return this;
    }

    /**
     * Requires the value to be false.
     */
    public BooleanSchema isFalse() {
        constraints.add(new Constraint(
                value -> !value,
                "Value must be false",
                "NOT_FALSE"));
        return this;
    }

    @Override
    public ValidationResult<Boolean> validate(Boolean value) {
        if (value == null) {
            return ValidationResult.failure("Value cannot be null", "NULL_VALUE");
        }

        List<ValidationError> errors = new ArrayList<>();
        for (Constraint constraint : constraints) {
            if (!constraint.test(value)) {
                errors.add(ValidationError.of(constraint.message(), constraint.code()));
            }
        }

        if (errors.isEmpty()) {
            return ValidationResult.success(value);
        }
        return ValidationResult.failure(errors);
    }

    /**
     * Internal constraint representation.
     */
    private record Constraint(
            java.util.function.Predicate<Boolean> predicate,
            String message,
            String code) {
        boolean test(Boolean value) {
            return predicate.test(value);
        }
    }
}
