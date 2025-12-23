package io.github.m1tsumi.jod.schema.impl;

import io.github.m1tsumi.jod.error.ValidationError;
import io.github.m1tsumi.jod.result.ValidationResult;
import io.github.m1tsumi.jod.schema.BaseSchema;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Schema for validating integer values with fluent API.
 */
public class IntegerSchema implements BaseSchema<Integer> {
    
    private final boolean required;
    private final Integer minValue;
    private final Integer maxValue;
    private final boolean positive;
    private final boolean nonNegative;
    private final List<CustomRule<Integer>> customRules;
    
    private record CustomRule<T>(Predicate<T> predicate, String message) {}
    
    private IntegerSchema(
        boolean required,
        Integer minValue,
        Integer maxValue,
        boolean positive,
        boolean nonNegative,
        List<CustomRule<Integer>> customRules
    ) {
        this.required = required;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.positive = positive;
        this.nonNegative = nonNegative;
        this.customRules = customRules;
    }
    
    public IntegerSchema() {
        this(false, null, null, false, false, List.of());
    }
    
    @Override
    public ValidationResult<Integer> validate(Integer value) {
        return validateNullable(value);
    }
    
    @Override
    public ValidationResult<Integer> validateNullable(Integer value) {
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
        if (minValue != null && value < minValue) {
            errors.add(ValidationError.root(
                "MIN_VALUE",
                String.format("Value must be at least %d", minValue),
                value
            ));
        }
        
        // Check max value
        if (maxValue != null && value > maxValue) {
            errors.add(ValidationError.root(
                "MAX_VALUE",
                String.format("Value must be at most %d", maxValue),
                value
            ));
        }
        
        // Check positive
        if (positive && value <= 0) {
            errors.add(ValidationError.root(
                "POSITIVE",
                "Value must be positive",
                value
            ));
        }
        
        // Check non-negative
        if (nonNegative && value < 0) {
            errors.add(ValidationError.root(
                "NON_NEGATIVE",
                "Value must be non-negative",
                value
            ));
        }
        
        // Check custom rules
        for (CustomRule<Integer> rule : customRules) {
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
    public IntegerSchema required() {
        return new IntegerSchema(true, minValue, maxValue, positive, nonNegative, customRules);
    }
    
    @Override
    public IntegerSchema optional() {
        return new IntegerSchema(false, minValue, maxValue, positive, nonNegative, customRules);
    }
    
    @Override
    public IntegerSchema custom(Predicate<Integer> rule, String message) {
        List<CustomRule<Integer>> newRules = new ArrayList<>(customRules);
        newRules.add(new CustomRule<>(rule, message));
        return new IntegerSchema(required, minValue, maxValue, positive, nonNegative, newRules);
    }
    
    /**
     * Sets the minimum value requirement.
     * 
     * @param minValue the minimum value (inclusive)
     * @return a new schema with min value validation
     */
    public IntegerSchema min(int minValue) {
        return new IntegerSchema(required, minValue, maxValue, positive, nonNegative, customRules);
    }
    
    /**
     * Sets the maximum value requirement.
     * 
     * @param maxValue the maximum value (inclusive)
     * @return a new schema with max value validation
     */
    public IntegerSchema max(int maxValue) {
        return new IntegerSchema(required, minValue, maxValue, positive, nonNegative, customRules);
    }
    
    /**
     * Sets both minimum and maximum value requirements.
     * 
     * @param minValue the minimum value (inclusive)
     * @param maxValue the maximum value (inclusive)
     * @return a new schema with range validation
     */
    public IntegerSchema range(int minValue, int maxValue) {
        return new IntegerSchema(required, minValue, maxValue, positive, nonNegative, customRules);
    }
    
    /**
     * Requires the value to be positive (> 0).
     * 
     * @return a new schema with positive validation
     */
    public IntegerSchema positive() {
        return new IntegerSchema(required, minValue, maxValue, true, nonNegative, customRules);
    }
    
    /**
     * Requires the value to be non-negative (>= 0).
     * 
     * @return a new schema with non-negative validation
     */
    public IntegerSchema nonNegative() {
        return new IntegerSchema(required, minValue, maxValue, positive, true, customRules);
    }
}
