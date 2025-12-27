# 🤝 Contributing to Jod

First off, **thank you** for considering contributing to Jod! 🎉

Whether you're fixing bugs, adding features, improving documentation, or helping with tests - your contributions make Jod better for everyone. This guide will help you get started and ensure your contributions fit well with the project.

## 🚀 Quick Start

### Prerequisites
- **Java**: 17 or higher (we use modern Java features!)
- **Build Tools**: Maven 3.6+ or Gradle 7+
- **Git**: For version control
- **IDE**: IntelliJ IDEA, Eclipse, or VS Code with Java support

### Setup Your Development Environment

1. **Fork the repository** on GitHub
2. **Clone your fork**:
   ```bash
   git clone https://github.com/YOUR_USERNAME/Jod.git
   cd Jod
   ```
3. **Add upstream remote**:
   ```bash
   git remote add upstream https://github.com/M1tsumi/Jod.git
   ```
4. **Build and test**:
   ```bash
   mvn clean install  # or ./gradlew build
   ```

That's it! You're ready to start contributing.

## 💡 What Can I Contribute?

### 🐛 Bug Fixes
- Found a bug? Great! Check existing issues first, then submit a fix
- Include a test case that reproduces the bug
- Explain what the fix does and why it's correct

### ✨ New Features
- Check the [issues](https://github.com/M1tsumi/Jod/issues) for feature requests
- Start a discussion if you have a new idea
- Features should fit Jod's philosophy: type-safe, fast, composable

### 📚 Documentation
- Improve examples, fix typos, clarify confusing sections
- Add more comprehensive guides for complex use cases
- Translate documentation (we'd love more languages!)

### 🧪 Testing
- Add more test cases, especially edge cases
- Improve test coverage
- Performance benchmarks and regression tests

### 🛠️ Tooling
- Build improvements, CI/CD enhancements
- IDE integrations, code generation tools
- Performance monitoring and profiling

## 📝 Development Guidelines

### Code Style & Quality

**We follow these principles:**
- **Readable**: Code should be self-documenting
- **Maintainable**: Easy to modify and extend
- **Performant**: No unnecessary allocations or operations
- **Tested**: Every feature has comprehensive tests

**Technical Standards:**
- Use 4 spaces for indentation (no tabs)
- Follow standard Java naming conventions
- Keep lines under 120 characters
- Add Javadoc for all public APIs
- Use `final` for immutable variables
- Prefer records for simple data classes

### Example Code Style

```java
/**
 * Validates string values with comprehensive options.
 * This class demonstrates Jod's fluent API pattern.
 */
public class StringSchema implements BaseSchema<String> {

    // Good: Clear, descriptive field names
    private final boolean required;
    private final Integer minLength;

    // Good: Records for immutable data
    private record CustomRule<T>(Predicate<T> predicate, String message) {}

    // Good: Clear method names and comprehensive docs
    /**
     * Sets the minimum length requirement.
     *
     * @param minLength the minimum length (inclusive)
     * @return a new schema with min length validation
     * @throws IllegalArgumentException if minLength is negative
     */
    public StringSchema min(int minLength) {
        if (minLength < 0) {
            throw new IllegalArgumentException("minLength cannot be negative");
        }
        return new StringSchema(required, minLength, maxLength, customRules);
    }
}
```

### Testing Standards

**Every contribution must include tests:**
- Unit tests for new functionality
- Integration tests for complex features
- Edge case coverage (null values, empty strings, etc.)
- Performance regression tests for performance-critical code

**Test Naming Convention:**
```java
@Test
void shouldValidateValidEmail() {
    // Test implementation
}

@Test
void shouldRejectInvalidEmail() {
    // Test implementation
}

@ParameterizedTest
@ValueSource(strings = {"test@example.com", "user.name@domain.co.uk"})
void shouldValidateValidEmails(String email) {
    // Test implementation
}
```

### Commit Messages

We use [Conventional Commits](https://conventionalcommits.org/) for clear, structured commit messages:

```
type(scope): brief description

Optional detailed explanation of changes.
Include context and reasoning.

Footer with issue references or breaking changes.
```

**Types:**
- `feat`: New features
- `fix`: Bug fixes
- `docs`: Documentation changes
- `style`: Code style changes (formatting, etc.)
- `refactor`: Code refactoring
- `test`: Test additions or modifications
- `chore`: Maintenance tasks

**Examples:**
```
feat(validation): add UUID string validation

Add .uuid() method to StringSchema for validating UUID format strings.
Includes comprehensive test coverage for valid and invalid UUIDs.

Closes #42
```

```
fix(performance): optimize error collection

Reduce memory allocation in validation error collection by using
more efficient data structures. Improves performance by ~15%.

Related to #38
```

## 🔄 Pull Request Process

### Before Submitting

1. **Update your fork**:
   ```bash
   git fetch upstream
   git rebase upstream/main
   ```

2. **Run all tests**:
   ```bash
   mvn test
   ```

3. **Check code quality**:
   ```bash
   mvn spotless:check  # if we have code formatting
   ```

4. **Write clear commit messages** following the guidelines above

### Creating Your PR

1. **Push your branch** to your fork
2. **Create a Pull Request** on GitHub
3. **Fill out the PR template**:
   - Clear title describing the change
   - Detailed description of what was changed and why
   - Link to any related issues
   - Screenshots or examples if UI-related

### PR Review Process

1. **Automated checks** will run (tests, linting, etc.)
2. **Maintainer review** will happen within a few days
3. **Feedback** may be requested for changes
4. **Approval** and merge once everything looks good

## 🎯 Code of Conduct

**Be excellent to each other!** 🤝

- Be respectful and inclusive
- Focus on constructive feedback
- Help newcomers learn and contribute
- Assume good intent
- Keep discussions technical and on-topic

## ❓ Questions?

- **Check existing issues** first
- **Search the documentation**
- **Ask in discussions** for general questions
- **Open an issue** for bugs or feature requests

We're here to help you contribute successfully! 🚀

## 📜 License

By contributing to Jod, you agree that your contributions will be licensed under the same Apache License 2.0 that covers the project.
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
