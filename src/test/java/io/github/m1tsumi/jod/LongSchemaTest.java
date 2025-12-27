package io.github.m1tsumi.jod;

import io.github.m1tsumi.jod.schema.impl.LongSchema;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LongSchemaTest {

    @Test
    void shouldValidateValidLong() {
        LongSchema schema = new LongSchema();
        var result = schema.validate(42L);

        assertThat(result.isValid()).isTrue();
        assertThat(result.getValue()).contains(42L);
    }

    @Test
    void shouldValidateNullWhenOptional() {
        LongSchema schema = new LongSchema();
        var result = schema.validateNullable(null);

        assertThat(result.isValid()).isTrue();
        assertThat(result.getValue()).isEmpty();
    }

    @Test
    void shouldRejectNullWhenRequired() {
        LongSchema schema = new LongSchema().required();
        var result = schema.validateNullable(null);

        assertThat(result.isValid()).isFalse();
        assertThat(result.getErrors()).hasSize(1);
        assertThat(result.getErrors().get(0).code()).isEqualTo("REQUIRED");
    }

    @Test
    void shouldValidateMinValue() {
        LongSchema schema = new LongSchema().min(10L);

        var validResult = schema.validate(25L);
        assertThat(validResult.isValid()).isTrue();

        var invalidResult = schema.validate(5L);
        assertThat(invalidResult.isValid()).isFalse();
        assertThat(invalidResult.getErrors().get(0).code()).isEqualTo("MIN_VALUE");
    }

    @Test
    void shouldValidateMaxValue() {
        LongSchema schema = new LongSchema().max(100L);

        var validResult = schema.validate(50L);
        assertThat(validResult.isValid()).isTrue();

        var invalidResult = schema.validate(150L);
        assertThat(invalidResult.isValid()).isFalse();
        assertThat(invalidResult.getErrors().get(0).code()).isEqualTo("MAX_VALUE");
    }

    @Test
    void shouldValidateRange() {
        LongSchema schema = new LongSchema().range(10L, 100L);

        var validResult = schema.validate(50L);
        assertThat(validResult.isValid()).isTrue();

        var tooSmallResult = schema.validate(5L);
        assertThat(tooSmallResult.isValid()).isFalse();

        var tooLargeResult = schema.validate(150L);
        assertThat(tooLargeResult.isValid()).isFalse();
    }

    @Test
    void shouldValidatePositive() {
        LongSchema schema = new LongSchema().positive();

        var validResult = schema.validate(42L);
        assertThat(validResult.isValid()).isTrue();

        var invalidResult = schema.validate(-5L);
        assertThat(invalidResult.isValid()).isFalse();
        assertThat(invalidResult.getErrors().get(0).code()).isEqualTo("POSITIVE");
    }

    @Test
    void shouldValidateNonNegative() {
        LongSchema schema = new LongSchema().nonNegative();

        var validResult = schema.validate(0L);
        assertThat(validResult.isValid()).isTrue();

        var invalidResult = schema.validate(-5L);
        assertThat(invalidResult.isValid()).isFalse();
        assertThat(invalidResult.getErrors().get(0).code()).isEqualTo("NON_NEGATIVE");
    }

    @Test
    void shouldValidateCustomRule() {
        LongSchema schema = new LongSchema()
            .custom(l -> l % 2 == 0, "Must be even");

        var validResult = schema.validate(4L);
        assertThat(validResult.isValid()).isTrue();

        var invalidResult = schema.validate(5L);
        assertThat(invalidResult.isValid()).isFalse();
        assertThat(invalidResult.getErrors().get(0).code()).isEqualTo("CUSTOM");
        assertThat(invalidResult.getErrors().get(0).message()).isEqualTo("Must be even");
    }

    @Test
    void shouldChainMultipleValidations() {
        LongSchema schema = new LongSchema()
            .required()
            .min(0L)
            .max(1000L)
            .positive();

        var validResult = schema.validate(500L);
        assertThat(validResult.isValid()).isTrue();

        var invalidResult = schema.validate(-5L);
        assertThat(invalidResult.isValid()).isFalse();
        // Should fail on positive check first
        assertThat(invalidResult.getErrors()).anyMatch(e -> e.code().equals("POSITIVE"));
    }
}</content>
<parameter name="filePath">/home/pepe/Desktop/Github DevWork/Software/Jod/src/test/java/io/github/m1tsumi/jod/LongSchemaTest.java