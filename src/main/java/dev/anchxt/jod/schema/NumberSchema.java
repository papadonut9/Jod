package dev.anchxt.jod.schema;

import dev.anchxt.jod.core.Schema;
import dev.anchxt.jod.core.ValidationError;
import dev.anchxt.jod.core.ValidationResult;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for numeric schema validation. Provides common constraints: min, max,
 * positive, multipleOf.
 *
 * @param <T> The numeric type (Integer, Long, Double)
 * @param <S> The concrete schema type for fluent method chaining
 */
public abstract class NumberSchema<T extends Number & Comparable<T>, S extends NumberSchema<T, S>>
    implements Schema<T> {

  protected final List<Constraint<T>> constraints = new ArrayList<>();

  /** Returns this instance cast to the concrete schema type. */
  @SuppressWarnings("unchecked")
  protected S self() {
    return (S) this;
  }

  /** Minimum value constraint (inclusive). */
  public S min(T minValue) {
    constraints.add(
        new Constraint<>(
            value -> value.compareTo(minValue) >= 0,
            "Value must be at least " + minValue,
            "NUMBER_TOO_SMALL"));
    return self();
  }

  /** Maximum value constraint (inclusive). */
  public S max(T maxValue) {
    constraints.add(
        new Constraint<>(
            value -> value.compareTo(maxValue) <= 0,
            "Value must be at most " + maxValue,
            "NUMBER_TOO_LARGE"));
    return self();
  }

  /** Positive number constraint (value > 0). */
  public S positive() {
    constraints.add(new Constraint<>(this::isPositive, "Value must be positive", "NOT_POSITIVE"));
    return self();
  }

  /** Negative number constraint (value < 0). */
  public S negative() {
    constraints.add(new Constraint<>(this::isNegative, "Value must be negative", "NOT_NEGATIVE"));
    return self();
  }

  /** Multiple of constraint. */
  public S multipleOf(T divisor) {
    constraints.add(
        new Constraint<>(
            value -> isMultipleOf(value, divisor),
            "Value must be a multiple of " + divisor,
            "NOT_MULTIPLE"));
    return self();
  }

  /** Check if value is positive. Subclasses override for type-specific comparison. */
  protected abstract boolean isPositive(T value);

  /** Check if value is negative. Subclasses override for type-specific comparison. */
  protected abstract boolean isNegative(T value);

  /** Check if value is a multiple of divisor. Subclasses override for type-specific modulo. */
  protected abstract boolean isMultipleOf(T value, T divisor);

  @Override
  public ValidationResult<T> validate(T value) {
    if (value == null) {
      return ValidationResult.failure("Value cannot be null", "NULL_VALUE");
    }

    List<ValidationError> errors = new ArrayList<>();
    for (Constraint<T> constraint : constraints) {
      if (!constraint.test(value)) {
        errors.add(ValidationError.of(constraint.message(), constraint.code()));
      }
    }

    if (errors.isEmpty()) {
      return ValidationResult.success(value);
    }
    return ValidationResult.failure(errors);
  }

  /** Internal constraint representation. */
  protected record Constraint<T>(
      java.util.function.Predicate<T> predicate, String message, String code) {
    boolean test(T value) {
      return predicate.test(value);
    }
  }
}
