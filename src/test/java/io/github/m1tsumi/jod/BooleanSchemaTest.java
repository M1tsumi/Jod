package io.github.m1tsumi.jod;

import io.github.m1tsumi.jod.schema.impl.BooleanSchema;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BooleanSchemaTest {
    
    @Test
    void shouldValidateValidBoolean() {
        BooleanSchema schema = new BooleanSchema();
        
        var trueResult = schema.validate(true);
        assertThat(trueResult.isValid()).isTrue();
        assertThat(trueResult.getValue()).contains(true);
        
        var falseResult = schema.validate(false);
        assertThat(falseResult.isValid()).isTrue();
        assertThat(falseResult.getValue()).contains(false);
    }
    
    @Test
    void shouldValidateNullWhenOptional() {
        BooleanSchema schema = new BooleanSchema();
        var result = schema.validateNullable(null);
        
        assertThat(result.isValid()).isTrue();
        assertThat(result.getValue()).isEmpty();
    }
    
    @Test
    void shouldRejectNullWhenRequired() {
        BooleanSchema schema = new BooleanSchema().required();
        var result = schema.validateNullable(null);
        
        assertThat(result.isValid()).isFalse();
        assertThat(result.getErrors()).hasSize(1);
        assertThat(result.getErrors().get(0).code()).isEqualTo("REQUIRED");
    }
    
    @Test
    void shouldValidateTrueValue() {
        BooleanSchema schema = new BooleanSchema().trueValue();
        
        var validResult = schema.validate(true);
        assertThat(validResult.isValid()).isTrue();
        
        var invalidResult = schema.validate(false);
        assertThat(invalidResult.isValid()).isFalse();
        assertThat(invalidResult.getErrors().get(0).code()).isEqualTo("EXPECTED_VALUE");
    }
    
    @Test
    void shouldValidateFalseValue() {
        BooleanSchema schema = new BooleanSchema().falseValue();
        
        var validResult = schema.validate(false);
        assertThat(validResult.isValid()).isTrue();
        
        var invalidResult = schema.validate(true);
        assertThat(invalidResult.isValid()).isFalse();
        assertThat(invalidResult.getErrors().get(0).code()).isEqualTo("EXPECTED_VALUE");
    }
    
    @Test
    void shouldValidateCustomRule() {
        BooleanSchema schema = new BooleanSchema()
            .custom(b -> b, "Must be true");
        
        var validResult = schema.validate(true);
        assertThat(validResult.isValid()).isTrue();
        
        var invalidResult = schema.validate(false);
        assertThat(invalidResult.isValid()).isFalse();
        assertThat(invalidResult.getErrors().get(0).code()).isEqualTo("CUSTOM");
        assertThat(invalidResult.getErrors().get(0).message()).isEqualTo("Must be true");
    }
    
    @Test
    void shouldChainValidations() {
        BooleanSchema schema = new BooleanSchema()
            .required()
            .trueValue();
        
        var validResult = schema.validate(true);
        assertThat(validResult.isValid()).isTrue();
        
        var invalidResult = schema.validate(false);
        assertThat(invalidResult.isValid()).isFalse();
        assertThat(invalidResult.getErrors()).anyMatch(e -> e.code().equals("EXPECTED_VALUE"));
    }
}
