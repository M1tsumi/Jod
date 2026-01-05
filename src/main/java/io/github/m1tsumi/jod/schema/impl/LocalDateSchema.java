package io.github.m1tsumi.jod.schema.impl;

import io.github.m1tsumi.jod.error.ValidationError;
import io.github.m1tsumi.jod.result.ValidationResult;
import io.github.m1tsumi.jod.schema.BaseSchema;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Schema for validating LocalDate values with fluent API.
 */
public class LocalDateSchema implements BaseSchema<LocalDate> {
    
    private final boolean required;
    private final LocalDate minDate;
    private final LocalDate maxDate;
    private final List<CustomRule<LocalDate>> customRules;
    
    private record CustomRule<T>(Predicate<T> predicate, String message) {}
    
    private LocalDateSchema(
        boolean required,
        LocalDate minDate,
        LocalDate maxDate,
        List<CustomRule<LocalDate>> customRules
    ) {
        this.required = required;
        this.minDate = minDate;
        this.maxDate = maxDate;
        this.customRules = customRules;
    }
    
    public LocalDateSchema() {
        this(false, null, null, List.of());
    }
    
    @Override
    public ValidationResult<LocalDate> validate(LocalDate value) {
        return validateNullable(value);
    }
    
    @Override
    public ValidationResult<LocalDate> validateNullable(LocalDate value) {
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
        
        // Check min date
        if (minDate != null && value.isBefore(minDate)) {
            errors.add(ValidationError.root(
                "MIN_DATE",
                String.format("Date must be on or after %s", minDate),
                value
            ));
        }
        
        // Check max date
        if (maxDate != null && value.isAfter(maxDate)) {
            errors.add(ValidationError.root(
                "MAX_DATE",
                String.format("Date must be on or before %s", maxDate),
                value
            ));
        }
        
        // Check custom rules
        for (CustomRule<LocalDate> rule : customRules) {
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
    public LocalDateSchema required() {
        return new LocalDateSchema(true, minDate, maxDate, customRules);
    }
    
    @Override
    public LocalDateSchema optional() {
        return new LocalDateSchema(false, minDate, maxDate, customRules);
    }
    
    @Override
    public LocalDateSchema custom(Predicate<LocalDate> rule, String message) {
        List<CustomRule<LocalDate>> newRules = new ArrayList<>(customRules);
        newRules.add(new CustomRule<>(rule, message));
        return new LocalDateSchema(required, minDate, maxDate, newRules);
    }
    
    /**
     * Sets the minimum date requirement.
     * 
     * @param minDate the minimum date (inclusive)
     * @return a new schema with min date validation
     */
    public LocalDateSchema min(LocalDate minDate) {
        return new LocalDateSchema(required, minDate, maxDate, customRules);
    }
    
    /**
     * Sets the maximum date requirement.
     * 
     * @param maxDate the maximum date (inclusive)
     * @return a new schema with max date validation
     */
    public LocalDateSchema max(LocalDate maxDate) {
        return new LocalDateSchema(required, minDate, maxDate, customRules);
    }
    
    /**
     * Sets both minimum and maximum date requirements.
     * 
     * @param minDate the minimum date (inclusive)
     * @param maxDate the maximum date (inclusive)
     * @return a new schema with date range validation
     */
    public LocalDateSchema range(LocalDate minDate, LocalDate maxDate) {
        return new LocalDateSchema(required, minDate, maxDate, customRules);
    }
    
    /**
     * Requires the date to be in the past.
     * 
     * @return a new schema with past date validation
     */
    public LocalDateSchema past() {
        return custom(date -> date.isBefore(LocalDate.now()), "Date must be in the past");
    }
    
    /**
     * Requires the date to be in the future.
     * 
     * @return a new schema with future date validation
     */
    public LocalDateSchema future() {
        return custom(date -> date.isAfter(LocalDate.now()), "Date must be in the future");
    }
}