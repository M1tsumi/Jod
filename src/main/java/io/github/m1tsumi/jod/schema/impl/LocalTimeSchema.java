package io.github.m1tsumi.jod.schema.impl;

import io.github.m1tsumi.jod.error.ValidationError;
import io.github.m1tsumi.jod.result.ValidationResult;
import io.github.m1tsumi.jod.schema.BaseSchema;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Schema for validating LocalTime values with fluent API.
 */
public class LocalTimeSchema implements BaseSchema<LocalTime> {
    
    private final boolean required;
    private final LocalTime minTime;
    private final LocalTime maxTime;
    private final List<CustomRule<LocalTime>> customRules;
    
    private record CustomRule<T>(Predicate<T> predicate, String message) {}
    
    private LocalTimeSchema(
        boolean required,
        LocalTime minTime,
        LocalTime maxTime,
        List<CustomRule<LocalTime>> customRules
    ) {
        this.required = required;
        this.minTime = minTime;
        this.maxTime = maxTime;
        this.customRules = customRules;
    }
    
    public LocalTimeSchema() {
        this(false, null, null, List.of());
    }
    
    @Override
    public ValidationResult<LocalTime> validate(LocalTime value) {
        return validateNullable(value);
    }
    
    @Override
    public ValidationResult<LocalTime> validateNullable(LocalTime value) {
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
        
        // Check min time
        if (minTime != null && value.isBefore(minTime)) {
            errors.add(ValidationError.root(
                "MIN_TIME",
                String.format("Time must be on or after %s", minTime),
                value
            ));
        }
        
        // Check max time
        if (maxTime != null && value.isAfter(maxTime)) {
            errors.add(ValidationError.root(
                "MAX_TIME",
                String.format("Time must be on or before %s", maxTime),
                value
            ));
        }
        
        // Check custom rules
        for (CustomRule<LocalTime> rule : customRules) {
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
    public LocalTimeSchema required() {
        return new LocalTimeSchema(true, minTime, maxTime, customRules);
    }
    
    @Override
    public LocalTimeSchema optional() {
        return new LocalTimeSchema(false, minTime, maxTime, customRules);
    }
    
    @Override
    public LocalTimeSchema custom(Predicate<LocalTime> rule, String message) {
        List<CustomRule<LocalTime>> newRules = new ArrayList<>(customRules);
        newRules.add(new CustomRule<>(rule, message));
        return new LocalTimeSchema(required, minTime, maxTime, newRules);
    }
    
    /**
     * Sets the minimum time requirement.
     * 
     * @param minTime the minimum time (inclusive)
     * @return a new schema with min time validation
     */
    public LocalTimeSchema min(LocalTime minTime) {
        return new LocalTimeSchema(required, minTime, maxTime, customRules);
    }
    
    /**
     * Sets the maximum time requirement.
     * 
     * @param maxTime the maximum time (inclusive)
     * @return a new schema with max time validation
     */
    public LocalTimeSchema max(LocalTime maxTime) {
        return new LocalTimeSchema(required, minTime, maxTime, customRules);
    }
    
    /**
     * Sets both minimum and maximum time requirements.
     * 
     * @param minTime the minimum time (inclusive)
     * @param maxTime the maximum time (inclusive)
     * @return a new schema with time range validation
     */
    public LocalTimeSchema range(LocalTime minTime, LocalTime maxTime) {
        return new LocalTimeSchema(required, minTime, maxTime, customRules);
    }
}