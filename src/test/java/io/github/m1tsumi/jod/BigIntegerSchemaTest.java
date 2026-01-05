package io.github.m1tsumi.jod;

import io.github.m1tsumi.jod.result.ValidationResult;
import io.github.m1tsumi.jod.schema.Schema;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;

class BigIntegerSchemaTest {

    @Test
    void shouldValidateValidBigInteger() {
        var schema = Schema.bigInteger();
        var result = schema.validate(BigInteger.valueOf(123456789));
        
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue()).isEqualTo(BigInteger.valueOf(123456789));
    }

    @Test
    void shouldFailValidationForNullWhenRequired() {
        var schema = Schema.bigInteger().required();
        var result = schema.validate(null);
        
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getErrors()).hasSize(1);
        assertThat(result.getErrors().get(0).message()).isEqualTo("Value is required");
    }

    @Test
    void shouldAllowNullWhenOptional() {
        var schema = Schema.bigInteger().optional();
        var result = schema.validate(null);
        
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue()).isNull();
    }

    @Test
    void shouldValidateMinValue() {
        var schema = Schema.bigInteger().min(BigInteger.valueOf(100));
        
        assertThat(schema.validate(BigInteger.valueOf(150)).isSuccess()).isTrue();
        assertThat(schema.validate(BigInteger.valueOf(50)).isFailure()).isTrue();
    }

    @Test
    void shouldValidateMaxValue() {
        var schema = Schema.bigInteger().max(BigInteger.valueOf(100));
        
        assertThat(schema.validate(BigInteger.valueOf(50)).isSuccess()).isTrue();
        assertThat(schema.validate(BigInteger.valueOf(150)).isFailure()).isTrue();
    }

    @Test
    void shouldValidatePositive() {
        var schema = Schema.bigInteger().positive();
        
        assertThat(schema.validate(BigInteger.valueOf(1)).isSuccess()).isTrue();
        assertThat(schema.validate(BigInteger.valueOf(0)).isFailure()).isTrue();
        assertThat(schema.validate(BigInteger.valueOf(-1)).isFailure()).isTrue();
    }

    @Test
    void shouldValidateNonNegative() {
        var schema = Schema.bigInteger().nonNegative();
        
        assertThat(schema.validate(BigInteger.valueOf(1)).isSuccess()).isTrue();
        assertThat(schema.validate(BigInteger.valueOf(0)).isSuccess()).isTrue();
        assertThat(schema.validate(BigInteger.valueOf(-1)).isFailure()).isTrue();
    }

    @Test
    void shouldValidateRange() {
        var schema = Schema.bigInteger().range(BigInteger.valueOf(10), BigInteger.valueOf(20));
        
        assertThat(schema.validate(BigInteger.valueOf(15)).isSuccess()).isTrue();
        assertThat(schema.validate(BigInteger.valueOf(5)).isFailure()).isTrue();
        assertThat(schema.validate(BigInteger.valueOf(25)).isFailure()).isTrue();
    }
}