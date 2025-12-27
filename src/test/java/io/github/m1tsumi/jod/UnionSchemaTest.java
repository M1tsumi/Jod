package io.github.m1tsumi.jod;

import io.github.m1tsumi.jod.schema.Schema;
import io.github.m1tsumi.jod.schema.impl.UnionSchema;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UnionSchemaTest {

    @Test
    void shouldValidateFirstMatchingSchema() {
        UnionSchema<String> schema = Schema.union(
            Schema.string().email(),
            Schema.string().uuid(),
            Schema.string().url()
        );

        var emailResult = schema.validate("user@example.com");
        assertThat(emailResult.isValid()).isTrue();
        assertThat(emailResult.getValue()).contains("user@example.com");

        var uuidResult = schema.validate("550e8400-e29b-41d4-a716-446655440000");
        assertThat(uuidResult.isValid()).isTrue();

        var urlResult = schema.validate("https://example.com");
        assertThat(urlResult.isValid()).isTrue();
    }

    @Test
    void shouldFailWhenNoSchemaMatches() {
        UnionSchema<String> schema = Schema.union(
            Schema.string().email(),
            Schema.string().uuid()
        );

        var result = schema.validate("not-valid-anything");
        assertThat(result.isValid()).isFalse();
        assertThat(result.getErrors()).anyMatch(e -> e.code().equals("UNION_FAILED"));
    }

    @Test
    void shouldApplyTransform() {
        UnionSchema<String> schema = Schema.union(
            Schema.string().email(),
            Schema.string().uuid()
        ).transform(String::toUpperCase);

        var result = schema.validate("user@example.com");
        assertThat(result.isValid()).isTrue();
        assertThat(result.getValue()).contains("USER@EXAMPLE.COM");
    }

    @Test
    void shouldHandleRequiredValidation() {
        UnionSchema<String> schema = Schema.union(
            Schema.string().email(),
            Schema.string().uuid()
        ).required();

        var nullResult = schema.validateNullable(null);
        assertThat(nullResult.isValid()).isFalse();

        var validResult = schema.validate("user@example.com");
        assertThat(validResult.isValid()).isTrue();
    }

    @Test
    void shouldHandleDifferentTypes() {
        // Union of different numeric types
        UnionSchema<Number> schema = Schema.union(
            Schema.integer(),
            Schema.longValue(),
            Schema.doubleValue()
        );

        var intResult = schema.validate(42);
        assertThat(intResult.isValid()).isTrue();

        var longResult = schema.validate(42L);
        assertThat(longResult.isValid()).isTrue();

        var doubleResult = schema.validate(42.5);
        assertThat(doubleResult.isValid()).isTrue();
    }
}</content>
<parameter name="filePath">/home/pepe/Desktop/Github DevWork/Software/Jod/src/test/java/io/github/m1tsumi/jod/UnionSchemaTest.java