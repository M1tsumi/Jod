package io.github.m1tsumi.jod.schema.impl;

import io.github.m1tsumi.jod.error.ValidationError;
import io.github.m1tsumi.jod.result.ValidationResult;
import io.github.m1tsumi.jod.schema.BaseSchema;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Schema for validating string values with fluent API.
 */
public class StringSchema implements BaseSchema<String> {
    
    private final boolean required;
    private final Integer minLength;
    private final Integer maxLength;
    private final String pattern;
    private final boolean email;
    private final List<CustomRule<String>> customRules;
    private final java.util.function.Function<String, String> transform;
    
    private record CustomRule<T>(Predicate<T> predicate, String message) {}
    
    private StringSchema(
        boolean required,
        Integer minLength,
        Integer maxLength,
        String pattern,
        boolean email,
        List<CustomRule<String>> customRules,
        java.util.function.Function<String, String> transform
    ) {
        this.required = required;
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.pattern = pattern;
        this.email = email;
        this.customRules = customRules;
        this.transform = transform != null ? transform : java.util.function.Function.identity();
    }
    
    public StringSchema() {
        this(false, null, null, null, false, List.of(), null);
    }
    
    @Override
    public ValidationResult<String> validate(String value) {
        return validateNullable(value);
    }
    
    @Override
    public ValidationResult<String> validateNullable(String value) {
        List<ValidationError> errors = new ArrayList<>();
        
        // Check null
        if (value == null) {
            if (required) {
                errors.add(ValidationError.root("REQUIRED", "Value is required", null));
            }
            return errors.isEmpty() 
                ? ValidationResult.success(null)
                : ValidationResult.failure(errors);
        }
        
        // Check min length
        if (minLength != null && value.length() < minLength) {
            errors.add(ValidationError.root(
                "MIN_LENGTH",
                String.format("String must be at least %d characters long", minLength),
                value
            ));
        }
        
        // Check max length
        if (maxLength != null && value.length() > maxLength) {
            errors.add(ValidationError.root(
                "MAX_LENGTH",
                String.format("String must be at most %d characters long", maxLength),
                value
            ));
        }
        
        // Check pattern
        if (pattern != null && !value.matches(pattern)) {
            errors.add(ValidationError.root(
                "PATTERN",
                "String does not match required pattern",
                value
            ));
        }
        
        // Check email
        if (email && !isValidEmail(value)) {
            errors.add(ValidationError.root(
                "EMAIL",
                "Invalid email format",
                value
            ));
        }
        
        // Check custom rules
        for (CustomRule<String> rule : customRules) {
            if (!rule.predicate.test(value)) {
                errors.add(ValidationError.root(
                    "CUSTOM",
                    rule.message,
                    value
                ));
            }
        }
        
        return errors.isEmpty() 
            ? ValidationResult.success(transform.apply(value))
            : ValidationResult.failure(errors);
    }
    
    @Override
    public StringSchema required() {
        return new StringSchema(true, minLength, maxLength, pattern, email, customRules, transform);
    }
    
    @Override
    public StringSchema optional() {
        return new StringSchema(false, minLength, maxLength, pattern, email, customRules, transform);
    }
    
    @Override
    public StringSchema custom(Predicate<String> rule, String message) {
        List<CustomRule<String>> newRules = new ArrayList<>(customRules);
        newRules.add(new CustomRule<>(rule, message));
        return new StringSchema(required, minLength, maxLength, pattern, email, newRules, transform);
    }
    
    @Override
    public StringSchema transform(java.util.function.Function<String, String> transform) {
        return new StringSchema(required, minLength, maxLength, pattern, email, customRules, transform);
    }
    
    /**
     * Sets the minimum length requirement.
     * 
     * @param minLength the minimum length (inclusive)
     * @return a new schema with min length validation
     */
    public StringSchema min(int minLength) {
        return new StringSchema(required, minLength, maxLength, pattern, email, customRules, transform);
    }
    
    /**
     * Sets the maximum length requirement.
     * 
     * @param maxLength the maximum length (inclusive)
     * @return a new schema with max length validation
     */
    public StringSchema max(int maxLength) {
        return new StringSchema(required, minLength, maxLength, pattern, email, customRules, transform);
    }
    
    /**
     * Sets both minimum and maximum length requirements.
     * 
     * @param minLength the minimum length (inclusive)
     * @param maxLength the maximum length (inclusive)
     * @return a new schema with length validation
     */
    public StringSchema length(int minLength, int maxLength) {
        return new StringSchema(required, minLength, maxLength, pattern, email, customRules, transform);
    }
    
    /**
     * Requires the string to match a regular expression pattern.
     * 
     * @param regex the regular expression pattern
     * @return a new schema with pattern validation
     */
    public StringSchema regex(String regex) {
        return new StringSchema(required, minLength, maxLength, regex, email, customRules, transform);
    }
    
    /**
     * Requires the string to be a valid email address.
     * 
     * @return a new schema with email validation
     */
    public StringSchema email() {
        return new StringSchema(required, minLength, maxLength, pattern, true, customRules, transform);
    }
    
    /**
     * Requires the string to be one of the specified values.
     * 
     * @param allowedValues the allowed values
     * @return a new schema with enum validation
     */
    public StringSchema oneOf(String... allowedValues) {
        return custom(s -> java.util.List.of(allowedValues).contains(s), 
                     "Must be one of: " + java.util.Arrays.toString(allowedValues));
    }
    
    /**
     * Requires the string to be a valid UUID.
     * 
     * @return a new schema with UUID validation
     */
    public StringSchema uuid() {
        return custom(this::isValidUuid, "Invalid UUID format");
    }
    
    /**
     * Requires the string to be a valid URL.
     * 
     * @return a new schema with URL validation
     */
    public StringSchema url() {
        return custom(this::isValidUrl, "Invalid URL format");
    }
    
    /**
     * Requires the string to be a valid phone number (E.164 format).
     * 
     * @return a new schema with phone number validation
     */
    public StringSchema phone() {
        return custom(this::isValidPhone, "Invalid phone number format");
    }
    
    /**
     * Basic email validation using regex.
     * This is a simplified version - production use should consider more sophisticated validation.
     */
    private boolean isValidEmail(String email) {
        if (email == null) return false;
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
    }
    
    /**
     * UUID validation using regex.
     */
    private boolean isValidUuid(String uuid) {
        if (uuid == null) return false;
        String uuidRegex = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
        return uuid.matches(uuidRegex);
    }
    
    /**
     * Basic URL validation using regex.
     */
    private boolean isValidUrl(String url) {
        if (url == null) return false;
        String urlRegex = "^https?://[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}(/.*)?$";
        return url.matches(urlRegex);
    }
    
    /**
     * Basic phone number validation (E.164 format).
     */
    private boolean isValidPhone(String phone) {
        if (phone == null) return false;
        String phoneRegex = "^\\+[1-9]\\d{1,14}$";
        return phone.matches(phoneRegex);
    }
}
