<p align="center">
  <img src="logo.svg" alt="Jod Logo" width="200" height="200">
</p>

<h1 align="center">Jod</h1>

<p align="center">
  Modern, type-safe validation for Java 17+. Inspired by <a href="https://github.com/colinhacks/zod">Zod</a>.
</p>

<p align="center">
  <a href="https://quefep.uk">
    <img src="https://img.shields.io/badge/website-quefep.uk-blue" alt="Website: quefep.uk">
  </a>
</p>

## Why Jod?

Traditional Java validation is verbose and annotation-heavy. Jod gives you a modern, fluent API that's:

- **Type-Safe**: Compile-time safety with full generic support
- **Composable**: Build complex schemas from simple building blocks
- **Zero Reflection**: Fast performance, GraalVM native compatible
- **Excellent Errors**: Detailed validation errors with paths and context
- **Framework Agnostic**: Works with Spring, Micronaut, Quarkus, or standalone

## Quick Example

```java
import io.github.m1tsumi.jod.schema.Schema;
import java.util.Map;

// Define your schema
var userSchema = Schema.<Map<String, Object>>object()
    .field("email", Schema.string().email().required())
    .field("age", Schema.integer().min(18).max(120).required())
    .field("role", Schema.string().oneOf("admin", "user", "guest"))
    .field("active", Schema.bool().trueValue())
    .builds(fields -> fields);

// Parse and validate
var result = userSchema.validate(userData);

// Pattern matching for result handling
switch (result) {
    case ValidationResult.Success(Map<String, Object> user) -> {
        // Guaranteed valid data
        System.out.println("Valid user: " + user.get("email"));
    }
    case ValidationResult.Failure(var errors) -> {
        errors.forEach(error -> 
            System.out.println(error.path() + ": " + error.message())
        );
    }
}
```

## Installation

### Maven

```xml
<dependency>
    <groupId>io.github.m1tsumi</groupId>
    <artifactId>jod</artifactId>
    <version>0.1.0</version>
</dependency>
```

### Gradle

```gradle
implementation 'io.github.m1tsumi:jod:0.1.0'
```

## API Reference

### String Validation

```java
// Basic string validation
Schema.string().validate("hello");

// Chain validators
Schema.string()
    .min(5)
    .max(100)
    .email()
    .required()
    .validate("user@example.com");

// Custom validation
Schema.string()
    .custom(s -> s.startsWith("prefix"), "Must start with prefix")
    .validate("prefix_value");

// Regular expressions
Schema.string()
    .regex("[a-z]+")
    .validate("lowercase");
```

### Number Validation

```java
// Integer validation
Schema.integer()
    .min(0)
    .max(100)
    .positive()
    .validate(42);

// Range validation
Schema.integer()
    .range(18, 65)
    .validate(25);

// Non-negative numbers
Schema.integer()
    .nonNegative()
    .validate(0);
```

### Boolean Validation

```java
// Boolean values
Schema.bool().validate(true);

// Require specific value
Schema.bool()
    .trueValue()
    .validate(true);

Schema.bool()
    .falseValue()
    .validate(false);
```

### Object Validation

```java
import java.util.Map;

// Build object schema
var userSchema = Schema.<Map<String, Object>>object()
    .field("email", Schema.string().email().required())
    .field("age", Schema.integer().min(18))
    .field("active", Schema.bool().trueValue())
    .builds(fields -> fields);

// Validate from Map
Map<String, Object> userData = Map.of(
    "email", "user@example.com",
    "age", 25,
    "active", true
);

var result = userSchema.validate(userData);
```

## Error Handling

Jod provides detailed error information with paths for nested objects:

```java
var result = userSchema.validate(invalidData);

if (!result.isValid()) {
    result.getErrors().forEach(error -> {
        System.out.println("Code: " + error.code());
        System.out.println("Message: " + error.message());
        System.out.println("Path: " + error.path());
        System.out.println("Rejected value: " + error.rejectedValue());
    });
}

// Output:
// Code: EMAIL
// Message: Invalid email format
// Path: $.email
// Rejected value: invalid-email
```

## Advanced Usage

### Optional Fields

```java
var schema = Schema.<Map<String, Object>>object()
    .field("email", Schema.string().email())  // Optional by default
    .field("age", Schema.integer().min(18).required())  // Required
    .builds(fields -> fields);
```

### Custom Validators

```java
// Custom validation for any type
Schema.string()
    .custom(s -> !s.contains("spam"), "Cannot contain spam")
    .validate("valid message");

Schema.integer()
    .custom(i -> i % 2 == 0, "Must be even")
    .validate(4);
```

### Reusable Schemas

```java
// Create reusable schema components
var emailSchema = Schema.string().email().max(100);
var ageSchema = Schema.integer().min(18).max(120);

// Use them in multiple places
var userSchema = Schema.<Map<String, Object>>object()
    .field("email", emailSchema.required())
    .field("age", ageSchema.required())
    .builds(fields -> fields);

var adminSchema = Schema.<Map<String, Object>>object()
    .field("email", emailSchema.required())
    .field("level", Schema.integer().min(1))
    .builds(fields -> fields);
```

## Performance

Jod is designed for performance with verified metrics:

- **Zero Reflection**: No runtime reflection for Map-based validation
- **Immutable Schemas**: Thread-safe and shareable
- **Efficient Error Collection**: Minimal allocation when validation fails
- **GraalVM Native**: Compatible with native image compilation

### Benchmarks

Based on our performance tests (simple benchmarks, not JMH):

- String validation: ~1-5 μs per validation
- Object validation: ~10-30 μs per validation  
- Memory usage: <5MB for 1000 schemas with 10,000 validations

Note: ObjectSchema requires Map inputs to maintain zero reflection. For POJOs, convert to Map before validation.

## Comparison with Bean Validation

| Feature | Bean Validation | Jod |
|---------|----------------|-----|
| API Style | Annotations | Fluent code |
| Composition | Difficult | Natural |
| Input Type | POJOs | Map only |
| Reflection | Heavy | None |
| Type Safety | Limited | Full |
| Error Detail | Basic | Comprehensive |
| Native Image | Complex | Easy |

## Requirements

- Java 17 or higher
- Maven 3.6+ or Gradle 7+

## License

Apache License 2.0 - see [LICENSE](LICENSE) file for details.

## Contributing

Contributions are welcome! Please see [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.


## Acknowledgments

Inspired by [Zod](https://github.com/colinhacks/zod) by Colin McDonnell. Built for the modern Java ecosystem.
