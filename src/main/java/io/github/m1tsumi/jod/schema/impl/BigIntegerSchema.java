package io.github.m1tsumi.jod.schema.impl;

import io.github.m1tsumi.jod.error.ValidationError;
import io.github.m1tsumi.jod.result.ValidationResult;
import io.github.m1tsumi.jod.schema.BaseSchema;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Schema for validating BigInteger values with fluent API.
 */
public class BigIntegerSchema implements BaseSchema<BigInteger> {
    
    private final boolean required;
    private final BigInteger minValue;
    private final BigInteger maxValue;
    private final boolean positive;
    private final boolean nonNegative;
    private final List<CustomRule<BigInteger>> customRules;
    
    private record CustomRule<T>(Predicate<T> predicate, String message) {}
    
    private BigIntegerSchema(
        boolean required,
        BigInteger minValue,
        BigInteger maxValue,
        boolean positive,
        boolean nonNegative,
        List<CustomRule<BigInteger>> customRules
    ) {
        this.required = required;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.positive = positive;
        this.nonNegative = nonNegative;
        this.customRules = customRules;
    }
    
    public BigIntegerSchema() {
        this(false, null, null, false, false, List.of());
    }
    
    @Override
    public ValidationResult<BigInteger> validate(BigInteger value) {
        return validateNullable(value);
    }
    
    @Override
    public ValidationResult<BigInteger> validateNullable(BigInteger value) {
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
        
        // Check min value
        if (minValue != null && value.compareTo(minValue) < 0) {
            errors.add(ValidationError.root(
                "MIN_VALUE",
                String.format("Value must be at least %s", minValue),
                value
            ));
        }
        
        // Check max value
        if (maxValue != null && value.compareTo(maxValue) > 0) {
            errors.add(ValidationError.root(
                "MAX_VALUE",
                String.format("Value must be at most %s", maxValue),
                value
            ));
        }
        
        // Check positive
        if (positive && value.compareTo(BigInteger.ZERO) <= 0) {
            errors.add(ValidationError.root(
                "POSITIVE",
                "Value must be positive",
                value
            ));
        }
        
        // Check non-negative
        if (nonNegative && value.compareTo(BigInteger.ZERO) < 0) {
            errors.add(ValidationError.root(
                "NON_NEGATIVE",
                "Value must be non-negative",
                value
            ));
        }
        
        // Check custom rules
        for (CustomRule<BigInteger> rule : customRules) {
            if (!rule.predicate.test(value)) {
                errors.add(ValidationError.root(
                    "CUSTOM",
                    rule.message,
                    value
                ));
            }
        }
        
        return errors.isEmpty() 
            ? ValidationResult.success(value)
            : ValidationResult.failure(errors);
    }
    
    @Override
    public BigIntegerSchema required() {
        return new BigIntegerSchema(true, minValue, maxValue, positive, nonNegative, customRules);
    }
    
    @Override
    public BigIntegerSchema optional() {
        return new BigIntegerSchema(false, minValue, maxValue, positive, nonNegative, customRules);
    }
    
    @Override
    public BigIntegerSchema custom(Predicate<BigInteger> rule, String message) {
        List<CustomRule<BigInteger>> newRules = new ArrayList<>(customRules);
        newRules.add(new CustomRule<>(rule, message));
        return new BigIntegerSchema(required, minValue, maxValue, positive, nonNegative, newRules);
    }
    
    /**
     * Sets the minimum value requirement.
     * 
     * @param minValue the minimum value (inclusive)
     * @return a new schema with min value validation
     */
    public BigIntegerSchema min(BigInteger minValue) {
        return new BigIntegerSchema(required, minValue, maxValue, positive, nonNegative, customRules);
    }
    
    /**
     * Sets the maximum value requirement.
     * 
     * @param maxValue the maximum value (inclusive)
     * @return a new schema with max value validation
     */
    public BigIntegerSchema max(BigInteger maxValue) {
        return new BigIntegerSchema(required, minValue, maxValue, positive, nonNegative, customRules);
    }
    
    /**
     * Sets both minimum and maximum value requirements.
     * 
     * @param minValue the minimum value (inclusive)
     * @param maxValue the maximum value (inclusive)
     * @return a new schema with range validation
     */
    public BigIntegerSchema range(BigInteger minValue, BigInteger maxValue) {
        return new BigIntegerSchema(required, minValue, maxValue, positive, nonNegative, customRules);
    }
    
    /**
     * Requires the value to be positive (> 0).
     * 
     * @return a new schema with positive validation
     */
    public BigIntegerSchema positive() {
        return new BigIntegerSchema(required, minValue, maxValue, true, nonNegative, customRules);
    }
    
    /**
     * Requires the value to be non-negative (>= 0).
     * 
     * @return a new schema with non-negative validation
     */
    public BigIntegerSchema nonNegative() {
        return new BigIntegerSchema(required, minValue, maxValue, positive, true, customRules);
    }
}