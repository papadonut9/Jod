package dev.anchxt.jod.core;

import java.util.List;
import java.util.function.Function;

/**
 * A monad representing the result of a validation operation.
 * Either a Success containing the validated/transformed value,
 * or a Failure containing a list of validation errors.
 *
 * @param <T> The type of the validated value
 */
public sealed interface ValidationResult<T> permits ValidationResult.Success, ValidationResult.Failure {

    /**
     * Creates a successful validation result.
     */
    static <T> ValidationResult<T> success(T value) {
        return new Success<>(value);
    }

    /**
     * Creates a failed validation result with a single error.
     */
    static <T> ValidationResult<T> failure(ValidationError error) {
        return new Failure<>(List.of(error));
    }

    /**
     * Creates a failed validation result with multiple errors.
     */
    static <T> ValidationResult<T> failure(List<ValidationError> errors) {
        return new Failure<>(List.copyOf(errors));
    }

    /**
     * Creates a failed validation result with message and code.
     */
    static <T> ValidationResult<T> failure(String message, String code) {
        return new Failure<>(List.of(ValidationError.of(message, code)));
    }

    /**
     * Returns true if validation succeeded.
     */
    boolean isSuccess();

    /**
     * Returns true if validation failed.
     */
    default boolean isFailure() {
        return !isSuccess();
    }

    /**
     * Gets the validated value. Throws if validation failed.
     */
    T getValue();

    /**
     * Gets the list of validation errors. Empty if validation succeeded.
     */
    List<ValidationError> getErrors();

    /**
     * Transforms the success value using the given function.
     * If this is a Failure, returns itself unchanged.
     */
    <R> ValidationResult<R> map(Function<? super T, ? extends R> mapper);

    /**
     * Chains validation operations. The function is only called on success.
     * If this is a Failure, returns itself unchanged.
     */
    <R> ValidationResult<R> flatMap(Function<? super T, ? extends ValidationResult<R>> mapper);

    /**
     * Success case - contains the validated/transformed value.
     */
    record Success<T>(T value) implements ValidationResult<T> {

        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public T getValue() {
            return value;
        }

        @Override
        public List<ValidationError> getErrors() {
            return List.of();
        }

        @Override
        public <R> ValidationResult<R> map(Function<? super T, ? extends R> mapper) {
            return new Success<>(mapper.apply(value));
        }

        @Override
        public <R> ValidationResult<R> flatMap(Function<? super T, ? extends ValidationResult<R>> mapper) {
            return mapper.apply(value);
        }
    }

    /**
     * Failure case - contains a list of validation errors.
     */
    record Failure<T>(List<ValidationError> errors) implements ValidationResult<T> {

        public Failure {
            if (errors == null || errors.isEmpty()) {
                throw new IllegalArgumentException("Failure must have at least one error");
            }
        }

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public T getValue() {
            throw new IllegalStateException("Cannot get value from a failed validation. Errors: " + errors);
        }

        @Override
        public List<ValidationError> getErrors() {
            return errors;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <R> ValidationResult<R> map(Function<? super T, ? extends R> mapper) {
            return (ValidationResult<R>) this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <R> ValidationResult<R> flatMap(Function<? super T, ? extends ValidationResult<R>> mapper) {
            return (ValidationResult<R>) this;
        }
    }
}
