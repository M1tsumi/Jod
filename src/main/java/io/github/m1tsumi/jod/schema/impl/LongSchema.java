package io.github.m1tsumi.jod.schema.impl;

import io.github.m1tsumi.jod.error.ValidationError;
import io.github.m1tsumi.jod.result.ValidationResult;
import io.github.m1tsumi.jod.schema.BaseSchema;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Schema for validating long values with fluent API.
 */
public class LongSchema implements BaseSchema<Long> {

    private final boolean required;
    private final Long minValue;
    private final Long maxValue;
    private final boolean positive;
    private final boolean nonNegative;
    private final List<CustomRule<Long>> customRules;

    private record CustomRule<T>(Predicate<T> predicate, String message) {}

    private LongSchema(
        boolean required,
        Long minValue,
        Long maxValue,
        boolean positive,
        boolean nonNegative,
        List<CustomRule<Long>> customRules
    ) {
        this.required = required;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.positive = positive;
        this.nonNegative = nonNegative;
        this.customRules = customRules;
    }

    public LongSchema() {
        this(false, null, null, false, false, List.of());
    }

    @Override
    public ValidationResult<Long> validate(Long value) {
        return validateNullable(value);
    }

    @Override
    public ValidationResult<Long> validateNullable(Long value) {
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

        // Check min value
        if (minValue != null && value < minValue) {
            errors.add(ValidationError.root(
                "MIN_VALUE",
                String.format("Value must be at least %d", minValue),
                value
            ));
        }

        // Check max value
        if (maxValue != null && value > maxValue) {
            errors.add(ValidationError.root(
                "MAX_VALUE",
                String.format("Value must be at most %d", maxValue),
                value
            ));
        }

        // Check positive
        if (positive && value <= 0) {
            errors.add(ValidationError.root(
                "POSITIVE",
                "Value must be positive",
                value
            ));
        }

        // Check non-negative
        if (nonNegative && value < 0) {
            errors.add(ValidationError.root(
                "NON_NEGATIVE",
                "Value must be non-negative",
                value
            ));
        }

        // Check custom rules
        for (CustomRule<Long> rule : customRules) {
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
    public LongSchema required() {
        return new LongSchema(true, minValue, maxValue, positive, nonNegative, customRules);
    }

    @Override
    public LongSchema optional() {
        return new LongSchema(false, minValue, maxValue, positive, nonNegative, customRules);
    }

    @Override
    public LongSchema custom(Predicate<Long> rule, String message) {
        List<CustomRule<Long>> newRules = new ArrayList<>(customRules);
        newRules.add(new CustomRule<>(rule, message));
        return new LongSchema(required, minValue, maxValue, positive, nonNegative, newRules);
    }

    /**
     * Sets the minimum value requirement.
     *
     * @param minValue the minimum value (inclusive)
     * @return a new schema with min value validation
     */
    public LongSchema min(long minValue) {
        return new LongSchema(required, minValue, maxValue, positive, nonNegative, customRules);
    }

    /**
     * Sets the maximum value requirement.
     *
     * @param maxValue the maximum value (inclusive)
     * @return a new schema with max value validation
     */
    public LongSchema max(long maxValue) {
        return new LongSchema(required, minValue, maxValue, positive, nonNegative, customRules);
    }

    /**
     * Sets both minimum and maximum value requirements.
     *
     * @param minValue the minimum value (inclusive)
     * @param maxValue the maximum value (inclusive)
     * @return a new schema with range validation
     */
    public LongSchema range(long minValue, long maxValue) {
        return new LongSchema(required, minValue, maxValue, positive, nonNegative, customRules);
    }

    /**
     * Requires the value to be positive (> 0).
     *
     * @return a new schema with positive validation
     */
    public LongSchema positive() {
        return new LongSchema(required, minValue, maxValue, true, nonNegative, customRules);
    }

    /**
     * Requires the value to be non-negative (>= 0).
     *
     * @return a new schema with non-negative validation
     */
    public LongSchema nonNegative() {
        return new LongSchema(required, minValue, maxValue, positive, true, customRules);
    }
}</content>
<parameter name="filePath">/home/pepe/Desktop/Github DevWork/Software/Jod/src/main/java/io/github/m1tsumi/jod/schema/impl/LongSchema.java