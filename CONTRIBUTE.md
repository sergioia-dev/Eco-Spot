# Guía de Contribución

## Requisitos Previos

### Frontend
- Flutter SDK 3.x
- Android Emulator (o dispositivo físico)

### Backend
- PostgreSQL 14+
- Java 17 o superior

---

## Tecnologías del Proyecto

### Frontend
- **Framework:** Flutter
- **Lenguaje:** Dart
- **Dependencias:**
  - flutter_secure_storage
  - http
  - provider
  - material

### Backend
- **Framework:** Spring Boot
- **Lenguaje:** Java
- **Dependencias:**
  - Spring Boot
  - Spring JPA
  - PostgreSQL Driver

---

## Estructura del Proyecto

```
Eco-Spot/
├── frontend/          # Aplicación Flutter
│   ├── lib/           # Código fuente Dart
│   │   ├── presentation/  # Capa de presentación
│   │   ├── domain/         # Capa de dominio
│   │   └── data/           # Capa de datos
│   └── pubspec.yaml   # Dependencias Flutter
├── backend/           # Aplicación Spring Boot
│   ├── src/main/java/com/ecospot/backend/
│   │   ├── controller/    # Capa de presentación
│   │   ├── service/        # Capa de negocio
│   │   └── repository/    # Capa de datos
│   ├── src/main/resources/
│   │   └── application.properties
│   └── pom.xml        # Dependencias Maven
└── docs/img/          # Imágenes de documentación
```

---

## Arquitectura del Proyecto

### Frontend: Arquitectura por Capas

#### Capa de Presentación
Es la capa más externa, la que el usuario ve e interactúa.

**Responsabilidades:**
- Mostrar interfaces de usuario (pantallas, botones, textos, imágenes)
- Capturar las acciones del usuario (toques, clics, entradas de texto)
- Mostrar datos de forma visual
- Reaccionar a cambios en el estado de la aplicación

**Qué NO debe hacer:**
- Acceder directamente a bases de datos o APIs
- Contener lógica de negocio compleja
- Saber cómo se almacenan o procesan los datos

---

#### Capa de Dominio
Es el corazón de la aplicación. Contiene toda la lógica de negocio y las reglas que hacen única a la aplicación.

**Responsabilidades:**
- Definir las entidades: los objetos principales del negocio (usuario, producto, etc.)
- Definir los casos de uso o interactores: cada acción específica que la aplicación puede realizar
- Definir los repositorios como interfaces: contratos abstractos que describen qué operaciones de datos se necesitan

**Qué NO debe hacer:**
- Tener dependencias de frameworks externos (bases de datos, librerías de red, herramientas de UI)
- Conocer detalles de implementación como SQL, REST API, o SharedPreferences
- Saber cómo se muestran los datos al usuario

---

#### Capa de Datos
Es la capa encargada de obtener y almacenar datos.

**Responsabilidades:**
- Implementar los repositorios definidos en dominio
- Gestionar fuentes de datos: locales (SQLite, SharedPreferences) o remotas (APIs REST)
- Transformar los datos del formato externo al formato que entiende el dominio
- Manejar errores de red, caché, y lógica de sincronización

**Qué NO debe hacer:**
- Contener lógica de negocio
- Saber cómo se muestran los datos en la UI
- Llamar directamente a la capa de presentación

---

### Backend: Arquitectura por Capas

#### Capa de Presentación (Controladores)
Es el punto de entrada para todas las solicitudes HTTP.

**Responsabilidades:**
- Mapear endpoints HTTP a métodos de Java (@GetMapping, @PostMapping, etc.)
- Parsear los datos de la solicitud
- Validación básica de campos
- Autenticación y autorización
- Transformar respuestas a JSON/XML
- Manejar códigos de estado HTTP
- Manejo de excepciones

**Qué NO debe hacer:**
- Contener lógica de negocio o cálculos
- Acceder directamente a la base de datos
- Conocer sobre tablas de base de datos o consultas

---

#### Capa de Negocio (Servicios)
La capa de negocio contiene toda la lógica.

**Responsabilidades:**
- Reglas de negocio: aplicar validaciones como "un usuario no puede tener más de 5 pedidos activos"
- Cálculos: totales, impuestos, tarifas
- Orquestación de flujos: coordinar múltiples llamadas a repositorios o APIs externas
- Validaciones complejas que involucran contexto de negocio
- Manejo de transacciones
- Autorización: quién puede realizar operaciones específicas
- Transformación de datos entre controladores y repositorios

---

#### Capa de Datos (Repositorios)
Encargada del acceso a datos.

**Responsabilidades:**
- Implementar el acceso a la base de datos
- Ejecutar consultas SQL
- Gestionar entidades y mapeo objeto-relacional

---

## Convenciones de Nomenclatura

### Backend

| Tipo | Convención | Ejemplo |
|------|------------|---------|
| Archivos Java | UpperCamelCase | `UserController.java`, `TestConfiguration.java` |
| Variables y métodos | camelCase | `getUserById()`, `codeTest()` |
| Endpoints API | lower_snake_case | `/api/user_profile` |
| Objetos (DTOs/Entidades) | lower_snake_case | `user_profile_dto`, `create_user_request` |

### Frontend

| Tipo | Convención | Ejemplo |
|------|------------|---------|
| Archivos Dart | snake_case | `user_controller.dart`, `test_configuration.dart` |
| Clases | PascalCase | `UserController`, `TestConfiguration` |
| Variables y métodos | camelCase | `getUserById()`, `testConfiguration` |

---

## Instalación del Frontend

### 1. Instalar dependencias
```bash
cd frontend
flutter pub get
```

### 2. Ejecutar en el emulador
```bash
flutter run
```

**Nota:** Asegúrate de tener un emulador de Android en ejecución o un dispositivo conectado.

---

## Instalación del Backend

### 1. Configurar PostgreSQL

Crea una base de datos llamada `ecospot` y añade tus credenciales  a tu variables de entorno o cambia las credenciales en `backend/src/main/resources/application.properties`:

```properties
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/ecospot}
spring.datasource.username=${DB_USER:tu_usuario}
spring.datasource.password=${DB_PASSWORD:tu_contraseña}


server.port=${PORT:8080}
```

### 2. Ejecutar el backend
```bash
cd backend
./mvnw spring-boot:run
```

El backend estará disponible en `http://localhost:8080`.

---

## Cómo Contribuir

### Pasos para contribuir

1. **Fork** el repositorio
2. Crea una rama nueva para tu feature (`git checkout -b feature/nueva-funcionalidad`)
3. Realiza tus cambios siguiendo la arquitectura establecida
4. Asegúrate de que el código siga las convenciones del proyecto
5. Commit tus cambios (`git commit -m 'Agregar nueva funcionalidad'`)
6. Push a la rama (`git push origin feature/nueva-funcionalidad`)
7. Abre un **Pull Request**

### Buenas prácticas

- Sigue la arquitectura por capas establecida
- Mantén las capas separadas y sin dependencias circulares
- Agrega comentarios solo cuando sea necesario para explicar decisiones complejas
- Asegúrate de que el código compile sin errores antes de hacer commit
- Sigue las convenciones de nomenclatura establecidas para cada capa
