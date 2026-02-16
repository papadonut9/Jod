package dev.anchxt.jod.core;

/**
 * Base interface for all validation schemas. A schema validates a value of type T and returns a
 * ValidationResult.
 *
 * @param <T> The type of value this schema validates
 */
@FunctionalInterface
public interface Schema<T> {

  /**
   * Validates the given value.
   *
   * @param value The value to validate
   * @return A ValidationResult containing either the validated/transformed value or errors
   */
  ValidationResult<T> validate(T value);
}
