package dev.anchxt.jod.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("ValidationResult")
class ValidationResultTest {

  @Nested
  @DisplayName("Success")
  class SuccessTests {

    @Test
    void shouldCreateSuccessWithValue() {
      var result = ValidationResult.success("hello");

      assertThat(result.isSuccess()).isTrue();
      assertThat(result.isFailure()).isFalse();
      assertThat(result.getValue()).isEqualTo("hello");
      assertThat(result.getErrors()).isEmpty();
    }

    @Test
    void shouldMapSuccessValue() {
      var result = ValidationResult.success(5);
      var mapped = result.map(n -> n * 2);

      assertThat(mapped.isSuccess()).isTrue();
      assertThat(mapped.getValue()).isEqualTo(10);
    }

    @Test
    void shouldFlatMapSuccessValue() {
      var result = ValidationResult.success(5);
      var flatMapped = result.flatMap(n -> ValidationResult.success(n * 2));

      assertThat(flatMapped.isSuccess()).isTrue();
      assertThat(flatMapped.getValue()).isEqualTo(10);
    }

    @Test
    void shouldFlatMapToFailure() {
      var result = ValidationResult.success(5);
      var flatMapped =
          result.flatMap(n -> ValidationResult.failure("Value too small", "TOO_SMALL"));

      assertThat(flatMapped.isFailure()).isTrue();
      assertThat(flatMapped.getErrors()).hasSize(1);
    }
  }

  @Nested
  @DisplayName("Failure")
  class FailureTests {

    @Test
    void shouldCreateFailureWithError() {
      var error = ValidationError.of("Invalid value", "INVALID");
      var result = ValidationResult.<String>failure(error);

      assertThat(result.isSuccess()).isFalse();
      assertThat(result.isFailure()).isTrue();
      assertThat(result.getErrors()).hasSize(1);
      assertThat(result.getErrors().getFirst().message()).isEqualTo("Invalid value");
    }

    @Test
    void shouldCreateFailureWithMessageAndCode() {
      var result = ValidationResult.<String>failure("Invalid", "INVALID_CODE");

      assertThat(result.isFailure()).isTrue();
      assertThat(result.getErrors().getFirst().code()).isEqualTo("INVALID_CODE");
    }

    @Test
    void shouldCreateFailureWithMultipleErrors() {
      var errors =
          List.of(ValidationError.of("Error 1", "ERR1"), ValidationError.of("Error 2", "ERR2"));
      var result = ValidationResult.<String>failure(errors);

      assertThat(result.getErrors()).hasSize(2);
    }

    @Test
    void shouldThrowWhenGettingValueFromFailure() {
      var result = ValidationResult.<String>failure("Error", "ERR");

      assertThatThrownBy(result::getValue)
          .isInstanceOf(IllegalStateException.class)
          .hasMessageContaining("Cannot get value from a failed validation");
    }

    @Test
    void shouldNotMapFailure() {
      var result = ValidationResult.<Integer>failure("Error", "ERR");
      var mapped = result.map(n -> n * 2);

      assertThat(mapped.isFailure()).isTrue();
      assertThat(mapped.getErrors()).hasSize(1);
    }

    @Test
    void shouldNotFlatMapFailure() {
      var result = ValidationResult.<Integer>failure("Error", "ERR");
      var flatMapped = result.flatMap(n -> ValidationResult.success(n * 2));

      assertThat(flatMapped.isFailure()).isTrue();
      assertThat(flatMapped.getErrors()).hasSize(1);
    }

    @Test
    void shouldRejectEmptyErrorList() {
      assertThatThrownBy(() -> ValidationResult.failure(List.of()))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("at least one error");
    }
  }

  @Nested
  @DisplayName("ValidationError")
  class ValidationErrorTests {

    @Test
    void shouldCreateErrorWithPath() {
      var error = new ValidationError("user.name", "Invalid name", "INVALID_NAME");

      assertThat(error.path()).isEqualTo("user.name");
      assertThat(error.message()).isEqualTo("Invalid name");
      assertThat(error.code()).isEqualTo("INVALID_NAME");
    }

    @Test
    void shouldPrefixPath() {
      var error = ValidationError.of("Invalid", "ERR");
      var prefixed = error.withPathPrefix("user");

      assertThat(prefixed.path()).isEqualTo("user");
    }

    @Test
    void shouldPrefixExistingPath() {
      var error = new ValidationError("name", "Invalid", "ERR");
      var prefixed = error.withPathPrefix("user");

      assertThat(prefixed.path()).isEqualTo("user.name");
    }

    @Test
    void shouldHandleArrayPathPrefix() {
      var error = new ValidationError("[0].name", "Invalid", "ERR");
      var prefixed = error.withPathPrefix("users");

      assertThat(prefixed.path()).isEqualTo("users[0].name");
    }
  }
}
