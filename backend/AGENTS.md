# Eco-Spot Backend Agent Guidelines

This file provides guidelines for agentic coding agents working on the Eco-Spot backend project.

## Project Overview

- **Framework**: Spring Boot 4.0.5
- **Language**: Java 25
- **Build Tool**: Maven (wrapper: `./mvnw`)
- **Database**: PostgreSQL
- **Architecture**: Layered (presentation/business/persistance/util/configuration)

## Development Environment

**IMPORTANT**: Before running any Java or Maven commands, you must enter the Nix development shell:

```bash
nix develop .#backend
```

This sets up Java 25 and all required dependencies. All build commands below should be run after entering this shell.

## Package Structure

```
src/main/java/com/ecospot/
├── presentation/    # Controllers (REST endpoints)
├── business/        # Service layer (business logic)
├── persistance/
│   ├── entity/      # JPA entities (User, Roles enum)
│   └── repository/  # Spring Data repositories
├── util/            # Utilities (JWT)
└── configuration/   # Spring configuration classes
```

## Build Commands

### Build
```bash
./mvnw clean package      # Build JAR
./mvnw clean install      # Build and install to local repo
```

### Run
```bash
./mvnw spring-boot:run   # Run application
java -jar target/backend-0.0.1-SNAPSHOT.jar  # Run JAR directly
```

### Tests
```bash
./mvnw test                    # Run all tests
./mvnw test -Dtest=ClassName           # Run single test class
./mvnw test -Dtest=ClassName#methodName # Run single test method
./mvnw verify                    # Run tests and verify
```

### Other
```bash
./mvnw clean           # Clean target directory
./mvnw dependency:tree # Show dependency tree
```

## Code Style Guidelines

### Indentation & Formatting
- **2 spaces** for indentation (no tabs)
- Line length: reasonable (not strictly enforced, but avoid excessive length)
- Opening brace on same line, closing brace on new line

### Naming Conventions
- **Classes/Interfaces**: PascalCase (e.g., `AuthService`, `UserRepository`)
- **Methods/Variables**: camelCase (e.g., `createUser`, `passwordEncoder`)
- **Constants**: UPPER_SNAKE_CASE (e.g., `MAX_RETRY_COUNT`)
- **Packages**: lowercase, single words ordot-separated (e.g., `com.ecospot.business`)
- **Entities**: Use singular noun (e.g., `User`, not `Users`)
- **Database columns**: snake_case in DB, camelCase in Java (map via `@Column`)

### Classes should follow:
- `@Service` annotation for business logic classes
- `@Repository` for data access classes
- `@RestController` for REST controllers
- `@Configuration` for configuration classes
- `@Component` for general Spring beans

### Import Organization
Order imports:
1. Java standard library (`java.*`)
2. Spring framework (`org.springframework.*`)
3. Third-party libraries (`io.jsonwebtoken.*`, `com.fasterxml.jackson.*`)
4. Project imports (`com.ecospot.*`)

### Dependency Injection
- Use **constructor injection** (preferred) for required dependencies
- Use `@Autowired` on fields only when necessary (avoid)
- Always use `@RequiredArgsConstructor` from Lombok or manual constructor

### Error Handling
- Use **SLF4J Logger** (`org.slf4j.Logger`, `LoggerFactory.getLogger()`)
- Never use `System.out.println()` or `System.err.println()` for logging
- Log appropriate levels: `logger.debug()`, `logger.info()`, `logger.warn()`, `logger.error()`
- Include exception stack trace in error logs: `logger.error("msg", e)`
- Return meaningful error responses from controllers (not just `null` or empty)

### Entity Guidelines
- Always annotate with `@Entity` and `@Table`
- Use `@Id` for primary key
- Use `@Enumerated(EnumType.STRING)` for enums (not ordinal)
- Use proper JPA annotations: `@Column`, `@JoinColumn`, etc.
- Consider using Lombok `@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`
- Use `UUID` type for ID fields (not `Long` unless auto-increment)

### Repository Guidelines
- Extend `JpaRepository<EntityType, IDType>`
- Use Spring Data method naming conventions (e.g., `findByEmail`, `existsByEmail`)
- Avoid custom JPQL unless necessary

### Controller Guidelines
- Use `@RestController` (not `@Controller` for REST APIs)
- Use `@RequestMapping`, `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`
- Return proper HTTP status codes (200, 201, 400, 404, 500)
- Use `@RequestBody` for request bodies
- Consider using DTOs for request/response (don't expose entities directly)

### Properties & Configuration
- Use `@Value` for individual properties
- Use `@ConfigurationProperties` for type-safe configuration
- Store sensitive values in environment variables (not hardcoded)
- Use meaningful default values in `application.properties`

### Testing
- Place tests in `src/test/java/` under same package structure
- Use JUnit 5 (`org.junit.jupiter.api.*`)
- Use `@SpringBootTest` for integration tests
- Use `@WebMvcTest` for controller tests
- Use `@DataJpaTest` for repository tests

### General Best Practices
- Avoid raw types (use generics)
- Use `Optional` for nullable returns from repositories
- Make classes `final` where appropriate
- Use builder pattern for complex object creation
- Keep methods small and focused (single responsibility)
- Write meaningful javadoc for public APIs only (not for every method)