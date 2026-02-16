package dev.anchxt.jod.schema;

import static org.assertj.core.api.Assertions.assertThat;

import dev.anchxt.jod.Jod;
import java.util.regex.Pattern;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("StringSchema")
class StringSchemaTest {

  @Nested
  @DisplayName("Basic validation")
  class BasicValidation {

    @Test
    void shouldPassValidString() {
      var schema = Jod.string();
      var result = schema.validate("hello");

      assertThat(result.isSuccess()).isTrue();
      assertThat(result.getValue()).isEqualTo("hello");
    }

    @Test
    void shouldFailOnNull() {
      var schema = Jod.string();
      var result = schema.validate(null);

      assertThat(result.isFailure()).isTrue();
      assertThat(result.getErrors()).hasSize(1);
      assertThat(result.getErrors().getFirst().code()).isEqualTo("NULL_VALUE");
    }
  }

  @Nested
  @DisplayName("Length constraints")
  class LengthConstraints {

    @Test
    void shouldPassMinLengthConstraint() {
      var schema = Jod.string().min(3);
      var result = schema.validate("hello");

      assertThat(result.isSuccess()).isTrue();
    }

    @Test
    void shouldFailMinLengthConstraint() {
      var schema = Jod.string().min(10);
      var result = schema.validate("hello");

      assertThat(result.isFailure()).isTrue();
      assertThat(result.getErrors().getFirst().code()).isEqualTo("STRING_TOO_SHORT");
    }

    @Test
    void shouldPassMaxLengthConstraint() {
      var schema = Jod.string().max(10);
      var result = schema.validate("hello");

      assertThat(result.isSuccess()).isTrue();
    }

    @Test
    void shouldFailMaxLengthConstraint() {
      var schema = Jod.string().max(3);
      var result = schema.validate("hello");

      assertThat(result.isFailure()).isTrue();
      assertThat(result.getErrors().getFirst().code()).isEqualTo("STRING_TOO_LONG");
    }

    @Test
    void shouldCollectMultipleErrors() {
      // "hello" (5 chars) fails min(10) AND max(3)
      var schema = Jod.string().min(10).max(3);
      var result = schema.validate("hello");

      assertThat(result.isFailure()).isTrue();
      assertThat(result.getErrors()).hasSize(2);
    }
  }

  @Nested
  @DisplayName("Email validation")
  class EmailValidation {

    @Test
    void shouldPassValidEmail() {
      var schema = Jod.string().email();

      assertThat(schema.validate("test@example.com").isSuccess()).isTrue();
      assertThat(schema.validate("user.name+tag@domain.co.uk").isSuccess()).isTrue();
    }

    @Test
    void shouldFailInvalidEmail() {
      var schema = Jod.string().email();

      assertThat(schema.validate("invalid").isFailure()).isTrue();
      assertThat(schema.validate("@missing.com").isFailure()).isTrue();
      assertThat(schema.validate("no-domain@").isFailure()).isTrue();

      var result = schema.validate("invalid");
      assertThat(result.getErrors().getFirst().code()).isEqualTo("INVALID_EMAIL");
    }
  }

  @Nested
  @DisplayName("UUID validation")
  class UuidValidation {

    @Test
    void shouldPassValidUuid() {
      var schema = Jod.string().uuid();
      var result = schema.validate("550e8400-e29b-41d4-a716-446655440000");

      assertThat(result.isSuccess()).isTrue();
    }

    @Test
    void shouldFailInvalidUuid() {
      var schema = Jod.string().uuid();

      assertThat(schema.validate("not-a-uuid").isFailure()).isTrue();
      assertThat(schema.validate("550e8400-e29b-41d4-a716").isFailure()).isTrue();

      var result = schema.validate("invalid");
      assertThat(result.getErrors().getFirst().code()).isEqualTo("INVALID_UUID");
    }
  }

  @Nested
  @DisplayName("Regex validation")
  class RegexValidation {

    @Test
    void shouldPassMatchingRegex() {
      var schema = Jod.string().regex(Pattern.compile("^[A-Z]+$"));
      var result = schema.validate("HELLO");

      assertThat(result.isSuccess()).isTrue();
    }

    @Test
    void shouldPassMatchingRegexString() {
      var schema = Jod.string().regex("^[0-9]+$");
      var result = schema.validate("12345");

      assertThat(result.isSuccess()).isTrue();
    }

    @Test
    void shouldFailNonMatchingRegex() {
      var schema = Jod.string().regex("^[A-Z]+$");
      var result = schema.validate("hello");

      assertThat(result.isFailure()).isTrue();
      assertThat(result.getErrors().getFirst().code()).isEqualTo("REGEX_MISMATCH");
    }
  }

  @Nested
  @DisplayName("Transformations")
  class Transformations {

    @Test
    void shouldTrimWhitespace() {
      var schema = Jod.string().trim();
      var result = schema.validate("  hello world  ");

      assertThat(result.isSuccess()).isTrue();
      assertThat(result.getValue()).isEqualTo("hello world");
    }

    @Test
    void shouldConvertToLowerCase() {
      var schema = Jod.string().toLowerCase();
      var result = schema.validate("HELLO World");

      assertThat(result.isSuccess()).isTrue();
      assertThat(result.getValue()).isEqualTo("hello world");
    }

    @Test
    void shouldConvertToUpperCase() {
      var schema = Jod.string().toUpperCase();
      var result = schema.validate("hello World");

      assertThat(result.isSuccess()).isTrue();
      assertThat(result.getValue()).isEqualTo("HELLO WORLD");
    }

    @Test
    void shouldChainTransformations() {
      var schema = Jod.string().trim().toLowerCase();
      var result = schema.validate("  HELLO WORLD  ");

      assertThat(result.isSuccess()).isTrue();
      assertThat(result.getValue()).isEqualTo("hello world");
    }

    @Test
    void shouldApplyTransformationsBeforeConstraints() {
      // Without trim, " hi " has length 6, which would fail min(3)
      // With trim applied first, "hi" has length 2, which fails min(3)
      var schema = Jod.string().trim().min(3);
      var result = schema.validate("  hi  ");

      assertThat(result.isFailure()).isTrue();
      assertThat(result.getErrors().getFirst().code()).isEqualTo("STRING_TOO_SHORT");
    }
  }

  @Nested
  @DisplayName("Fluent API chaining")
  class FluentApiChaining {

    @Test
    void shouldSupportFullChaining() {
      var schema = Jod.string().trim().toLowerCase().min(3).max(50).regex("^[a-z ]+$");

      var result = schema.validate("  HELLO WORLD  ");

      assertThat(result.isSuccess()).isTrue();
      assertThat(result.getValue()).isEqualTo("hello world");
    }
  }
}
