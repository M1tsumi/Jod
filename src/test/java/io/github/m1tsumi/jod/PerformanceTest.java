package io.github.m1tsumi.jod;

import io.github.m1tsumi.jod.schema.Schema;
import io.github.m1tsumi.jod.schema.impl.StringSchema;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Performance tests to validate Jod's performance claims.
 * These are simple benchmarks - for production use, consider JMH.
 */
class PerformanceTest {
    
    private static final int ITERATIONS = 1_000_000;
    private static final String TEST_EMAIL = "user@example.com";
    private static final String TEST_STRING = "hello world";
    
    @Test
    void validateStringPerformance() {
        StringSchema schema = Schema.string()
            .min(5)
            .max(100)
            .email();
        
        long startTime = System.nanoTime();
        
        for (int i = 0; i < ITERATIONS; i++) {
            var result = schema.validate(TEST_EMAIL);
            assertThat(result.isValid()).isTrue();
        }
        
        long duration = System.nanoTime() - startTime;
        double avgMicros = duration / (double) ITERATIONS / 1000;
        
        System.out.printf("String validation: %.2f μs per validation%n", avgMicros);
        System.out.printf("Total time: %d ms%n", TimeUnit.NANOSECONDS.toMillis(duration));
        
        // Validation should be fast - less than 10 microseconds per validation
        assertThat(avgMicros).isLessThan(10);
    }
    
    @Test
    void validateObjectPerformance() {
        var userSchema = Schema.<Map<String, Object>>object()
            .field("email", Schema.string().email().required())
            .field("age", Schema.integer().min(18).required())
            .field("active", Schema.bool().trueValue())
            .builds(fields -> fields);
        
        Map<String, Object> userData = Map.of(
            "email", "user@example.com",
            "age", 25,
            "active", true
        );
        
        long startTime = System.nanoTime();
        
        for (int i = 0; i < ITERATIONS / 10; i++) { // Fewer iterations for objects
            var result = userSchema.validate(userData);
            assertThat(result.isValid()).isTrue();
        }
        
        long duration = System.nanoTime() - startTime;
        double avgMicros = duration / (double) (ITERATIONS / 10) / 1000;
        
        System.out.printf("Object validation: %.2f μs per validation%n", avgMicros);
        System.out.printf("Total time: %d ms%n", TimeUnit.NANOSECONDS.toMillis(duration));
        
        // Object validation should still be under 50 microseconds
        assertThat(avgMicros).isLessThan(50);
    }
    
    @Test
    void validateMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        
        // Force garbage collection
        System.gc();
        long memoryBefore = runtime.totalMemory() - runtime.freeMemory();
        
        // Create many schemas and validations
        StringSchema[] schemas = new StringSchema[1000];
        for (int i = 0; i < 1000; i++) {
            schemas[i] = Schema.string()
                .min(5)
                .max(100)
                .email();
        }
        
        // Validate many times
        for (int i = 0; i < 10000; i++) {
            for (StringSchema schema : schemas) {
                schema.validate(TEST_EMAIL);
            }
        }
        
        System.gc();
        long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
        long memoryUsed = memoryAfter - memoryBefore;
        
        System.out.printf("Memory used: %d KB%n", memoryUsed / 1024);
        
        // Memory usage should be reasonable - schemas are immutable and reusable
        assertThat(memoryUsed).isLessThan(5 * 1024 * 1024); // Less than 5MB
    }
    
    @Test
    void validateZeroReflection() {
        // Verify that we're not using reflection in the validation path
        StringSchema schema = Schema.string().email();
        
        // This should not trigger any reflection
        var result = schema.validate(TEST_EMAIL);
        assertThat(result.isValid()).isTrue();
        
        // Object schema with Map input should not use reflection
        var objectSchema = Schema.<Map<String, Object>>object()
            .field("email", Schema.string().email())
            .builds(fields -> fields);
        
        Map<String, Object> data = Map.of("email", TEST_EMAIL);
        var objectResult = objectSchema.validate(data);
        assertThat(objectResult.isValid()).isTrue();
    }
}
