package io.github.m1tsumi.jod.schema.impl;

import io.github.m1tsumi.jod.error.ValidationError;
import io.github.m1tsumi.jod.result.ValidationResult;
import io.github.m1tsumi.jod.schema.BaseSchema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

/**
 * Schema for validating list/collection values with fluent API.
 * Validates each element against an element schema.
 */
public class ListSchema<T> implements BaseSchema<List<T>> {

    private final boolean required;
    private final BaseSchema<T> elementSchema;
    private final Integer minSize;
    private final Integer maxSize;
    private final List<CustomRule<List<T>>> customRules;

    private record CustomRule<U>(Predicate<U> predicate, String message) {}

    private ListSchema(
        boolean required,
        BaseSchema<T> elementSchema,
        Integer minSize,
        Integer maxSize,
        List<CustomRule<List<T>>> customRules
    ) {
        this.required = required;
        this.elementSchema = elementSchema;
        this.minSize = minSize;
        this.maxSize = maxSize;
        this.customRules = customRules;
    }

    public ListSchema(BaseSchema<T> elementSchema) {
        this(false, elementSchema, null, null, List.of());
    }

    @Override
    public ValidationResult<List<T>> validate(List<T> value) {
        return validateNullable(value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ValidationResult<List<T>> validateNullable(List<T> value) {
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

        // Convert collections to lists if needed
        List<T> listValue;
        if (value instanceof List) {
            listValue = value;
        } else if (value instanceof Collection) {
            listValue = new ArrayList<>((Collection<T>) value);
        } else {
            errors.add(ValidationError.root(
                "INVALID_TYPE",
                "Value must be a list or collection",
                value
            ));
            return ValidationResult.failure(errors);
        }

        // Check min size
        if (minSize != null && listValue.size() < minSize) {
            errors.add(ValidationError.root(
                "MIN_SIZE",
                String.format("List must have at least %d elements", minSize),
                value
            ));
        }

        // Check max size
        if (maxSize != null && listValue.size() > maxSize) {
            errors.add(ValidationError.root(
                "MAX_SIZE",
                String.format("List must have at most %d elements", maxSize),
                value
            ));
        }

        // Validate each element
        List<T> validatedElements = new ArrayList<>();
        for (int i = 0; i < listValue.size(); i++) {
            T element = listValue.get(i);
            ValidationResult<T> elementResult = elementSchema.validateNullable(element);

            if (elementResult.isValid()) {
                elementResult.getValue().ifPresent(validatedElements::add);
            } else {
                // Transform element errors to include list index path
                for (ValidationError error : elementResult.getErrors()) {
                    errors.add(ValidationError.at(
                        error.code(),
                        error.message(),
                        String.format("$.[%d]%s", i, error.path().substring(1)),
                        error.rejectedValue()
                    ));
                }
            }
        }

        // Check custom rules
        for (CustomRule<List<T>> rule : customRules) {
            if (!rule.predicate.test(listValue)) {
                errors.add(ValidationError.root(
                    "CUSTOM",
                    rule.message,
                    value
                ));
            }
        }

        return errors.isEmpty()
            ? ValidationResult.success(validatedElements)
            : ValidationResult.failure(errors);
    }

    @Override
    public ListSchema<T> required() {
        return new ListSchema<>(true, elementSchema, minSize, maxSize, customRules);
    }

    @Override
    public ListSchema<T> optional() {
        return new ListSchema<>(false, elementSchema, minSize, maxSize, customRules);
    }

    @Override
    public ListSchema<T> custom(Predicate<List<T>> rule, String message) {
        List<CustomRule<List<T>>> newRules = new ArrayList<>(customRules);
        newRules.add(new CustomRule<>(rule, message));
        return new ListSchema<>(required, elementSchema, minSize, maxSize, newRules);
    }

    /**
     * Sets the minimum size requirement.
     *
     * @param minSize the minimum number of elements (inclusive)
     * @return a new schema with min size validation
     */
    public ListSchema<T> minSize(int minSize) {
        return new ListSchema<>(required, elementSchema, minSize, maxSize, customRules);
    }

    /**
     * Sets the maximum size requirement.
     *
     * @param maxSize the maximum number of elements (inclusive)
     * @return a new schema with max size validation
     */
    public ListSchema<T> maxSize(int maxSize) {
        return new ListSchema<>(required, elementSchema, minSize, maxSize, customRules);
    }

    /**
     * Sets both minimum and maximum size requirements.
     *
     * @param minSize the minimum number of elements (inclusive)
     * @param maxSize the maximum number of elements (inclusive)
     * @return a new schema with size range validation
     */
    public ListSchema<T> size(int minSize, int maxSize) {
        return new ListSchema<>(required, elementSchema, minSize, maxSize, customRules);
    }

    /**
     * Requires the list to be non-empty.
     *
     * @return a new schema with non-empty validation
     */
    public ListSchema<T> nonEmpty() {
        return minSize(1);
    }
}</content>
<parameter name="filePath">/home/pepe/Desktop/Github DevWork/Software/Jod/src/main/java/io/github/m1tsumi/jod/schema/impl/ListSchema.java