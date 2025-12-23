package io.github.m1tsumi.jod.error;

import java.util.List;
import java.util.Objects;

/**
 * Represents a validation error with detailed information about what went wrong.
 * Uses a record for immutability and includes path information for nested validations.
 */
public record ValidationError(
    String code,
    String message,
    String path,
    Object rejectedValue
) {
    
    public ValidationError {
        Objects.requireNonNull(code, "Error code cannot be null");
        Objects.requireNonNull(message, "Error message cannot be null");
        Objects.requireNonNull(path, "Error path cannot be null");
    }
    
    /**
     * Creates a validation error at the root level.
     * 
     * @param code the error code
     * @param message the error message
     * @param rejectedValue the value that was rejected
     * @return a new ValidationError
     */
    public static ValidationError root(String code, String message, Object rejectedValue) {
        return new ValidationError(code, message, "$", rejectedValue);
    }
    
    /**
     * Creates a validation error at a specific path.
     * 
     * @param code the error code
     * @param message the error message
     * @param path the path to the error (e.g., "$.user.email")
     * @param rejectedValue the value that was rejected
     * @return a new ValidationError
     */
    public static ValidationError at(String code, String message, String path, Object rejectedValue) {
        return new ValidationError(code, message, path, rejectedValue);
    }
    
    /**
     * Creates a child error by appending to the current path.
     * 
     * @param field the field to append to the path
     * @param childError the error that occurred on the child field
     * @return a new ValidationError with updated path
     */
    public static ValidationError child(String field, ValidationError childError) {
        String newPath = childError.path.equals("$") 
            ? "$." + field 
            : childError.path.replace("$", "$." + field);
        return new ValidationError(
            childError.code(),
            childError.message(),
            newPath,
            childError.rejectedValue()
        );
    }
    
    /**
     * Combines multiple errors into a list, handling nulls.
     * 
     * @param errors the errors to combine
     * @return a list of validation errors
     */
    public static List<ValidationError> combine(List<ValidationError>... errors) {
        return List.of(errors).stream()
            .filter(Objects::nonNull)
            .flatMap(List::stream)
            .toList();
    }
}
