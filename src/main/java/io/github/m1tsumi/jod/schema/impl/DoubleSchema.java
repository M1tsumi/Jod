package io.github.m1tsumi.jod.schema.impl;

import io.github.m1tsumi.jod.error.ValidationError;
import io.github.m1tsumi.jod.result.ValidationResult;
import io.github.m1tsumi.jod.schema.BaseSchema;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Schema for validating double values with fluent API.
 */
public class DoubleSchema implements BaseSchema<Double> {

    private final boolean required;
    private final Double minValue;
    private final Double maxValue;
    private final boolean positive;
    private final boolean nonNegative;
    private final boolean finite;
    private final List<CustomRule<Double>> customRules;

    private record CustomRule<T>(Predicate<T> predicate, String message) {}

    private DoubleSchema(
        boolean required,
        Double minValue,
        Double maxValue,
        boolean positive,
        boolean nonNegative,
        boolean finite,
        List<CustomRule<Double>> customRules
    ) {
        this.required = required;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.positive = positive;
        this.nonNegative = nonNegative;
        this.finite = finite;
        this.customRules = customRules;
    }

    public DoubleSchema() {
        this(false, null, null, false, false, false, List.of());
    }

    @Override
    public ValidationResult<Double> validate(Double value) {
        return validateNullable(value);
    }

    @Override
    public ValidationResult<Double> validateNullable(Double value) {
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

        // Check finite
        if (finite && !Double.isFinite(value)) {
            errors.add(ValidationError.root(
                "FINITE",
                "Value must be finite (not NaN or infinite)",
                value
            ));
        }

        // Check min value
        if (minValue != null && value < minValue) {
            errors.add(ValidationError.root(
                "MIN_VALUE",
                String.format("Value must be at least %f", minValue),
                value
            ));
        }

        // Check max value
        if (maxValue != null && value > maxValue) {
            errors.add(ValidationError.root(
                "MAX_VALUE",
                String.format("Value must be at most %f", maxValue),
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
        for (CustomRule<Double> rule : customRules) {
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
    public DoubleSchema required() {
        return new DoubleSchema(true, minValue, maxValue, positive, nonNegative, finite, customRules);
    }

    @Override
    public DoubleSchema optional() {
        return new DoubleSchema(false, minValue, maxValue, positive, nonNegative, finite, customRules);
    }

    @Override
    public DoubleSchema custom(Predicate<Double> rule, String message) {
        List<CustomRule<Double>> newRules = new ArrayList<>(customRules);
        newRules.add(new CustomRule<>(rule, message));
        return new DoubleSchema(required, minValue, maxValue, positive, nonNegative, finite, newRules);
    }

    /**
     * Sets the minimum value requirement.
     *
     * @param minValue the minimum value (inclusive)
     * @return a new schema with min value validation
     */
    public DoubleSchema min(double minValue) {
        return new DoubleSchema(required, minValue, maxValue, positive, nonNegative, finite, customRules);
    }

    /**
     * Sets the maximum value requirement.
     *
     * @param maxValue the maximum value (inclusive)
     * @return a new schema with max value validation
     */
    public DoubleSchema max(double maxValue) {
        return new DoubleSchema(required, minValue, maxValue, positive, nonNegative, finite, customRules);
    }

    /**
     * Sets both minimum and maximum value requirements.
     *
     * @param minValue the minimum value (inclusive)
     * @param maxValue the maximum value (inclusive)
     * @return a new schema with range validation
     */
    public DoubleSchema range(double minValue, double maxValue) {
        return new DoubleSchema(required, minValue, maxValue, positive, nonNegative, finite, customRules);
    }

    /**
     * Requires the value to be positive (> 0).
     *
     * @return a new schema with positive validation
     */
    public DoubleSchema positive() {
        return new DoubleSchema(required, minValue, maxValue, true, nonNegative, finite, customRules);
    }

    /**
     * Requires the value to be non-negative (>= 0).
     *
     * @return a new schema with non-negative validation
     */
    public DoubleSchema nonNegative() {
        return new DoubleSchema(required, minValue, maxValue, positive, true, finite, customRules);
    }

    /**
     * Requires the value to be finite (not NaN or infinite).
     *
     * @return a new schema with finite validation
     */
    public DoubleSchema finite() {
        return new DoubleSchema(required, minValue, maxValue, positive, nonNegative, true, customRules);
    }
}</content>
<parameter name="filePath">/home/pepe/Desktop/Github DevWork/Software/Jod/src/main/java/io/github/m1tsumi/jod/schema/impl/DoubleSchema.java