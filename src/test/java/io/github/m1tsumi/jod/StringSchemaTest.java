package io.github.m1tsumi.jod;

import io.github.m1tsumi.jod.schema.impl.StringSchema;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class StringSchemaTest {
    
    @Test
    void shouldValidateValidString() {
        StringSchema schema = new StringSchema();
        var result = schema.validate("hello");
        
        assertThat(result.isValid()).isTrue();
        assertThat(result.getValue()).contains("hello");
    }
    
    @Test
    void shouldValidateNullWhenOptional() {
        StringSchema schema = new StringSchema();
        var result = schema.validateNullable(null);
        
        assertThat(result.isValid()).isTrue();
        assertThat(result.getValue()).isEmpty();
    }
    
    @Test
    void shouldRejectNullWhenRequired() {
        StringSchema schema = new StringSchema().required();
        var result = schema.validateNullable(null);
        
        assertThat(result.isValid()).isFalse();
        assertThat(result.getErrors()).hasSize(1);
        assertThat(result.getErrors().get(0).code()).isEqualTo("REQUIRED");
    }
    
    @Test
    void shouldValidateMinLength() {
        StringSchema schema = new StringSchema().min(5);
        
        var validResult = schema.validate("hello world");
        assertThat(validResult.isValid()).isTrue();
        
        var invalidResult = schema.validate("hi");
        assertThat(invalidResult.isValid()).isFalse();
        assertThat(invalidResult.getErrors().get(0).code()).isEqualTo("MIN_LENGTH");
    }
    
    @Test
    void shouldValidateMaxLength() {
        StringSchema schema = new StringSchema().max(10);
        
        var validResult = schema.validate("hello");
        assertThat(validResult.isValid()).isTrue();
        
        var invalidResult = schema.validate("this is too long");
        assertThat(invalidResult.isValid()).isFalse();
        assertThat(invalidResult.getErrors().get(0).code()).isEqualTo("MAX_LENGTH");
    }
    
    @Test
    void shouldValidateLengthRange() {
        StringSchema schema = new StringSchema().length(3, 10);
        
        var validResult = schema.validate("hello");
        assertThat(validResult.isValid()).isTrue();
        
        var tooShortResult = schema.validate("hi");
        assertThat(tooShortResult.isValid()).isFalse();
        assertThat(tooShortResult.getErrors().get(0).code()).isEqualTo("MIN_LENGTH");
        
        var tooLongResult = schema.validate("this is way too long");
        assertThat(tooLongResult.isValid()).isFalse();
        assertThat(tooLongResult.getErrors().get(0).code()).isEqualTo("MAX_LENGTH");
    }
    
    @Test
    void shouldValidateRegex() {
        StringSchema schema = new StringSchema().regex("[a-z]+");
        
        var validResult = schema.validate("hello");
        assertThat(validResult.isValid()).isTrue();
        
        var invalidResult = schema.validate("Hello123");
        assertThat(invalidResult.isValid()).isFalse();
        assertThat(invalidResult.getErrors().get(0).code()).isEqualTo("PATTERN");
    }
    
    @ParameterizedTest
    @ValueSource(strings = {
        "test@example.com",
        "user.name+tag@domain.co.uk",
        "123@456.com"
    })
    void shouldValidateValidEmails(String email) {
        StringSchema schema = new StringSchema().email();
        var result = schema.validate(email);
        
        assertThat(result.isValid()).isTrue();
    }
    
    @ParameterizedTest
    @ValueSource(strings = {
        "invalid",
        "@domain.com",
        "user@",
        "user..name@domain.com"
    })
    void shouldRejectInvalidEmails(String email) {
        StringSchema schema = new StringSchema().email();
        var result = schema.validate(email);
        
        assertThat(result.isValid()).isFalse();
        assertThat(result.getErrors().get(0).code()).isEqualTo("EMAIL");
    }
    
    @Test
    void shouldValidateCustomRule() {
        StringSchema schema = new StringSchema()
            .custom(s -> s.startsWith("prefix"), "Must start with prefix");
        
        var validResult = schema.validate("prefix_value");
        assertThat(validResult.isValid()).isTrue();
        
        var invalidResult = schema.validate("other_value");
        assertThat(invalidResult.isValid()).isFalse();
        assertThat(invalidResult.getErrors().get(0).code()).isEqualTo("CUSTOM");
        assertThat(invalidResult.getErrors().get(0).message()).isEqualTo("Must start with prefix");
    }
    
    @Test
    void shouldChainMultipleValidations() {
        StringSchema schema = new StringSchema()
            .required()
            .min(5)
            .max(20)
            .email();
        
        var validResult = schema.validate("test@example.com");
        assertThat(validResult.isValid()).isTrue();
        
        var invalidResult = schema.validate("bad");
        assertThat(invalidResult.isValid()).isFalse();
        // Should fail on min length first
        assertThat(invalidResult.getErrors()).anyMatch(e -> e.code().equals("MIN_LENGTH"));
    }
}
