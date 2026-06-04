# Project Development Guide

This document provides essential information for developers working on the Expert project.

## Build/Configuration Instructions

The project uses **Amper** as the build tool.

### Prerequisites
- JDK 17 or higher.

### Configuration
The application requires MongoDB credentials to be set as environment variables or provided in `resources/application.yaml`.

**Required Environment Variables:**
- `DB_MONGO_USER`: MongoDB username.
- `DB_MONGO_PASSWORD`: MongoDB password.
- `DB_MONGO_DATABASE_NAME`: (Optional) Defaults to `nutricao_db` in `application.yaml`.

### Common Commands
- **Build the project:**
  ```powershell
  .\amper.bat build
  ```
- **Run the application:**
  ```powershell
  .\amper.bat run
  ```

---

## Testing Information

### Configuration
Tests are executed using Amper. Integration tests that load the Ktor application will fail if MongoDB credentials are not provided.

### Running Tests
- **Run all tests:**
  ```powershell
  .\amper.bat test
  ```

### Guidelines for Adding New Tests
- **Unit Tests:** Place in the `test/` directory. Use `kotlin.test` annotations. Unit tests should not depend on external services like MongoDB.
- **Integration Tests:** Use `testApplication { ... }` from Ktor. Ensure mock data or a test database is configured.

### Simple Test Example
The following is a simple unit test for the `Paciente` data class:

```kotlin
package ai.features.paciente

import kotlin.test.*

class PacienteTest {
    @Test
    fun `test valid paciente`() {
        val paciente = Paciente("12345678901", "John Doe", "Lose weight")
        assertEquals("12345678901", paciente.cpf)
        assertEquals("John Doe", paciente.nome)
    }

    @Test
    fun `test invalid paciente blank cpf`() {
        assertFailsWith<IllegalArgumentException> {
            Paciente("", "John Doe", "Lose weight")
        }
    }
}
```

---

## Additional Development Information

### Code Style & Architecture
- **Feature-based Structure:** The project is organized by features (e.g., `src/features/paciente`, `src/features/nutricionista`).
- **Inversion of Control:** Koin is used for dependency injection.
- **Asynchronous Database Access:** The project uses the MongoDB Coroutine driver.
- **Ktor Plugins:** Configuration is split into plugins (Serialization, StatusPages, Koin, etc.) located in `ai.plugins`.

### Debugging
- Logs are configured via `resources/logback.xml`.
- To debug MongoDB connection issues, check `MongoFactory.kt` and ensure the connection string matches your Atlas cluster configuration.
