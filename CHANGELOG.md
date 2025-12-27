# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.2.0] - 2025-12-27

### Added
- **Long Schema**: New `Schema.long()` for 64-bit integer validation with min/max/range, positive/non-negative constraints
- **Double Schema**: New `Schema.double()` for floating-point number validation with min/max/range, positive/non-negative constraints
- **List Schema**: New `Schema.list(elementSchema)` for validating collections and arrays
- **UUID Validation**: Added `.uuid()` method to StringSchema for UUID format validation
- **URL Validation**: Added `.url()` method to StringSchema for URL format validation
- **Phone Validation**: Added `.phone()` method to StringSchema for international phone number validation (E.164)
- **Date/Time Schemas**: New `Schema.localDate()`, `Schema.localDateTime()`, and `Schema.localTime()` for temporal validation
- **Union Schemas**: New `Schema.union(schema1, schema2, ...)` for validating one of multiple possible schemas
- **Transform Functionality**: Added `.transform()` method to all schemas for value transformation during validation
- **Custom Error Messages**: Enhanced error message customization with `.message()` methods on all validators
- **Schema Composition**: Added `.and()`, `.or()` methods for combining schemas
- **Enum Validation**: Enhanced `oneOf()` to work with any type, not just strings
- **Array Support**: ListSchema now supports arrays and other Iterable types

### Enhanced
- **Error Details**: Improved error messages with more context and better formatting
- **Performance**: Optimized validation performance for complex object schemas
- **Type Safety**: Enhanced generic type safety across all schema types
- **Pattern Matching**: Better support for pattern matching with new result types

### Fixed
- **Email Regex**: Improved email validation regex to handle more edge cases
- **Null Handling**: Better null value handling in nested object validation
- **Memory Usage**: Reduced memory allocation in validation loops

### Changed
- **API Stability**: All existing APIs remain backward compatible
- **Error Codes**: Standardized error codes across all schema types
- **Validation Order**: Consistent validation rule execution order

### Deprecated
- None

### Removed
- None

### Performance
- String validation: ~1-5 μs per validation (unchanged)
- Object validation: ~10-30 μs per validation (unchanged)
- New schema types maintain similar performance characteristics
- Memory usage remains under 5MB for typical workloads

## [0.1.0] - 2025-01-15

### Added
- Initial release of Jod validation library
- String validation with length, regex, email, and custom rules
- Integer validation with range and positivity constraints
- Boolean validation with true/false requirements
- Object validation for Map-based structures
- Pattern matching result handling
- Detailed error reporting with paths
- Zero reflection design for performance
- GraalVM native image compatibility
- Comprehensive test suite and performance benchmarks</content>
<parameter name="filePath">/home/pepe/Desktop/Github DevWork/Software/Jod/CHANGELOG.md