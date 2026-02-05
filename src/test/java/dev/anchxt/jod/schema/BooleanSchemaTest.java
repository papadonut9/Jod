package dev.anchxt.jod.schema;

import dev.anchxt.jod.Jod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("BooleanSchema")
class BooleanSchemaTest {

    @Nested
    @DisplayName("Basic validation")
    class BasicValidation {

        @Test
        void shouldPassValidBoolean() {
            var schema = Jod.bool();

            assertThat(schema.validate(true).isSuccess()).isTrue();
            assertThat(schema.validate(true).getValue()).isTrue();

            assertThat(schema.validate(false).isSuccess()).isTrue();
            assertThat(schema.validate(false).getValue()).isFalse();
        }

        @Test
        void shouldFailOnNull() {
            var schema = Jod.bool();
            var result = schema.validate(null);

            assertThat(result.isFailure()).isTrue();
            assertThat(result.getErrors().getFirst().code()).isEqualTo("NULL_VALUE");
        }
    }

    @Nested
    @DisplayName("isTrue constraint")
    class IsTrueConstraint {

        @Test
        void shouldPassWhenTrue() {
            var schema = Jod.bool().isTrue();
            var result = schema.validate(true);

            assertThat(result.isSuccess()).isTrue();
            assertThat(result.getValue()).isTrue();
        }

        @Test
        void shouldFailWhenFalse() {
            var schema = Jod.bool().isTrue();
            var result = schema.validate(false);

            assertThat(result.isFailure()).isTrue();
            assertThat(result.getErrors().getFirst().code()).isEqualTo("NOT_TRUE");
            assertThat(result.getErrors().getFirst().message()).isEqualTo("Value must be true");
        }
    }

    @Nested
    @DisplayName("isFalse constraint")
    class IsFalseConstraint {

        @Test
        void shouldPassWhenFalse() {
            var schema = Jod.bool().isFalse();
            var result = schema.validate(false);

            assertThat(result.isSuccess()).isTrue();
            assertThat(result.getValue()).isFalse();
        }

        @Test
        void shouldFailWhenTrue() {
            var schema = Jod.bool().isFalse();
            var result = schema.validate(true);

            assertThat(result.isFailure()).isTrue();
            assertThat(result.getErrors().getFirst().code()).isEqualTo("NOT_FALSE");
            assertThat(result.getErrors().getFirst().message()).isEqualTo("Value must be false");
        }
    }

    @Nested
    @DisplayName("Conflicting constraints")
    class ConflictingConstraints {

        @Test
        void shouldCollectMultipleErrorsForConflictingConstraints() {
            // Intentionally conflicting constraints - value can never be both true and
            // false
            var schema = Jod.bool().isTrue().isFalse();

            var resultTrue = schema.validate(true);
            assertThat(resultTrue.isFailure()).isTrue();
            assertThat(resultTrue.getErrors()).hasSize(1);
            assertThat(resultTrue.getErrors().getFirst().code()).isEqualTo("NOT_FALSE");

            var resultFalse = schema.validate(false);
            assertThat(resultFalse.isFailure()).isTrue();
            assertThat(resultFalse.getErrors()).hasSize(1);
            assertThat(resultFalse.getErrors().getFirst().code()).isEqualTo("NOT_TRUE");
        }
    }
}
