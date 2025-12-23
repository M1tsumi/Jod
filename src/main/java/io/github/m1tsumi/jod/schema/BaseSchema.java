package io.github.m1tsumi.jod.schema;

import io.github.m1tsumi.jod.result.ValidationResult;

/**
 * Base interface for all validation schemas.
 * Defines the contract for validating values of type T.
 * 
 * @param <T> the type of values this schema validates
 */
public interface BaseSchema<T> {
    
    /**
     * Validates the given value against this schema.
     * 
     * @param value the value to validate
     * @return the result of validation
     */
    ValidationResult<T> validate(T value);
    
    /**
     * Validates the given value, accepting null values.
     * 
     * @param value the value to validate (may be null)
     * @return the result of validation
     */
    ValidationResult<T> validateNullable(T value);
    
    /**
     * Marks this schema as required (non-null).
     * 
     * @return a schema that requires non-null values
     */
    BaseSchema<T> required();
    
    /**
     * Marks this schema as optional (allows null values).
     * 
     * @return a schema that allows null values
     */
    BaseSchema<T> optional();
    
    /**
     * Adds a custom validation rule.
     * 
     * @param rule the validation rule to apply
     * @param message the error message if validation fails
     * @return a schema with the additional rule
     */
    BaseSchema<T> custom(java.util.function.Predicate<T> rule, String message);
}
