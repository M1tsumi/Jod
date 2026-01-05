# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.3.0] - 2026-01-04

### Added
- Temporal validation: `Schema.localDate()`, `Schema.localDateTime()`, `Schema.localTime()` with min/max and past/future constraints
- `Schema.bigInteger()` for arbitrary-precision integer validation
- `.creditCard()` method for Luhn algorithm validation
- `.postalCode(countryCode)` for international postal code validation (US, UK, CA, DE, FR, AU)

### Enhanced
- Transform chaining and composition across all schemas
- Error message formatting for temporal validations

### Fixed
- Schema composition stability with `.and()` and `.or()` methods

### Performance
- Temporal validation: ~5-15 μs per operation (new)

## [0.2.0] - 2025-12-27

### Added
- Numeric schemas: `Schema.long()`, `Schema.double()` with range constraints
- Collection validation: `Schema.list(elementSchema)` with min/max size
- String format validators: `.uuid()`, `.url()`, `.phone()` (E.164)
- Schema composition: `.union()`, `.and()`, `.or()` for complex validations
- `.transform()` method for value transformation and normalization
- `oneOf()` now supports any value type (not just strings)
- Array and Iterable support in ListSchema

### Enhanced
- Error messages with improved context and detail
- Validation performance for complex objects
- Generic type safety across schemas
- Pattern matching support in results

### Fixed
- Email regex validation for edge cases
- Null handling in nested objects
- Memory efficiency in validation loops

### Performance
- String: ~1-5 μs | Object: ~10-30 μs | Memory: <5MB

## [0.1.0] - 2025-01-15

### Added
- Core schemas: String, Integer, Boolean with full constraint support
- Object validation for Map-based structures
- Pattern matching result handling
- Zero-reflection design for optimal performance
- GraalVM native image support
- Detailed error reporting with path information</content>
<parameter name="filePath">/home/pepe/Desktop/Github DevWork/Software/Jod/CHANGELOG.md