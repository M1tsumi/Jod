<p align="center">
  <img src="logo.svg" alt="Jod Logo" width="200" height="200">
</p>

<h1 align="center">Jod</h1>

<p align="center">
  <strong>Modern, type-safe validation for Java 17+</strong><br>
  Inspired by <a href="https://github.com/colinhacks/zod">Zod</a> - Simple, powerful, and delightful to use
</p>

<p align="center">
  <a href="https://quefep.uk">
    <img src="https://img.shields.io/badge/website-quefep.uk-blue" alt="Website: quefep.uk">
  </a>
  <a href="https://github.com/M1tsumi/Jod/releases">
    <img src="https://img.shields.io/badge/version-0.3.0-green" alt="Version 0.3.0">
  </a>
  <a href="https://github.com/M1tsumi/Jod/blob/main/LICENSE">
    <img src="https://img.shields.io/badge/license-Apache%202.0-blue" alt="Apache 2.0 License">
  </a>
</p>

## ✨ Why Choose Jod?

Jod brings modern, intuitive validation to Java with a clean fluent API inspired by Zod. No more verbose boilerplate or annotation sprawl.

### Key Features

- **Type-Safe**: Full compile-time safety with generics
- **Composable**: Build complex validations from simple pieces
- **Zero Reflection**: Lightning-fast performance for high-throughput applications
- **Excellent Errors**: Clear, detailed error messages with path information
- **Framework Agnostic**: Works with Spring, Micronaut, Quarkus, or standalone
- **Modern Java**: Built for Java 17+ with records, sealed classes, and pattern matching

### The Problem with Traditional Java Validation

```java
// Verbose, error-prone, hard to maintain
public class UserValidator {
    public List<String> validate(User user) {
        List<String> errors = new ArrayList<>();
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            errors.add("Invalid email");
        }
        if (user.getAge() < 18) {
            errors.add("Must be 18 or older");
        }
        // ... more boilerplate
        return errors;
    }
}
```

### The Jod Way

```java
// Declarative, type-safe, composable
var userSchema = Schema.<Map<String, Object>>object()
    .field("email", Schema.string().email().required())
    .field("age", Schema.integer().min(18).required())
    .field("role", Schema.string().oneOf("admin", "user", "guest"))
    .builds(fields -> fields);

var result = userSchema.validate(userData);
```

## 🚀 Quick Start

### Installation

**Maven:**
```xml
<dependency>
    <groupId>io.github.m1tsumi</groupId>
    <artifactId>jod</artifactId>
    <version>0.3.0</version>
</dependency>
```

**Gradle:**
```gradle
implementation 'io.github.m1tsumi:jod:0.3.0'
```

### Basic Example

```java
import io.github.m1tsumi.jod.schema.Schema;
import java.util.Map;

// Define schema
var userSchema = Schema.<Map<String, Object>>object()
    .field("email", Schema.string().email().required())
    .field("age", Schema.integer().min(18).max(120).required())
    .field("active", Schema.bool())
    .builds(fields -> fields);

// Validate data
var result = userSchema.validate(Map.of(
    "email", "user@example.com",
    "age", 25,
    "active", true
));

// Handle result
switch (result) {
    case ValidationResult.Success(var data) -> 
        System.out.println("✅ Valid: " + data);
    case ValidationResult.Failure(var errors) -> 
        errors.forEach(e -> System.out.println("❌ " + e.path() + ": " + e.message()));
}
```

## 📚 API Overview

### String Validation

```java
Schema.string()
    .min(5)                    // Min length
    .max(100)                  // Max length
    .email()                   // Email format
    .uuid()                    // UUID format
    .url()                     // HTTP/HTTPS URL
    .phone()                   // E.164 phone number
    .creditCard()              // Luhn algorithm
    .postalCode("US")          // Postal code validation
    .regex("[a-z]+")           // Custom regex
    .oneOf("admin", "user")    // Enum validation
    .custom(s -> s.length() > 3, "Custom rule")
    .transform(String::toLowerCase)  // Transform value
    .required()                // Non-null
    .validate("value");
```

### Numeric Validation

```java
// Integer (32-bit)
Schema.integer()
    .min(0).max(100)
    .positive()
    .nonNegative()
    .range(18, 65)
    .validate(42);

// Long (64-bit)
Schema.longValue()
    .min(1L).max(9999999999L)
    .positive()
    .validate(1234567890L);

// Double (floating-point)
Schema.doubleValue()
    .min(0.0).max(100.0)
    .validate(42.5);

// BigInteger (arbitrary precision)
Schema.bigInteger()
    .min(BigInteger.valueOf(100))
    .positive()
    .validate(new BigInteger("999999999"));
```

### Temporal Validation

```java
Schema.localDate()
    .min(LocalDate.of(2020, 1, 1))
    .max(LocalDate.of(2030, 12, 31))
    .past()                    // Must be before today
    .future()                  // Must be after today
    .validate(LocalDate.now());

Schema.localDateTime()
    .min(LocalDateTime.now().minusHours(1))
    .past()
    .validate(LocalDateTime.now());

Schema.localTime()
    .min(LocalTime.of(9, 0))
    .max(LocalTime.of(17, 0))
    .validate(LocalTime.now());
```

