package io.github.m1tsumi.jod.schema;

import io.github.m1tsumi.jod.result.ValidationResult;
import io.github.m1tsumi.jod.schema.impl.BigIntegerSchema;
import io.github.m1tsumi.jod.schema.impl.BooleanSchema;
import io.github.m1tsumi.jod.schema.impl.DoubleSchema;
import io.github.m1tsumi.jod.schema.impl.IntegerSchema;
import io.github.m1tsumi.jod.schema.impl.ListSchema;
import io.github.m1tsumi.jod.schema.impl.LocalDateSchema;
import io.github.m1tsumi.jod.schema.impl.LocalDateTimeSchema;
import io.github.m1tsumi.jod.schema.impl.LocalTimeSchema;
import io.github.m1tsumi.jod.schema.impl.LongSchema;
import io.github.m1tsumi.jod.schema.impl.ObjectSchema;
import io.github.m1tsumi.jod.schema.impl.StringSchema;
import io.github.m1tsumi.jod.schema.impl.UnionSchema;

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
     * Creates a schema for long values.
     * 
     * @return a long schema builder
     */
    public static LongSchema longValue() {
        return new LongSchema();
    }
    
    /**
     * Creates a schema for double values.
     * 
     * @return a double schema builder
     */
    public static DoubleSchema doubleValue() {
        return new DoubleSchema();
    }
    
    /**
     * Creates a schema for LocalDate values.
     * 
     * @return a LocalDate schema builder
     */
    public static LocalDateSchema localDate() {
        return new LocalDateSchema();
    }
    
    /**
     * Creates a schema for LocalDateTime values.
     * 
     * @return a LocalDateTime schema builder
     */
    public static LocalDateTimeSchema localDateTime() {
        return new LocalDateTimeSchema();
    }
    
    /**
     * Creates a schema for LocalTime values.
     * 
     * @return a LocalTime schema builder
     */
    public static LocalTimeSchema localTime() {
        return new LocalTimeSchema();
    }
    
    /**
     * Creates a schema for BigInteger values.
     * 
     * @return a BigInteger schema builder
     */
    public static BigIntegerSchema bigInteger() {
        return new BigIntegerSchema();
    }
    
    /**
     * Creates a schema for list values.
     * 
     * @param <T> the element type
     * @param elementSchema the schema for list elements
     * @return a list schema builder
     */
    public static <T> ListSchema<T> list(BaseSchema<T> elementSchema) {
        return new ListSchema<>(elementSchema);
    }
    
    /**
     * Creates a union schema that validates against one of multiple schemas.
     * 
     * @param <T> the common type of all schemas
     * @param schemas the schemas to try in order
     * @return a union schema
     */
    @SafeVarargs
    public static <T> UnionSchema<T> union(BaseSchema<? extends T>... schemas) {
        return new UnionSchema<>(java.util.List.of(schemas));
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
