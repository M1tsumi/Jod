package io.github.m1tsumi.jod;

import io.github.m1tsumi.jod.schema.impl.DoubleSchema;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DoubleSchemaTest {

    @Test
    void shouldValidateValidDouble() {
        DoubleSchema schema = new DoubleSchema();
        var result = schema.validate(42.5);

        assertThat(result.isValid()).isTrue();
        assertThat(result.getValue()).contains(42.5);
    }

    @Test
    void shouldValidateNullWhenOptional() {
        DoubleSchema schema = new DoubleSchema();
        var result = schema.validateNullable(null);

        assertThat(result.isValid()).isTrue();
        assertThat(result.getValue()).isEmpty();
    }

    @Test
    void shouldRejectNullWhenRequired() {
        DoubleSchema schema = new DoubleSchema().required();
        var result = schema.validateNullable(null);

        assertThat(result.isValid()).isFalse();
        assertThat(result.getErrors()).hasSize(1);
        assertThat(result.getErrors().get(0).code()).isEqualTo("REQUIRED");
    }

    @Test
    void shouldValidateMinValue() {
        DoubleSchema schema = new DoubleSchema().min(10.0);

        var validResult = schema.validate(25.5);
        assertThat(validResult.isValid()).isTrue();

        var invalidResult = schema.validate(5.5);
        assertThat(invalidResult.isValid()).isFalse();
        assertThat(invalidResult.getErrors().get(0).code()).isEqualTo("MIN_VALUE");
    }

    @Test
    void shouldValidateMaxValue() {
        DoubleSchema schema = new DoubleSchema().max(100.0);

        var validResult = schema.validate(50.5);
        assertThat(validResult.isValid()).isTrue();

        var invalidResult = schema.validate(150.5);
        assertThat(invalidResult.isValid()).isFalse();
        assertThat(invalidResult.getErrors().get(0).code()).isEqualTo("MAX_VALUE");
    }

    @Test
    void shouldValidateRange() {
        DoubleSchema schema = new DoubleSchema().range(10.0, 100.0);

        var validResult = schema.validate(50.5);
        assertThat(validResult.isValid()).isTrue();

        var tooSmallResult = schema.validate(5.5);
        assertThat(tooSmallResult.isValid()).isFalse();

        var tooLargeResult = schema.validate(150.5);
        assertThat(tooLargeResult.isValid()).isFalse();
    }

    @Test
    void shouldValidatePositive() {
        DoubleSchema schema = new DoubleSchema().positive();

        var validResult = schema.validate(42.5);
        assertThat(validResult.isValid()).isTrue();

        var invalidResult = schema.validate(-5.5);
        assertThat(invalidResult.isValid()).isFalse();
        assertThat(invalidResult.getErrors().get(0).code()).isEqualTo("POSITIVE");
    }

    @Test
    void shouldValidateNonNegative() {
        DoubleSchema schema = new DoubleSchema().nonNegative();

        var validResult = schema.validate(0.0);
        assertThat(validResult.isValid()).isTrue();

        var invalidResult = schema.validate(-5.5);
        assertThat(invalidResult.isValid()).isFalse();
        assertThat(invalidResult.getErrors().get(0).code()).isEqualTo("NON_NEGATIVE");
    }

    @Test
    void shouldValidateFinite() {
        DoubleSchema schema = new DoubleSchema().finite();

        var validResult = schema.validate(42.5);
        assertThat(validResult.isValid()).isTrue();

        var nanResult = schema.validate(Double.NaN);
        assertThat(nanResult.isValid()).isFalse();
        assertThat(nanResult.getErrors().get(0).code()).isEqualTo("FINITE");

        var positiveInfinityResult = schema.validate(Double.POSITIVE_INFINITY);
        assertThat(positiveInfinityResult.isValid()).isFalse();

        var negativeInfinityResult = schema.validate(Double.NEGATIVE_INFINITY);
        assertThat(negativeInfinityResult.isValid()).isFalse();
    }

    @Test
    void shouldValidateCustomRule() {
        DoubleSchema schema = new DoubleSchema()
            .custom(d -> d > 0 && d < 1, "Must be between 0 and 1");

        var validResult = schema.validate(0.5);
        assertThat(validResult.isValid()).isTrue();

        var invalidResult = schema.validate(1.5);
        assertThat(invalidResult.isValid()).isFalse();
        assertThat(invalidResult.getErrors().get(0).code()).isEqualTo("CUSTOM");
    }

    @Test
    void shouldChainMultipleValidations() {
        DoubleSchema schema = new DoubleSchema()
            .required()
            .min(0.0)
            .max(100.0)
            .finite()
            .positive();

        var validResult = schema.validate(50.5);
        assertThat(validResult.isValid()).isTrue();

        var invalidResult = schema.validate(-5.5);
        assertThat(invalidResult.isValid()).isFalse();
        // Should fail on positive check
        assertThat(invalidResult.getErrors()).anyMatch(e -> e.code().equals("POSITIVE"));
    }
}</content>
<parameter name="filePath">/home/pepe/Desktop/Github DevWork/Software/Jod/src/test/java/io/github/m1tsumi/jod/DoubleSchemaTest.java