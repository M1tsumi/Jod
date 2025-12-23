package io.github.m1tsumi.jod.schema;

import io.github.m1tsumi.jod.result.ValidationResult;
import io.github.m1tsumi.jod.schema.impl.BooleanSchema;
import io.github.m1tsumi.jod.schema.impl.IntegerSchema;
import io.github.m1tsumi.jod.schema.impl.ObjectSchema;
import io.github.m1tsumi.jod.schema.impl.StringSchema;

/**
 * Main entry point for creating validation schemas.
 * Provides factory methods for creating type-specific schema builders.
 */
public final class Schema {
    
    private Schema() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Creates a schema for string values.
     * 
     * @return a string schema builder
     */
    public static StringSchema string() {
        return new StringSchema();
    }
    
    /**
     * Creates a schema for integer values.
     * 
     * @return an integer schema builder
     */
    public static IntegerSchema integer() {
        return new IntegerSchema();
    }
    
    /**
     * Creates a schema for boolean values.
     * 
     * @return a boolean schema builder
     */
    public static BooleanSchema bool() {
        return new BooleanSchema();
    }
    
    /**
     * Creates a builder for object schemas.
     * 
     * @param <T> the type of the object
     * @return an object schema builder
     */
    public static <T> ObjectSchema.Builder<T> object() {
        return new ObjectSchema.Builder<>();
    }
}
