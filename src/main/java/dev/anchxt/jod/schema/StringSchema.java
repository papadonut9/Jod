package dev.anchxt.jod.schema;

import dev.anchxt.jod.core.Schema;
import dev.anchxt.jod.core.ValidationError;
import dev.anchxt.jod.core.ValidationResult;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

/**
 * Schema for validating and transforming String values. Supports constraints (min, max, email,
 * regex, uuid) and transformations (trim, toLowerCase, toUpperCase). Transformations are applied
 * before constraints.
 */
public class StringSchema implements Schema<String> {

  private static final Pattern EMAIL_PATTERN =
      Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
  private static final Pattern UUID_PATTERN =
      Pattern.compile(
          "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

  private final List<UnaryOperator<String>> transformations = new ArrayList<>();
  private final List<Constraint> constraints = new ArrayList<>();

  /** Minimum length constraint. */
  public StringSchema min(int minLength) {
    constraints.add(
        new Constraint(
            value -> value.length() >= minLength,
            "String must be at least " + minLength + " characters",
            "STRING_TOO_SHORT"));
    return this;
  }

  /** Maximum length constraint. */
  public StringSchema max(int maxLength) {
    constraints.add(
        new Constraint(
            value -> value.length() <= maxLength,
            "String must be at most " + maxLength + " characters",
            "STRING_TOO_LONG"));
    return this;
  }

  /** Email format constraint. */
  public StringSchema email() {
    constraints.add(
        new Constraint(
            value -> EMAIL_PATTERN.matcher(value).matches(),
            "Invalid email format",
            "INVALID_EMAIL"));
    return this;
  }

  /** Custom regex pattern constraint. */
  public StringSchema regex(Pattern pattern) {
    constraints.add(
        new Constraint(
            value -> pattern.matcher(value).matches(),
            "String does not match pattern: " + pattern.pattern(),
            "REGEX_MISMATCH"));
    return this;
  }

  /** Custom regex pattern constraint with string pattern. */
  public StringSchema regex(String pattern) {
    return regex(Pattern.compile(pattern));
  }

  /** UUID format constraint. */
  public StringSchema uuid() {
    constraints.add(
        new Constraint(
            value -> UUID_PATTERN.matcher(value).matches(), "Invalid UUID format", "INVALID_UUID"));
    return this;
  }

  // ==================== Transformations ====================

  /** Trims whitespace from both ends of the string. */
  public StringSchema trim() {
    transformations.add(String::trim);
    return this;
  }

  /** Converts the string to lowercase. */
  public StringSchema toLowerCase() {
    transformations.add(String::toLowerCase);
    return this;
  }

  /** Converts the string to uppercase. */
  public StringSchema toUpperCase() {
    transformations.add(String::toUpperCase);
    return this;
  }

  // ==================== Validation ====================

  @Override
  public ValidationResult<String> validate(String value) {
    if (value == null) {
      return ValidationResult.failure("Value cannot be null", "NULL_VALUE");
    }

    // Apply transformations first
    String transformed = value;
    for (UnaryOperator<String> transform : transformations) {
      transformed = transform.apply(transformed);
    }

    // Collect all constraint violations
    List<ValidationError> errors = new ArrayList<>();
    for (Constraint constraint : constraints) {
      if (!constraint.test(transformed)) {
        errors.add(ValidationError.of(constraint.message(), constraint.code()));
      }
    }

    if (errors.isEmpty()) {
      return ValidationResult.success(transformed);
    }
    return ValidationResult.failure(errors);
  }

  /** Internal constraint representation. */
  private record Constraint(
      java.util.function.Predicate<String> predicate, String message, String code) {
    boolean test(String value) {
      return predicate.test(value);
    }
  }
}
