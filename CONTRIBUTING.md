# Contributing to Jod

Thank you for your interest in contributing to Jod! This document provides guidelines for contributors.

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+ or Gradle 7+
- Git

### Setup

1. Fork the repository
2. Clone your fork: `git clone https://github.com/YOUR_USERNAME/Jod.git`
3. Navigate to the project: `cd Jod`
4. Build the project: `mvn clean install`

## Development Guidelines

### Code Style

- Use 4 spaces for indentation (no tabs)
- Follow Java naming conventions
- Keep lines under 120 characters
- Add Javadoc for public APIs

### Testing

- Write unit tests for new features
- Aim for high test coverage
- Run tests before submitting: `mvn test`
- Use descriptive test names

### Commit Messages

Follow the conventional commit format:
```
type(scope): subject

body

footer
```

Types: `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`

Example:
```
feat(validation): add phone number validation

Implements E.164 phone number format validation.
Includes comprehensive test coverage.

Closes #42
```

## Submitting Changes

1. Create a new branch: `git checkout -b feature/your-feature-name`
2. Make your changes
3. Add tests for new functionality
4. Ensure all tests pass: `mvn test`
5. Commit your changes with descriptive messages
6. Push to your fork: `git push origin feature/your-feature-name`
7. Create a pull request

## Pull Request Process

- Provide a clear description of changes
- Link any related issues
- Ensure CI checks pass
- Wait for code review

## Areas for Contribution

- Additional validators (dates, URLs, etc.)
- Framework integrations (Spring, Micronaut, etc.)
- Performance optimizations
- Documentation improvements
- Bug fixes

## Code of Conduct

Be respectful, inclusive, and professional in all interactions. We're here to build a great validation library together.

## Questions?

Feel free to open an issue for questions or discussion.
