package io.github.m1tsumi.jod.schema.impl;

import io.github.m1tsumi.jod.error.ValidationError;
import io.github.m1tsumi.jod.result.ValidationResult;
import io.github.m1tsumi.jod.schema.BaseSchema;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Schema for validating boolean values with fluent API.
 */
public class BooleanSchema implements BaseSchema<Boolean> {
    
    private final boolean required;
    private final Boolean expectedValue;
    private final List<CustomRule<Boolean>> customRules;
    
    private record CustomRule<T>(Predicate<T> predicate, String message) {}
    
    private BooleanSchema(
        boolean required,
        Boolean expectedValue,
        List<CustomRule<Boolean>> customRules
    ) {
        this.required = required;
        this.expectedValue = expectedValue;
        this.customRules = customRules;
    }
    
    public BooleanSchema() {
        this(false, null, List.of());
    }
    
    @Override
    public ValidationResult<Boolean> validate(Boolean value) {
        return validateNullable(value);
    }
    
    @Override
    public ValidationResult<Boolean> validateNullable(Boolean value) {
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
        
        // Check expected value
        if (expectedValue != null && !value.equals(expectedValue)) {
            errors.add(ValidationError.root(
                "EXPECTED_VALUE",
                String.format("Value must be %b", expectedValue),
                value
            ));
        }
        
        // Check custom rules
        for (CustomRule<Boolean> rule : customRules) {
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
    public BooleanSchema required() {
        return new BooleanSchema(true, expectedValue, customRules);
    }
    
    @Override
    public BooleanSchema optional() {
        return new BooleanSchema(false, expectedValue, customRules);
    }
    
    @Override
    public BooleanSchema custom(Predicate<Boolean> rule, String message) {
        List<CustomRule<Boolean>> newRules = new ArrayList<>(customRules);
        newRules.add(new CustomRule<>(rule, message));
        return new BooleanSchema(required, expectedValue, newRules);
    }
    
    /**
     * Requires the boolean value to be true.
     * 
     * @return a new schema that requires true
     */
    public BooleanSchema trueValue() {
        return new BooleanSchema(required, true, customRules);
    }
    
    /**
     * Requires the boolean value to be false.
     * 
     * @return a new schema that requires false
     */
    public BooleanSchema falseValue() {
        return new BooleanSchema(required, false, customRules);
    }
}
