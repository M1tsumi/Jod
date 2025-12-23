package io.github.m1tsumi.jod;

import io.github.m1tsumi.jod.schema.Schema;
import io.github.m1tsumi.jod.schema.impl.ObjectSchema;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ObjectSchemaTest {
    
    record User(String email, int age, boolean active) {}
    
    @Test
    void shouldValidateValidObject() {
        ObjectSchema<User> schema = Schema.<User>object()
            .field("email", Schema.string().email().required())
            .field("age", Schema.integer().min(18).required())
            .field("active", Schema.bool().trueValue())
            .builds(fields -> new User(
                (String) fields.get("email"),
                (Integer) fields.get("age"),
                (Boolean) fields.get("active")
            ));
        
        Map<String, Object> validUser = Map.of(
            "email", "test@example.com",
            "age", 25,
            "active", true
        );
        
        var result = schema.validate(validUser);
        assertThat(result.isValid()).isTrue();
        assertThat(result.getValue()).isPresent();
        assertThat(result.getValue().get().email()).isEqualTo("test@example.com");
        assertThat(result.getValue().get().age()).isEqualTo(25);
        assertThat(result.getValue().get().active()).isTrue();
    }
    
    @Test
    void shouldValidateNullWhenOptional() {
        ObjectSchema<User> schema = Schema.<User>object()
            .field("email", Schema.string().email())
            .builds(fields -> new User(
                (String) fields.get("email"),
                0,
                false
            ));
        
        var result = schema.validateNullable(null);
        assertThat(result.isValid()).isTrue();
        assertThat(result.getValue()).isEmpty();
    }
    
    @Test
    void shouldRejectNullWhenRequired() {
        ObjectSchema<User> schema = Schema.<User>object()
            .required()
            .field("email", Schema.string().email())
            .builds(fields -> new User(
                (String) fields.get("email"),
                0,
                false
            ));
        
        var result = schema.validateNullable(null);
        assertThat(result.isValid()).isFalse();
        assertThat(result.getErrors()).hasSize(1);
        assertThat(result.getErrors().get(0).code()).isEqualTo("REQUIRED");
    }
    
    @Test
    void shouldRejectInvalidFields() {
        ObjectSchema<User> schema = Schema.<User>object()
            .field("email", Schema.string().email().required())
            .field("age", Schema.integer().min(18).required())
            .field("active", Schema.bool().trueValue())
            .builds(fields -> new User(
                (String) fields.get("email"),
                (Integer) fields.get("age"),
                (Boolean) fields.get("active")
            ));
        
        Map<String, Object> invalidUser = Map.of(
            "email", "invalid-email",
            "age", 15,
            "active", false
        );
        
        var result = schema.validate(invalidUser);
        assertThat(result.isValid()).isFalse();
        assertThat(result.getErrors()).hasSize(3);
        
        // Check that errors have proper paths
        assertThat(result.getErrors()).anyMatch(e -> 
            e.code().equals("EMAIL") && e.path().equals("$.email")
        );
        assertThat(result.getErrors()).anyMatch(e -> 
            e.code().equals("MIN_VALUE") && e.path().equals("$.age")
        );
        assertThat(result.getErrors()).anyMatch(e -> 
            e.code().equals("EXPECTED_VALUE") && e.path().equals("$.active")
        );
    }
    
    @Test
    void shouldValidatePartialObject() {
        ObjectSchema<User> schema = Schema.<User>object()
            .field("email", Schema.string().email())
            .field("age", Schema.integer().min(18))
            .builds(fields -> new User(
                (String) fields.getOrDefault("email", "default@example.com"),
                (Integer) fields.getOrDefault("age", 18),
                false
            ));
        
        Map<String, Object> partialUser = Map.of(
            "email", "test@example.com"
        );
        
        var result = schema.validate(partialUser);
        assertThat(result.isValid()).isTrue();
        assertThat(result.getValue().get().email()).isEqualTo("test@example.com");
        assertThat(result.getValue().get().age()).isEqualTo(18);
    }
    
    @Test
    void shouldHandleMissingFields() {
        ObjectSchema<User> schema = Schema.<User>object()
            .field("email", Schema.string().email().required())
            .field("age", Schema.integer().min(18).required())
            .builds(fields -> new User(
                (String) fields.get("email"),
                (Integer) fields.get("age"),
                false
            ));
        
        Map<String, Object> userWithoutAge = Map.of(
            "email", "test@example.com"
        );
        
        var result = schema.validate(userWithoutAge);
        assertThat(result.isValid()).isFalse();
        // Missing required field should fail validation
        assertThat(result.getErrors()).anyMatch(e -> 
            e.code().equals("REQUIRED") && e.path().equals("$.age")
        );
    }
}
