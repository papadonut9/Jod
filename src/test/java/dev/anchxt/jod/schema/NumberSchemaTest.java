package dev.anchxt.jod.schema;

import static org.assertj.core.api.Assertions.assertThat;

import dev.anchxt.jod.Jod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("NumberSchema")
class NumberSchemaTest {

  @Nested
  @DisplayName("IntSchema")
  class IntSchemaTests {

    @Test
    void shouldPassValidInteger() {
      var schema = Jod.intType();
      var result = schema.validate(42);

      assertThat(result.isSuccess()).isTrue();
      assertThat(result.getValue()).isEqualTo(42);
    }

    @Test
    void shouldFailOnNull() {
      var schema = Jod.intType();
      var result = schema.validate(null);

      assertThat(result.isFailure()).isTrue();
      assertThat(result.getErrors().getFirst().code()).isEqualTo("NULL_VALUE");
    }

    @Test
    void shouldPassMinConstraint() {
      var schema = Jod.intType().min(10);

      assertThat(schema.validate(10).isSuccess()).isTrue();
      assertThat(schema.validate(15).isSuccess()).isTrue();
    }

    @Test
    void shouldFailMinConstraint() {
      var schema = Jod.intType().min(10);
      var result = schema.validate(5);

      assertThat(result.isFailure()).isTrue();
      assertThat(result.getErrors().getFirst().code()).isEqualTo("NUMBER_TOO_SMALL");
    }

    @Test
    void shouldPassMaxConstraint() {
      var schema = Jod.intType().max(100);

      assertThat(schema.validate(100).isSuccess()).isTrue();
      assertThat(schema.validate(50).isSuccess()).isTrue();
    }

    @Test
    void shouldFailMaxConstraint() {
      var schema = Jod.intType().max(100);
      var result = schema.validate(150);

      assertThat(result.isFailure()).isTrue();
      assertThat(result.getErrors().getFirst().code()).isEqualTo("NUMBER_TOO_LARGE");
    }

    @Test
    void shouldPassPositiveConstraint() {
      var schema = Jod.intType().positive();

      assertThat(schema.validate(1).isSuccess()).isTrue();
      assertThat(schema.validate(100).isSuccess()).isTrue();
    }

    @Test
    void shouldFailPositiveConstraint() {
      var schema = Jod.intType().positive();

      assertThat(schema.validate(0).isFailure()).isTrue();
      assertThat(schema.validate(-5).isFailure()).isTrue();

      var result = schema.validate(-1);
      assertThat(result.getErrors().getFirst().code()).isEqualTo("NOT_POSITIVE");
    }

    @Test
    void shouldPassNegativeConstraint() {
      var schema = Jod.intType().negative();

      assertThat(schema.validate(-1).isSuccess()).isTrue();
      assertThat(schema.validate(-100).isSuccess()).isTrue();
    }

    @Test
    void shouldFailNegativeConstraint() {
      var schema = Jod.intType().negative();

      assertThat(schema.validate(0).isFailure()).isTrue();
      assertThat(schema.validate(5).isFailure()).isTrue();

      var result = schema.validate(1);
      assertThat(result.getErrors().getFirst().code()).isEqualTo("NOT_NEGATIVE");
    }

    @Test
    void shouldPassMultipleOfConstraint() {
      var schema = Jod.intType().multipleOf(5);

      assertThat(schema.validate(0).isSuccess()).isTrue();
      assertThat(schema.validate(10).isSuccess()).isTrue();
      assertThat(schema.validate(-15).isSuccess()).isTrue();
    }

    @Test
    void shouldFailMultipleOfConstraint() {
      var schema = Jod.intType().multipleOf(5);
      var result = schema.validate(7);

      assertThat(result.isFailure()).isTrue();
      assertThat(result.getErrors().getFirst().code()).isEqualTo("NOT_MULTIPLE");
    }

    @Test
    void shouldSupportChaining() {
      var schema = Jod.intType().min(0).max(100).positive().multipleOf(10);

      assertThat(schema.validate(50).isSuccess()).isTrue();
      assertThat(schema.validate(0).isFailure()).isTrue(); // Not positive
      assertThat(schema.validate(55).isFailure()).isTrue(); // Not multiple of 10
    }
  }

  @Nested
  @DisplayName("LongSchema")
  class LongSchemaTests {

    @Test
    void shouldPassValidLong() {
      var schema = Jod.longType();
      var result = schema.validate(9_999_999_999L);

      assertThat(result.isSuccess()).isTrue();
      assertThat(result.getValue()).isEqualTo(9_999_999_999L);
    }

    @Test
    void shouldPassMinMaxConstraints() {
      var schema = Jod.longType().min(0L).max(Long.MAX_VALUE);

      assertThat(schema.validate(0L).isSuccess()).isTrue();
      assertThat(schema.validate(Long.MAX_VALUE).isSuccess()).isTrue();
    }

    @Test
    void shouldPassPositiveConstraint() {
      var schema = Jod.longType().positive();

      assertThat(schema.validate(1L).isSuccess()).isTrue();
      assertThat(schema.validate(-1L).isFailure()).isTrue();
    }

    @Test
    void shouldPassMultipleOfConstraint() {
      var schema = Jod.longType().multipleOf(1_000_000L);

      assertThat(schema.validate(5_000_000L).isSuccess()).isTrue();
      assertThat(schema.validate(5_000_001L).isFailure()).isTrue();
    }
  }

  @Nested
  @DisplayName("DoubleSchema")
  class DoubleSchemaTests {

    @Test
    void shouldPassValidDouble() {
      var schema = Jod.doubleType();
      var result = schema.validate(3.14159);

      assertThat(result.isSuccess()).isTrue();
      assertThat(result.getValue()).isEqualTo(3.14159);
    }

    @Test
    void shouldPassMinMaxConstraints() {
      var schema = Jod.doubleType().min(0.0).max(1.0);

      assertThat(schema.validate(0.0).isSuccess()).isTrue();
      assertThat(schema.validate(0.5).isSuccess()).isTrue();
      assertThat(schema.validate(1.0).isSuccess()).isTrue();
      assertThat(schema.validate(1.1).isFailure()).isTrue();
    }

    @Test
    void shouldPassPositiveConstraint() {
      var schema = Jod.doubleType().positive();

      assertThat(schema.validate(0.001).isSuccess()).isTrue();
      assertThat(schema.validate(0.0).isFailure()).isTrue();
      assertThat(schema.validate(-0.001).isFailure()).isTrue();
    }

    @Test
    void shouldPassNegativeConstraint() {
      var schema = Jod.doubleType().negative();

      assertThat(schema.validate(-0.001).isSuccess()).isTrue();
      assertThat(schema.validate(0.0).isFailure()).isTrue();
    }

    @Test
    void shouldPassMultipleOfConstraint() {
      var schema = Jod.doubleType().multipleOf(0.5);

      assertThat(schema.validate(1.0).isSuccess()).isTrue();
      assertThat(schema.validate(2.5).isSuccess()).isTrue();
      assertThat(schema.validate(0.0).isSuccess()).isTrue();
    }

    @Test
    void shouldFailMultipleOfConstraint() {
      var schema = Jod.doubleType().multipleOf(0.5);
      var result = schema.validate(0.3);

      assertThat(result.isFailure()).isTrue();
      assertThat(result.getErrors().getFirst().code()).isEqualTo("NOT_MULTIPLE");
    }
  }
}
