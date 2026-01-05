package io.github.m1tsumi.jod.schema.impl;

import io.github.m1tsumi.jod.error.ValidationError;
import io.github.m1tsumi.jod.result.ValidationResult;
import io.github.m1tsumi.jod.schema.BaseSchema;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Schema for validating LocalDateTime values with fluent API.
 */
public class LocalDateTimeSchema implements BaseSchema<LocalDateTime> {
    
    private final boolean required;
    private final LocalDateTime minDateTime;
    private final LocalDateTime maxDateTime;
    private final List<CustomRule<LocalDateTime>> customRules;
    
    private record CustomRule<T>(Predicate<T> predicate, String message) {}
    
    private LocalDateTimeSchema(
        boolean required,
        LocalDateTime minDateTime,
        LocalDateTime maxDateTime,
        List<CustomRule<LocalDateTime>> customRules
    ) {
        this.required = required;
        this.minDateTime = minDateTime;
        this.maxDateTime = maxDateTime;
        this.customRules = customRules;
    }
    
    public LocalDateTimeSchema() {
        this(false, null, null, List.of());
    }
    
    @Override
    public ValidationResult<LocalDateTime> validate(LocalDateTime value) {
        return validateNullable(value);
    }
    
    @Override
    public ValidationResult<LocalDateTime> validateNullable(LocalDateTime value) {
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
        
        // Check min date time
        if (minDateTime != null && value.isBefore(minDateTime)) {
            errors.add(ValidationError.root(
                "MIN_DATETIME",
                String.format("DateTime must be on or after %s", minDateTime),
                value
            ));
        }
        
        // Check max date time
        if (maxDateTime != null && value.isAfter(maxDateTime)) {
            errors.add(ValidationError.root(
                "MAX_DATETIME",
                String.format("DateTime must be on or before %s", maxDateTime),
                value
            ));
        }
        
        // Check custom rules
        for (CustomRule<LocalDateTime> rule : customRules) {
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
    public LocalDateTimeSchema required() {
        return new LocalDateTimeSchema(true, minDateTime, maxDateTime, customRules);
    }
    
    @Override
    public LocalDateTimeSchema optional() {
        return new LocalDateTimeSchema(false, minDateTime, maxDateTime, customRules);
    }
    
    @Override
    public LocalDateTimeSchema custom(Predicate<LocalDateTime> rule, String message) {
        List<CustomRule<LocalDateTime>> newRules = new ArrayList<>(customRules);
        newRules.add(new CustomRule<>(rule, message));
        return new LocalDateTimeSchema(required, minDateTime, maxDateTime, newRules);
    }
    
    /**
     * Sets the minimum date time requirement.
     * 
     * @param minDateTime the minimum date time (inclusive)
     * @return a new schema with min date time validation
     */
    public LocalDateTimeSchema min(LocalDateTime minDateTime) {
        return new LocalDateTimeSchema(required, minDateTime, maxDateTime, customRules);
    }
    
    /**
     * Sets the maximum date time requirement.
     * 
     * @param maxDateTime the maximum date time (inclusive)
     * @return a new schema with max date time validation
     */
    public LocalDateTimeSchema max(LocalDateTime maxDateTime) {
        return new LocalDateTimeSchema(required, minDateTime, maxDateTime, customRules);
    }
    
    /**
     * Sets both minimum and maximum date time requirements.
     * 
     * @param minDateTime the minimum date time (inclusive)
     * @param maxDateTime the maximum date time (inclusive)
     * @return a new schema with date time range validation
     */
    public LocalDateTimeSchema range(LocalDateTime minDateTime, LocalDateTime maxDateTime) {
        return new LocalDateTimeSchema(required, minDateTime, maxDateTime, customRules);
    }
    
    /**
     * Requires the date time to be in the past.
     * 
     * @return a new schema with past date time validation
     */
    public LocalDateTimeSchema past() {
        return custom(dateTime -> dateTime.isBefore(LocalDateTime.now()), "DateTime must be in the past");
    }
    
    /**
     * Requires the date time to be in the future.
     * 
     * @return a new schema with future date time validation
     */
    public LocalDateTimeSchema future() {
        return custom(dateTime -> dateTime.isAfter(LocalDateTime.now()), "DateTime must be in the future");
    }
}