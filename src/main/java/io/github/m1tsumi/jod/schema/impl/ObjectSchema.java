package io.github.m1tsumi.jod.schema.impl;

import io.github.m1tsumi.jod.error.ValidationError;
import io.github.m1tsumi.jod.result.ValidationResult;
import io.github.m1tsumi.jod.schema.BaseSchema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Schema for validating object values with field-level validation.
 * Uses a builder pattern for constructing object schemas.
 */
public class ObjectSchema<T> implements BaseSchema<T> {
    
    private final boolean required;
    private final Map<String, FieldSchema<T, ?>> fields;
    private final Function<Map<String, Object>, T> constructor;
    
    private ObjectSchema(
        boolean required,
        Map<String, FieldSchema<T, ?>> fields,
        Function<Map<String, Object>, T> constructor
    ) {
        this.required = required;
        this.fields = Map.copyOf(fields);
        this.constructor = constructor;
    }
    
    private ObjectSchema(Builder<T> builder) {
        this.required = builder.required;
        this.fields = Map.copyOf(builder.fields);
        this.constructor = builder.constructor;
    }
    
    @Override
    public ValidationResult<T> validate(T value) {
        return validateNullable(value);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public ValidationResult<T> validateNullable(T value) {
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
        
        // Extract field values using reflection or map conversion
        Map<String, Object> fieldValues = extractFieldValues(value);
        
        // Validate each field
        Map<String, Object> validFieldValues = new HashMap<>();
        for (Map.Entry<String, FieldSchema<T, ?>> entry : fields.entrySet()) {
            String fieldName = entry.getKey();
            FieldSchema<T, ?> fieldSchema = entry.getValue();
            
            Object fieldValue = fieldValues.get(fieldName);
            ValidationResult<?> fieldResult = fieldSchema.schema.validateNullable(fieldValue);
            
            if (fieldResult.isValid()) {
                fieldResult.getValue().ifPresent(v -> validFieldValues.put(fieldName, v));
            } else {
                // Transform errors to include field path
                for (ValidationError error : fieldResult.getErrors()) {
                    errors.add(ValidationError.child(fieldName, error));
                }
            }
        }
        
        if (!errors.isEmpty()) {
            return ValidationResult.failure(errors);
        }
        
        // Construct validated object
        try {
            T validatedValue = constructor.apply(validFieldValues);
            return ValidationResult.success(validatedValue);
        } catch (Exception e) {
            errors.add(ValidationError.root(
                "CONSTRUCTION_FAILED",
                "Failed to construct validated object: " + e.getMessage(),
                value
            ));
            return ValidationResult.failure(errors);
        }
    }
    
    @Override
    public ObjectSchema<T> required() {
        return new ObjectSchema<>(true, fields, constructor);
    }
    
    @Override
    public ObjectSchema<T> optional() {
        return new ObjectSchema<>(false, fields, constructor);
    }
    
    @Override
    public ObjectSchema<T> custom(java.util.function.Predicate<T> rule, String message) {
        // For objects, custom validation would be applied after field validation
        // This is a simplified implementation
        return this;
    }
    
    /**
     * Extracts field values from an object.
     * Only supports Map inputs to maintain zero reflection.
     * Records and other objects should be converted to Map before validation.
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> extractFieldValues(T value) {
        if (value instanceof Map) {
            return (Map<String, Object>) value;
        }
        
        throw new IllegalArgumentException(
            "ObjectSchema only supports Map inputs for zero reflection validation. " +
            "Please convert your object to Map before validation."
        );
    }
    
    /**
     * Builder for creating object schemas.
     */
    public static class Builder<T> {
        private boolean required = false;
        private final Map<String, FieldSchema<T, ?>> fields = new HashMap<>();
        private Function<Map<String, Object>, T> constructor;
        
        /**
         * Adds a field to the object schema.
         * 
         * @param name the field name
         * @param schema the schema for validating the field
         * @param <U> the field type
         * @return this builder for chaining
         */
        public <U> Builder<T> field(String name, BaseSchema<U> schema) {
            fields.put(name, new FieldSchema<>(name, schema));
            return this;
        }
        
        /**
         * Marks the object as required.
         * 
         * @return this builder for chaining
         */
        public Builder<T> required() {
            this.required = true;
            return this;
        }
        
        /**
         * Sets the constructor for building validated objects.
         * 
         * @param constructor a function that takes a map of field values and returns the object
         * @return this builder for chaining
         */
        public Builder<T> builds(Function<Map<String, Object>, T> constructor) {
            this.constructor = constructor;
            return this;
        }
        
        /**
         * Builds the object schema.
         * 
         * @return the constructed object schema
         */
        public ObjectSchema<T> build() {
            if (constructor == null) {
                throw new IllegalStateException("Constructor must be specified using builds()");
            }
            return new ObjectSchema<>(this);
        }
    }
    
    /**
     * Represents a field schema with its name and validation rules.
     */
    private record FieldSchema<T, U>(String name, BaseSchema<U> schema) {}
}
