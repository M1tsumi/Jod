package io.github.m1tsumi.jod;

import io.github.m1tsumi.jod.schema.Schema;
import io.github.m1tsumi.jod.schema.impl.ListSchema;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ListSchemaTest {

    @Test
    void shouldValidateValidList() {
        ListSchema<String> schema = Schema.list(Schema.string());
        var result = schema.validate(List.of("hello", "world"));

        assertThat(result.isValid()).isTrue();
        assertThat(result.getValue()).contains(List.of("hello", "world"));
    }

    @Test
    void shouldValidateNullWhenOptional() {
        ListSchema<String> schema = Schema.list(Schema.string());
        var result = schema.validateNullable(null);

        assertThat(result.isValid()).isTrue();
        assertThat(result.getValue()).isEmpty();
    }

    @Test
    void shouldRejectNullWhenRequired() {
        ListSchema<String> schema = Schema.list(Schema.string()).required();
        var result = schema.validateNullable(null);

        assertThat(result.isValid()).isFalse();
        assertThat(result.getErrors()).hasSize(1);
        assertThat(result.getErrors().get(0).code()).isEqualTo("REQUIRED");
    }

    @Test
    void shouldValidateMinSize() {
        ListSchema<String> schema = Schema.list(Schema.string()).minSize(2);

        var validResult = schema.validate(List.of("a", "b", "c"));
        assertThat(validResult.isValid()).isTrue();

        var invalidResult = schema.validate(List.of("a"));
        assertThat(invalidResult.isValid()).isFalse();
        assertThat(invalidResult.getErrors().get(0).code()).isEqualTo("MIN_SIZE");
    }

    @Test
    void shouldValidateMaxSize() {
        ListSchema<String> schema = Schema.list(Schema.string()).maxSize(2);

        var validResult = schema.validate(List.of("a", "b"));
        assertThat(validResult.isValid()).isTrue();

        var invalidResult = schema.validate(List.of("a", "b", "c"));
        assertThat(invalidResult.isValid()).isFalse();
        assertThat(invalidResult.getErrors().get(0).code()).isEqualTo("MAX_SIZE");
    }

    @Test
    void shouldValidateSizeRange() {
        ListSchema<String> schema = Schema.list(Schema.string()).size(1, 3);

        var validResult = schema.validate(List.of("a", "b"));
        assertThat(validResult.isValid()).isTrue();

        var tooSmallResult = schema.validate(List.of());
        assertThat(tooSmallResult.isValid()).isFalse();

        var tooLargeResult = schema.validate(List.of("a", "b", "c", "d"));
        assertThat(tooLargeResult.isValid()).isFalse();
    }

    @Test
    void shouldValidateNonEmpty() {
        ListSchema<String> schema = Schema.list(Schema.string()).nonEmpty();

        var validResult = schema.validate(List.of("a"));
        assertThat(validResult.isValid()).isTrue();

        var invalidResult = schema.validate(List.of());
        assertThat(invalidResult.isValid()).isFalse();
        assertThat(invalidResult.getErrors().get(0).code()).isEqualTo("MIN_SIZE");
    }

    @Test
    void shouldValidateElementConstraints() {
        ListSchema<String> schema = Schema.list(Schema.string().min(2).max(5));

        var validResult = schema.validate(List.of("hi", "hello"));
        assertThat(validResult.isValid()).isTrue();

        var invalidResult = schema.validate(List.of("hi", "x"));
        assertThat(invalidResult.isValid()).isFalse();
        // Should have error for second element
        assertThat(invalidResult.getErrors()).anyMatch(e ->
            e.path().equals("$.[1]") && e.code().equals("MIN_LENGTH"));
    }

    @Test
    void shouldValidateComplexObjects() {
        var objectSchema = Schema.<Map<String, Object>>object()
            .field("name", Schema.string().required())
            .field("age", Schema.integer().min(0).required())
            .builds(fields -> fields);

        ListSchema<Map<String, Object>> schema = Schema.list(objectSchema);

        var validData = List.of(
            Map.of("name", "Alice", "age", 25),
            Map.of("name", "Bob", "age", 30)
        );

        var validResult = schema.validate(validData);
        assertThat(validResult.isValid()).isTrue();

        var invalidData = List.of(
            Map.of("name", "Alice", "age", 25),
            Map.of("name", "Bob", "age", -5)  // Invalid age
        );

        var invalidResult = schema.validate(invalidData);
        assertThat(invalidResult.isValid()).isFalse();
        assertThat(invalidResult.getErrors()).anyMatch(e ->
            e.path().equals("$.[1].age") && e.code().equals("MIN_VALUE"));
    }

    @Test
    void shouldValidateCustomRule() {
        ListSchema<Integer> schema = Schema.list(Schema.integer())
            .custom(list -> list.stream().allMatch(i -> i >= 0), "All numbers must be non-negative");

        var validResult = schema.validate(List.of(1, 2, 3));
        assertThat(validResult.isValid()).isTrue();

        var invalidResult = schema.validate(List.of(1, -2, 3));
        assertThat(invalidResult.isValid()).isFalse();
        assertThat(invalidResult.getErrors().get(0).code()).isEqualTo("CUSTOM");
    }

    @Test
    void shouldHandleCollections() {
        ListSchema<String> schema = Schema.list(Schema.string().min(1));

        // Should work with ArrayList
        var arrayListResult = schema.validate(java.util.ArrayList.of("a", "b"));
        assertThat(arrayListResult.isValid()).isTrue();

        // Should work with LinkedList
        var linkedListResult = schema.validate(java.util.LinkedList.of("a", "b"));
        assertThat(linkedListResult.isValid()).isTrue();
    }
}</content>
<parameter name="filePath">/home/pepe/Desktop/Github DevWork/Software/Jod/src/test/java/io/github/m1tsumi/jod/ListSchemaTest.java