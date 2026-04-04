# Eco-Spot Frontend Agent Guidelines

This file provides guidelines for agentic coding agents working on the Eco-Spot frontend project.

## Project Overview

- **Framework**: Flutter 3.10.1
- **Language**: Dart 3.10.1
- **Build Tool**: Flutter (via `flutter` CLI)
- **Target Platforms**: Android, iOS, Web, Windows, Linux, macOS

## Development Environment

**IMPORTANT**: Before running any Flutter commands, you must enter the Nix development shell:

```bash
nix-shell -p flutter
```

This sets up Flutter SDK and all required dependencies. All commands below should be run after entering this shell.

Alternatively, you can use the Flutter SDK if installed globally on your system.

## Project Structure

```
lib/
├── main.dart              # App entry point
└── presentation/
    ├── routes/            # Navigation routes
    ├── screens/           # Screen widgets
    ├── widgets/           # Reusable widgets
    └── providers/         # State management (if any)
test/                      # Test files
```

## Build Commands

### Run
```bash
flutter run                    # Run app (debug mode)
flutter run -d <device>        # Run on specific device (e.g., chrome, android)
```

### Build
```bash
flutter build apk              # Build Android APK (debug)
flutter build apk --release   # Build Android APK (release)
flutter build ios             # Build iOS (requires macOS)
flutter build web             # Build for web
flutter build linux           # Build Linux desktop
```

### Lint & Type Check
```bash
flutter analyze               # Run static analysis (lint + type check)
flutter analyze .            # Analyze current directory
```

### Tests
```bash
flutter test                  # Run all tests
flutter test test/            # Run all tests in a directory
flutter test test/file_test.dart          # Run single test file
flutter test test/file_test.dart --name="test name"  # Run single test by name
flutter test test/file_test.dart --plain-name "test name"  # Run tests matching pattern
```

### Other
```bash
flutter pub get               # Fetch dependencies
flutter pub upgrade           # Upgrade dependencies
flutter doctor               # Check Flutter setup
flutter clean                # Clean build artifacts
```

## Code Style Guidelines

### Indentation & Formatting
- **2 spaces** for indentation (no tabs)
- Line length: 80 characters (recommended, not strictly enforced)
- Use Dart's built-in formatter: `dart format .`

### Naming Conventions
- **Classes/Enums**: PascalCase (e.g., `MyApp`, `UserModel`, `HttpException`)
- **Methods/Variables**: camelCase (e.g., `getUser()`, `userName`, `isLoading`)
- **Constants**: camelCase or UPPER_SNAKE_CASE (e.g., `maxRetries`, `MAX_RETRIES`)
- **Files**: snake_case (e.g., `user_model.dart`, `home_page.dart`)
- **Widgets**: PascalCase with suffix (e.g., `HomePage`, `UserCardWidget`, `SubmitButton`)

### Import Organization
Order imports:
1. Dart SDK (`dart:async`, `dart:io`, etc.)
2. Flutter SDK (`package:flutter/material.dart`, etc.)
3. Third-party packages (`package:http/http.dart`, etc.)
4. Project imports (`package:frontend/presentation/routes/routes.dart`)

Use absolute package imports over relative imports:
```dart
// Good
import 'package:frontend/presentation/routes/routes.dart';

// Avoid
import '../routes/routes.dart';
```

### Widget Guidelines
- Use `const` constructors where possible for performance
- Extract reusable UI into separate widget classes
- Name widget files consistently: `user_card.dart` -> `UserCard`
- Use meaningful widget names: `LoginFormWidget`, not `Widget1`

### State Management
- Follow Flutter's reactive state management patterns
- Use appropriate solution based on complexity (setState, Provider, Riverpod, BLoC, etc.)
- Keep business logic separate from UI widgets

### Error Handling
- Use try-catch for async operations
- Return meaningful error states in UI (not just empty screens)
- Use `FutureBuilder` or `Builder` pattern for async UI
- Log errors appropriately (avoid `print()`, use debugPrint or logging package)

### Controller/Page Guidelines
- Follow single responsibility principle
- Keep widgets focused and composable
- Extract business logic from widgets into separate classes/services

## Linting Rules

This project uses `flutter_lints` package. Key rules from `analysis_options.yaml`:
- Avoid `print()` - use `debugPrint()` instead
- Prefer single quotes for strings
- Use `const` constructors where possible
- Avoid unnecessary type annotations
- Prefer async/await over raw Futures

Run `flutter analyze` to check for lint violations.

## Testing

- Place tests in `test/` directory matching `lib/` structure
- Use `flutter_test` package (JUnit-like with test() and group())
- Follow naming: `user_model_test.dart` for `user_model.dart`
- Use `setUp()` for test fixtures
- Mock external dependencies

## General Best Practices

- Use type annotations for return types and parameters
- Avoid `dynamic` unless necessary
- Use `late` sparingly and only when initialization is guaranteed before use
- Prefer null safety features (?., !., ??) to avoid NPE
- Keep methods small and focused
- Write documentation for public APIs only
- Use `export` for barrel files to simplify imports