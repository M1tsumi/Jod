package io.github.m1tsumi.jod.schema.impl;

import io.github.m1tsumi.jod.error.ValidationError;
import io.github.m1tsumi.jod.result.ValidationResult;
import io.github.m1tsumi.jod.schema.BaseSchema;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Schema for validating one of multiple possible schemas (union types).
 * Tries each schema in order until one succeeds.
 */
public class UnionSchema<T> implements BaseSchema<T> {

    private final boolean required;
    private final List<BaseSchema<? extends T>> schemas;
    private final Function<T, T> transform;

    private UnionSchema(
        boolean required,
        List<BaseSchema<? extends T>> schemas,
        Function<T, T> transform
    ) {
        this.required = required;
        this.schemas = List.copyOf(schemas);
        this.transform = transform;
    }

    public UnionSchema(List<BaseSchema<? extends T>> schemas) {
        this(false, schemas, Function.identity());
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

        // Try each schema until one succeeds
        for (BaseSchema<? extends T> schema : schemas) {
            ValidationResult<? extends T> result = schema.validateNullable(value);
            if (result.isValid()) {
                // Apply transform if present
                T validatedValue = result.getValue().orElse(null);
                if (transform != null && validatedValue != null) {
                    validatedValue = transform.apply(validatedValue);
                }
                return ValidationResult.success(validatedValue);
            }
        }

        // All schemas failed - collect all errors
        for (BaseSchema<? extends T> schema : schemas) {
            ValidationResult<? extends T> result = schema.validateNullable(value);
            if (!result.isValid()) {
                errors.addAll(result.getErrors());
            }
        }

        errors.add(0, ValidationError.root(
            "UNION_FAILED",
            "Value did not match any of the allowed schemas",
            value
        ));

        return ValidationResult.failure(errors);
    }

    @Override
    public UnionSchema<T> required() {
        return new UnionSchema<>(true, schemas, transform);
    }

    @Override
    public UnionSchema<T> optional() {
        return new UnionSchema<>(false, schemas, transform);
    }

    @Override
    public UnionSchema<T> custom(java.util.function.Predicate<T> rule, String message) {
        // For unions, custom validation would be applied after union validation
        return this;
    }

    /**
     * Adds a transformation function to be applied to validated values.
     *
     * @param transform the transformation function
     * @return a new schema with transformation
     */
    public UnionSchema<T> transform(Function<T, T> transform) {
        return new UnionSchema<>(required, schemas, transform);
    }
}</content>
<parameter name="filePath">/home/pepe/Desktop/Github DevWork/Software/Jod/src/main/java/io/github/m1tsumi/jod/schema/impl/UnionSchema.java