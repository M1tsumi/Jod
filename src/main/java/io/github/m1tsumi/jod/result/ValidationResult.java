package io.github.m1tsumi.jod.result;

import io.github.m1tsumi.jod.error.ValidationError;

import java.util.List;
import java.util.Optional;

/**
 * A sealed interface representing the result of a validation operation.
 * Uses pattern matching for exhaustive handling of success and failure cases.
 * 
 * @param <T> the type of the validated value
 */
public sealed interface ValidationResult<T> permits ValidationResult.Success<T>, ValidationResult.Failure {
    
    /**
     * Creates a successful validation result.
     * 
     * @param value the validated value
     * @param <T> the type of the value
     * @return a success result containing the value
     */
    static <T> ValidationResult<T> success(T value) {
        return new Success<>(value);
    }
    
    /**
     * Creates a failed validation result.
     * 
     * @param errors the validation errors that occurred
     * @param <T> the type that was being validated
     * @return a failure result containing the errors
     */
    static <T> ValidationResult<T> failure(List<ValidationError> errors) {
        return new Failure<>(errors);
    }
    
    /**
     * Checks if the validation was successful.
     * 
     * @return true if successful, false if failed
     */
    boolean isValid();
    
    /**
     * Returns the validated value if successful.
     * 
     * @return an Optional containing the value if successful, empty otherwise
     */
    Optional<T> getValue();
    
    /**
     * Returns the validation errors if failed.
     * 
     * @return a list of validation errors, empty if successful
     */
    List<ValidationError> getErrors();
    
    /**
     * Represents a successful validation result.
     * 
     * @param <T> the type of the validated value
     */
    record Success<T>(T value) implements ValidationResult<T> {
        
        @Override
        public boolean isValid() {
            return true;
        }
        
        @Override
        public Optional<T> getValue() {
            return Optional.of(value);
        }
        
        @Override
        public List<ValidationError> getErrors() {
            return List.of();
        }
    }
    
    /**
     * Represents a failed validation result.
     * 
     * @param <T> the type that was being validated
     */
    record Failure<T>(List<ValidationError> errors) implements ValidationResult<T> {
        
        public Failure {
            errors = List.copyOf(errors);
        }
        
        @Override
        public boolean isValid() {
            return false;
        }
        
        @Override
        public Optional<T> getValue() {
            return Optional.empty();
        }
        
        @Override
        public List<ValidationError> getErrors() {
            return errors;
        }
    }
}
