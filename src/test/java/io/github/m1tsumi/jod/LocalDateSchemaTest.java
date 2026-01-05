package io.github.m1tsumi.jod;

import io.github.m1tsumi.jod.result.ValidationResult;
import io.github.m1tsumi.jod.schema.Schema;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class LocalDateSchemaTest {

    @Test
    void shouldValidateValidLocalDate() {
        var schema = Schema.localDate();
        var result = schema.validate(LocalDate.of(2023, 12, 25));
        
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue()).isEqualTo(LocalDate.of(2023, 12, 25));
    }

    @Test
    void shouldFailValidationForNullWhenRequired() {
        var schema = Schema.localDate().required();
        var result = schema.validate(null);
        
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getErrors()).hasSize(1);
        assertThat(result.getErrors().get(0).message()).isEqualTo("Value is required");
    }

    @Test
    void shouldAllowNullWhenOptional() {
        var schema = Schema.localDate().optional();
        var result = schema.validate(null);
        
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue()).isNull();
    }

    @Test
    void shouldValidateMinDate() {
        var schema = Schema.localDate().min(LocalDate.of(2023, 1, 1));
        
        assertThat(schema.validate(LocalDate.of(2023, 6, 15)).isSuccess()).isTrue();
        assertThat(schema.validate(LocalDate.of(2022, 12, 31)).isFailure()).isTrue();
    }

    @Test
    void shouldValidateMaxDate() {
        var schema = Schema.localDate().max(LocalDate.of(2023, 12, 31));
        
        assertThat(schema.validate(LocalDate.of(2023, 6, 15)).isSuccess()).isTrue();
        assertThat(schema.validate(LocalDate.of(2024, 1, 1)).isFailure()).isTrue();
    }

    @Test
    void shouldValidateDateRange() {
        var schema = Schema.localDate().range(LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 31));
        
        assertThat(schema.validate(LocalDate.of(2023, 6, 15)).isSuccess()).isTrue();
        assertThat(schema.validate(LocalDate.of(2022, 12, 31)).isFailure()).isTrue();
        assertThat(schema.validate(LocalDate.of(2024, 1, 1)).isFailure()).isTrue();
    }

    @Test
    void shouldValidatePastDate() {
        var schema = Schema.localDate().past();
        var pastDate = LocalDate.now().minusDays(1);
        var futureDate = LocalDate.now().plusDays(1);
        
        assertThat(schema.validate(pastDate).isSuccess()).isTrue();
        assertThat(schema.validate(futureDate).isFailure()).isTrue();
    }

    @Test
    void shouldValidateFutureDate() {
        var schema = Schema.localDate().future();
        var pastDate = LocalDate.now().minusDays(1);
        var futureDate = LocalDate.now().plusDays(1);
        
        assertThat(schema.validate(futureDate).isSuccess()).isTrue();
        assertThat(schema.validate(pastDate).isFailure()).isTrue();
    }

    @Test
    void shouldValidateWithCustomRule() {
        var schema = Schema.localDate().custom(date -> date.getDayOfWeek().name().equals("MONDAY"), "Must be a Monday");
        
        assertThat(schema.validate(LocalDate.of(2023, 12, 25)).isFailure()).isTrue(); // Christmas is not Monday
        // Assuming 2023-12-25 is not Monday, let's use a known Monday
        assertThat(schema.validate(LocalDate.of(2023, 12, 18)).isSuccess()).isTrue(); // 2023-12-18 is Monday
    }
}