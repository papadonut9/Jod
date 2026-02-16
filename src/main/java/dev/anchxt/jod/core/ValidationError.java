package dev.anchxt.jod.core;

/**
 * Represents a validation error with path, message, and error code.
 *
 * @param path The path to the invalid field (e.g., "user.address.city" or "tags[0]")
 * @param message Human-readable error message
 * @param code Machine-readable error code (e.g., "STRING_TOO_SHORT", "INVALID_EMAIL")
 */
public record ValidationError(String path, String message, String code) {

  /** Creates a validation error with an empty path (root level). */
  public static ValidationError of(String message, String code) {
    return new ValidationError("", message, code);
  }

  /** Creates a new error with a prefixed path. */
  public ValidationError withPathPrefix(String prefix) {
    if (path.isEmpty()) {
      return new ValidationError(prefix, message, code);
    }
    if (prefix.isEmpty()) {
      return this;
    }
    // Handle array indices: prefix + "[0].field" or prefix + ".field"
    String separator = path.startsWith("[") ? "" : ".";
    return new ValidationError(prefix + separator + path, message, code);
  }
}
