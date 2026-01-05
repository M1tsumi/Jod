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
    void shouldValidateUuid() {
        StringSchema schema = new StringSchema().uuid();

        var validResult = schema.validate("550e8400-e29b-41d4-a716-446655440000");
        assertThat(validResult.isValid()).isTrue();

        var invalidResult = schema.validate("not-a-uuid");
        assertThat(invalidResult.isValid()).isFalse();
        assertThat(invalidResult.getErrors().get(0).code()).isEqualTo("CUSTOM");
        assertThat(invalidResult.getErrors().get(0).message()).isEqualTo("Invalid UUID format");
    }

    @Test
    void shouldValidateUrl() {
        StringSchema schema = new StringSchema().url();

        var validHttpResult = schema.validate("http://example.com");
        assertThat(validHttpResult.isValid()).isTrue();

        var validHttpsResult = schema.validate("https://example.com/path");
        assertThat(validHttpsResult.isValid()).isTrue();

        var invalidResult = schema.validate("not-a-url");
        assertThat(invalidResult.isValid()).isFalse();
        assertThat(invalidResult.getErrors().get(0).code()).isEqualTo("CUSTOM");
        assertThat(invalidResult.getErrors().get(0).message()).isEqualTo("Invalid URL format");
    }

    @Test
    void shouldValidatePhone() {
        StringSchema schema = new StringSchema().phone();

        var validResult = schema.validate("+1234567890");
        assertThat(validResult.isValid()).isTrue();

        var invalidResult = schema.validate("123-456-7890");
        assertThat(invalidResult.isValid()).isFalse();
        assertThat(invalidResult.getErrors().get(0).code()).isEqualTo("CUSTOM");
        assertThat(invalidResult.getErrors().get(0).message()).isEqualTo("Invalid phone number format");
    }

    @Test
    void shouldValidateCreditCard() {
        StringSchema schema = new StringSchema().creditCard();

        var validResult = schema.validate("4532015112830366"); // Valid Visa test number
        assertThat(validResult.isValid()).isTrue();

        var invalidResult = schema.validate("1234567890123456");
        assertThat(invalidResult.isValid()).isFalse();
        assertThat(invalidResult.getErrors().get(0).code()).isEqualTo("CUSTOM");
        assertThat(invalidResult.getErrors().get(0).message()).isEqualTo("Invalid credit card number");
    }

    @Test
    void shouldValidatePostalCode() {
        // US postal code
        var usSchema = new StringSchema().postalCode("US");
        assertThat(usSchema.validate("12345").isValid()).isTrue();
        assertThat(usSchema.validate("12345-6789").isValid()).isTrue();
        assertThat(usSchema.validate("1234").isValid()).isFalse();

        // UK postal code
        var gbSchema = new StringSchema().postalCode("GB");
        assertThat(gbSchema.validate("SW1A 1AA").isValid()).isTrue();
        assertThat(gbSchema.validate("12345").isValid()).isFalse();

        // Canada postal code
        var caSchema = new StringSchema().postalCode("CA");
        assertThat(caSchema.validate("K1A 0A6").isValid()).isTrue();
        assertThat(caSchema.validate("12345").isValid()).isFalse();
    }

    @Test
    void shouldApplyTransform() {
        StringSchema schema = new StringSchema().transform(String::toUpperCase);

        var result = schema.validate("hello world");
        assertThat(result.isValid()).isTrue();
        assertThat(result.getValue()).contains("HELLO WORLD");
    }

    @Test
    void shouldChainTransformWithValidation() {
        StringSchema schema = new StringSchema()
            .min(3)
            .max(20)
            .transform(s -> "prefix_" + s.toLowerCase());

        var result = schema.validate("HELLO");
        assertThat(result.isValid()).isTrue();
        assertThat(result.getValue()).contains("prefix_hello");
    }
