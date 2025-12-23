package io.github.m1tsumi.jod;

import io.github.m1tsumi.jod.schema.impl.IntegerSchema;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IntegerSchemaTest {
    
    @Test
    void shouldValidateValidInteger() {
        IntegerSchema schema = new IntegerSchema();
        var result = schema.validate(42);
        
        assertThat(result.isValid()).isTrue();
        assertThat(result.getValue()).contains(42);
    }
    
    @Test
    void shouldValidateNullWhenOptional() {
        IntegerSchema schema = new IntegerSchema();
        var result = schema.validateNullable(null);
        
        assertThat(result.isValid()).isTrue();
        assertThat(result.getValue()).isEmpty();
    }
    
    @Test
    void shouldRejectNullWhenRequired() {
        IntegerSchema schema = new IntegerSchema().required();
        var result = schema.validateNullable(null);
        
        assertThat(result.isValid()).isFalse();
        assertThat(result.getErrors()).hasSize(1);
        assertThat(result.getErrors().get(0).code()).isEqualTo("REQUIRED");
    }
    
    @Test
    void shouldValidateMinValue() {
        IntegerSchema schema = new IntegerSchema().min(18);
        
        var validResult = schema.validate(25);
        assertThat(validResult.isValid()).isTrue();
        
        var invalidResult = schema.validate(15);
        assertThat(invalidResult.isValid()).isFalse();
        assertThat(invalidResult.getErrors().get(0).code()).isEqualTo("MIN_VALUE");
    }
    
    @Test
    void shouldValidateMaxValue() {
        IntegerSchema schema = new IntegerSchema().max(100);
        
        var validResult = schema.validate(50);
        assertThat(validResult.isValid()).isTrue();
        
        var invalidResult = schema.validate(150);
        assertThat(invalidResult.isValid()).isFalse();
        assertThat(invalidResult.getErrors().get(0).code()).isEqualTo("MAX_VALUE");
    }
    
    @Test
    void shouldValidateRange() {
        IntegerSchema schema = new IntegerSchema().range(18, 65);
        
        var validResult = schema.validate(30);
        assertThat(validResult.isValid()).isTrue();
        
        var tooLowResult = schema.validate(15);
        assertThat(tooLowResult.isValid()).isFalse();
        assertThat(tooLowResult.getErrors().get(0).code()).isEqualTo("MIN_VALUE");
        
        var tooHighResult = schema.validate(70);
        assertThat(tooHighResult.isValid()).isFalse();
        assertThat(tooHighResult.getErrors().get(0).code()).isEqualTo("MAX_VALUE");
    }
    
    @Test
    void shouldValidatePositive() {
        IntegerSchema schema = new IntegerSchema().positive();
        
        var validResult = schema.validate(5);
        assertThat(validResult.isValid()).isTrue();
        
        var invalidResult = schema.validate(0);
        assertThat(invalidResult.isValid()).isFalse();
        assertThat(invalidResult.getErrors().get(0).code()).isEqualTo("POSITIVE");
        
        var negativeResult = schema.validate(-5);
        assertThat(negativeResult.isValid()).isFalse();
        assertThat(negativeResult.getErrors().get(0).code()).isEqualTo("POSITIVE");
    }
    
    @Test
    void shouldValidateNonNegative() {
        IntegerSchema schema = new IntegerSchema().nonNegative();
        
        var validResult = schema.validate(5);
        assertThat(validResult.isValid()).isTrue();
        
        var zeroResult = schema.validate(0);
        assertThat(zeroResult.isValid()).isTrue();
        
        var negativeResult = schema.validate(-5);
        assertThat(negativeResult.isValid()).isFalse();
        assertThat(negativeResult.getErrors().get(0).code()).isEqualTo("NON_NEGATIVE");
    }
    
    @Test
    void shouldValidateCustomRule() {
        IntegerSchema schema = new IntegerSchema()
            .custom(i -> i % 2 == 0, "Must be even");
        
        var validResult = schema.validate(4);
        assertThat(validResult.isValid()).isTrue();
        
        var invalidResult = schema.validate(5);
        assertThat(invalidResult.isValid()).isFalse();
        assertThat(invalidResult.getErrors().get(0).code()).isEqualTo("CUSTOM");
        assertThat(invalidResult.getErrors().get(0).message()).isEqualTo("Must be even");
    }
    
    @Test
    void shouldChainMultipleValidations() {
        IntegerSchema schema = new IntegerSchema()
            .required()
            .positive()
            .max(100);
        
        var validResult = schema.validate(50);
        assertThat(validResult.isValid()).isTrue();
        
        var invalidResult = schema.validate(-5);
        assertThat(invalidResult.isValid()).isFalse();
        assertThat(invalidResult.getErrors()).anyMatch(e -> e.code().equals("POSITIVE"));
    }
}
