# Autenticación

## Registrar Usuario

Crea un nuevo usuario en el sistema.

**URL:** `POST /api/v1/auth/register`

**Cuerpo de la solicitud (JSON):**
```json
{
  "name": "Nombre del usuario",
  "surname": "Apellido del usuario",
  "city" : "MEDELLIN",
  "country" : "COLOMBIA",
  "email": "correo@ejemplo.com",
  "password": "contraseña123",
  "rol": "TOURIST"
}
```

**Parámetros:**
| Campo | Tipo | Obligatorio | Descripción |
|-------|------|-------------|-------------|
| name | String | Sí | Nombre del usuario |
| surname | String | Sí | Apellido del usuario |
| email | String | Sí | Correo electrónico (debe ser único) |
| password | String | Sí | Contraseña del usuario |
| rol | String | Sí | Rol del usuario (TOURIST, HOST, BUSINESS, ADMINISTRATOR) |

**Respuestas:**
- **201 Created:** Usuario creado exitosamente
- **409 Conflict:** El correo electrónico ya está registrado

**Nota:** El campo `rol` solo puede aceptar los valores: `TOURIST`, `HOST`, `BUSINESS` o `ADMINISTRATOR`.

**Ejemplo de solicitud:**
```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Juan",
    "surname": "Pérez",
    "email": "juan@example.com",
    "password": "miContraseña123",
    "rol": "TOURIST"
  }'
```

## Iniciar Sesión

Autentica a un usuario y retorna un token JWT.

**URL:** `POST /api/v1/auth/login`

**Cuerpo de la solicitud (JSON):**
```json
{
  "email": "correo@ejemplo.com",
  "password": "contraseña123"
}
```

**Parámetros:**
| Campo | Tipo | Obligatorio | Descripción |
|-------|------|-------------|-------------|
| email | String | Sí | Correo electrónico del usuario |
| password | String | Sí | Contraseña del usuario |

**Respuestas:**
- **200 OK:** Inicio de sesión exitoso (retorna el token JWT)
- **401 Unauthorized:** Credenciales incorrectas

**Ejemplo de solicitud:**
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "juan@example.com",
    "password": "miContraseña123"
  }'
```

**Respuesta exitosa:**
```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```