### Collections & Composition

```java
// Lists
Schema.list(Schema.string().email())
    .minSize(1)
    .maxSize(10)
    .validate(List.of("user@example.com"));

// Union (OR logic)
Schema.union(
    Schema.string().email(),
    Schema.string().phone()
).validate("user@example.com");

// Objects
Schema.<Map<String, Object>>object()
    .field("email", Schema.string().email().required())
    .field("age", Schema.integer().min(18).required())
    .field("tags", Schema.list(Schema.string()).optional())
    .builds(fields -> fields)
    .validate(userData);

// Boolean
Schema.bool()
    .trueValue()               // Must be true
    .falseValue()              // Must be false
    .validate(true);
```

## �️ Error Handling

Jod collects all validation errors with detailed path information:

```java
var result = userSchema.validate(invalidData);

if (result.isFailure()) {
    result.getErrors().forEach(error -> {
        System.out.println("Path: " + error.path());
        System.out.println("Message: " + error.message());
        System.out.println("Code: " + error.code());
    });
}
// Output:
// Path: $.email
// Message: Invalid email format
// Code: EMAIL
```

Error paths use JSONPath notation:
- `$.fieldName` - top-level field
- `$.user.email` - nested field
- `$[0].email` - array element
- `$.users[2].tags[0]` - deeply nested

## � Common Patterns

### Reusable Schema Components

```java
// Define reusable validators
var emailSchema = Schema.string().email().max(100);
var ageSchema = Schema.integer().min(18).max(120);

// Use in multiple schemas
var userSchema = Schema.<Map<String, Object>>object()
    .field("email", emailSchema.required())
    .field("age", ageSchema.required())
    .builds(fields -> fields);
```

### Conditional Validation

```java
var schema = Schema.<Map<String, Object>>object()
    .field("type", Schema.string().oneOf("personal", "business").required())
    .field("company", Schema.string())
    .builds(fields -> {
        if ("business".equals(fields.get("type")) && fields.get("company") == null) {
            throw new IllegalArgumentException("Company required for business accounts");
        }
        return fields;
    });
```

### Data Normalization

```java
var userSchema = Schema.<Map<String, Object>>object()
    .field("email", Schema.string()
        .email()
        .transform(String::toLowerCase)  // Normalize to lowercase
        .required())
    .field("age", Schema.integer()
        .transform(Math::abs)            // Ensure positive
        .required())
    .builds(fields -> fields);
```

### Spring Boot Integration

```java
@RestController
public class UserController {
    private final BaseSchema<Map<String, Object>> schema = createSchema();

    @PostMapping("/users")
    public ResponseEntity<?> create(@RequestBody Map<String, Object> data) {
        var result = schema.validate(data);
        
        return switch (result) {
            case ValidationResult.Success(var validated) -> 
                ResponseEntity.ok(userService.save(validated));
            case ValidationResult.Failure(var errors) -> {
                var errorMap = errors.stream()
                    .collect(Collectors.groupingBy(
                        ValidationError::path,
                        Collectors.mapping(ValidationError::message, Collectors.toList())
                    ));
                yield ResponseEntity.badRequest().body(errorMap);
            }
        };
    }
}
```

## ⚡ Performance

Jod is optimized for speed with zero reflection:

| Operation | Performance | Notes |
|-----------|-------------|-------|
| String validation | ~1-5 μs | Email, UUID, regex, length |
| Number validation | ~1-5 μs | Integer, Long, Double |
| Boolean validation | ~0.5-1 μs | Simple checks |
| Object validation | ~10-30 μs | Multiple fields |
| List validation | ~20-50 μs | Depends on size |
| Union validation | ~5-15 μs | Tries schemas in order |

**Why Jod is Fast:**
- No reflection - compile-time resolved
- Immutable schemas - thread-safe, cacheable
- Smart error collection - minimal allocation
- GraalVM compatible - native compilation ready

## 🔄 Jod vs Alternatives

| Feature | Bean Validation | Jod |
|---------|-----------------|-----|
| API Style | Annotations | Fluent |
| Composition | Difficult | Natural |
| Type Safety | Limited | Full |
| Reflection | Heavy | None |
| Performance | Medium | Very Fast |
| Error Detail | Basic | Excellent |
| Learning Curve | Medium | Low |

**Choose Jod for:**
- High-performance APIs and microservices
- Type-safe, composable validation
- Data processing pipelines
- Modern Java (17+) projects
- Applications requiring detailed error paths

## 📋 Requirements

- **Java**: 17+ (records, sealed classes, pattern matching)
- **Build Tools**: Maven 3.6+ or Gradle 7+
- **Frameworks**: Spring, Micronaut, Quarkus, or standalone
- **Memory**: Minimal footprint, suitable for microservices

## 🤝 Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for detailed guidelines. We welcome:
- Bug reports with minimal reproductions
- Feature discussions and proposals
- Pull requests with tests

## 📄 License

[Apache License 2.0](LICENSE) - Free to use, modify, and distribute in commercial projects.

---

Made with ❤️ for the Java community
