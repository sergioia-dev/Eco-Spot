# Documentación de Hostales

## Crear Rental

Crea un nuevo rental (propiedad de alquiler) asociado al usuario HOST autenticado.

**URL:** `POST /api/v1/host/rentals`

**Encabezados:**
| Campo | Valor |
|-------|-------|
| Authorization | Bearer {TOKEN_JWT} |

**Cuerpo de la solicitud (JSON):**
| Campo | Tipo | Obligatorio | Descripción |
|-------|------|-------------|-------------|
| name | String | Sí | Nombre del rental |
| description | String | No | Descripción del rental |
| contact | String | Sí | Teléfono de contacto |
| size | Integer | Sí | Tamaño en m² |
| peopleQuantity | Integer | Sí | Cantidad máxima de personas |
| rooms | Integer | Sí | Número de habitaciones |
| bathrooms | Integer | Sí | Número de baños |
| city | String | Sí | Ciudad |
| country | String | Sí | País |
| location | String | No | Dirección/ubicación |
| valueNight | Double | Sí | Precio por noche |

**Imágenes (multipart/form-data):**
| Campo | Tipo | Obligatorio | Descripción |
|-------|------|-------------|-------------|
| images | File[] | No | Imágenes (máximo 3, formatos: jpg, png, webp) |

**Respuestas:**
- **201 Created:** Rental creado exitosamente
- **400 Bad Request:** Datos faltantes, inválidos o más de 3 imágenes
- **401 Unauthorized:** Token inválido o usuario no tiene rol HOST

**Ejemplo de solicitud:**
```bash
curl -X POST "http://localhost:8080/api/v1/host/rentals" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Beach House",
    "description": "Nice house near the beach",
    "contact": "1234567890",
    "size": 100,
    "peopleQuantity": 4,
    "rooms": 2,
    "bathrooms": 1,
    "city": "Miami",
    "country": "USA",
    "location": "123 Beach St",
    "valueNight": 150.00
  }' \
  -F "images=@photo1.jpg" \
  -F "images=@photo2.png"
```

**Notas:**
- Solo usuarios con rol HOST pueden acceder a este endpoint
- Las imágenes se almacenan en la carpeta `/images/` con nombres UUID
- La tabla `images` guarda el ID (UUID) y la extensión del archivo
- El rental se crea con `is_enable = true` por defecto

## Actualizar Rental

Actualiza un rental existente. Solo el HOST que creó el rental o un ADMIN pueden actualizarlo.

**URL:** `PUT /api/v1/host/rentals/{rentalId}`

**Encabezados:**
| Campo | Valor |
|-------|-------|
| Authorization | Bearer {TOKEN_JWT} |

**Parámetros de ruta:**
| Campo | Tipo | Descripción |
|-------|------|-------------|
| rentalId | UUID | ID del rental a actualizar |

**Cuerpo de la solicitud (JSON):**
| Campo | Tipo | Obligatorio | Descripción |
|-------|------|-------------|-------------|
| name | String | Sí | Nombre del rental |
| description | String | No | Descripción del rental |
| contact | String | Sí | Teléfono de contacto |
| size | Integer | Sí | Tamaño en m² |
| peopleQuantity | Integer | Sí | Cantidad máxima de personas |
| rooms | Integer | Sí | Número de habitaciones |
| bathrooms | Integer | Sí | Número de baños |
| city | String | Sí | Ciudad |
| country | String | Sí | País |
| location | String | No | Dirección/ubicación |
| valueNight | Double | Sí | Precio por noche |

**Imágenes (multipart/form-data):**
| Campo | Tipo | Obligatorio | Descripción |
|-------|------|-------------|-------------|
| images | File[] | No | Imágenes (máximo 3, formatos: jpg, png, webp). Si se envían, reemplazan todas las imágenes existentes |

**Respuestas:**
- **200 OK:** Rental actualizado exitosamente
- **400 Bad Request:** Datos faltantes, inválidos o más de 3 imágenes
- **401 Unauthorized:** Token inválido o usuario no tiene rol HOST/ADMIN
- **403 Forbidden:** Usuario no es el propietario ni ADMIN
- **404 Not Found:** Rental no existe

**Ejemplo de solicitud:**
```bash
curl -X PUT "http://localhost:8080/api/v1/host/rentals/550e8400-e29b-41d4-a716-446655440000" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Beach House Updated",
    "description": "Updated description",
    "contact": "9876543210",
    "size": 120,
    "peopleQuantity": 5,
    "rooms": 3,
    "bathrooms": 2,
    "city": "Miami",
    "country": "USA",
    "location": "456 Ocean Dr",
    "valueNight": 200.00
  }' \
  -F "images=@newphoto1.jpg"
```

**Notas:**
- Solo el HOST que creó el rental o un usuario con rol ADMIN pueden actualizarlo
- Las imágenes existentes serán eliminadas y reemplazadas por las nuevas
- Todos los campos son requeridos (full update)

## Eliminar Rental

Elimina un rental existente. Solo el HOST que creó el rental o un ADMIN pueden eliminarlo.

**URL:** `DELETE /api/v1/host/rentals/{rentalId}`

**Encabezados:**
| Campo | Valor |
|-------|-------|
| Authorization | Bearer {TOKEN_JWT} |

**Parámetros de ruta:**
| Campo | Tipo | Descripción |
|-------|------|-------------|
| rentalId | UUID | ID del rental a eliminar |

**Respuestas:**
- **200 OK:** Rental eliminado exitosamente
- **400 Bad Request:** Encabezado Authorization faltante
- **401 Unauthorized:** Token inválido
- **403 Forbidden:** Usuario no es el propietario, no es ADMIN, o rental no existe
- **409 Conflict:** El rental tiene reservas futuras

**Ejemplo de solicitud:**
```bash
curl -X DELETE "http://localhost:8080/api/v1/host/rentals/550e8400-e29b-41d4-a716-446655440000" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

**Notas:**
- Solo el HOST que creó el rental o un usuario con rol ADMIN pueden eliminarlo
- No se puede eliminar un rental que tiene reservas futuras (retorna 409 Conflict)
- Todas las imágenes asociadas al rental también serán eliminadas (archivos y registros en la base de datos)
- Eliminar un rental que no existe retorna 403 Forbidden

## Obtener Rentals del Usuario

Retorna todos los rentals del usuario autenticado.

**URL:** `GET /api/v1/host/rentals`

**Encabezados:**
| Campo | Valor |
|-------|-------|
| Authorization | Bearer {TOKEN_JWT} |

**Parámetros de query:**
| Campo | Tipo | Obligatorio | Default | Descripción |
|-------|------|-------------|---------|-------------|
| includeDisabled | boolean | No | false | Incluir rentals deshabilitados |

**Respuestas:**
- **200 OK:** Lista de rentals del usuario
- **400 Bad Request:** Encabezado Authorization faltante
- **401 Unauthorized:** Token inválido

**Ejemplo de solicitud:**
```bash
curl -X GET "http://localhost:8080/api/v1/host/rentals" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

curl -X GET "http://localhost:8080/api/v1/host/rentals?includeDisabled=true" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

**Ejemplo de respuesta:**
```json
[{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "Beach House",
  "description": "Nice house",
  "contact": "1234567890",
  "size": 100,
  "peopleQuantity": 4,
  "rooms": 2,
  "bathrooms": 1,
  "city": "MIAMI",
  "country": "USA",
  "location": "123 Beach St",
  "valueNight": 150.0,
  "isEnable": true,
  "reviewAverage": 4.5,
  "images": [
    { "id": "uuid1", "extension": "jpg" }
  ]
}]
```

**Notas:**
- El usuario debe tener un token JWT válido
- Por defecto solo retorna rentals con `is_enable=true`
- El campo `reviewAverage` retorna el promedio de reseñas (0.0 si no hay reseñas)
- Cuando `includeDisabled=true`, retorna todos los rentals (habilitados y deshabilitados)
- El campo `isEnable` indica si el rental está habilitado

## Habilitar/Deshabilitar Rental

Habilita o deshabilita un rental existente.

**URL:** `PATCH /api/v1/host/rentals/{rentalId}/enable`

**Encabezados:**
| Campo | Valor |
|-------|-------|
| Authorization | Bearer {TOKEN_JWT} |

**Parámetros de ruta:**
| Campo | Tipo | Descripción |
|-------|------|-------------|
| rentalId | UUID | ID del rental |

**Parámetros de query:**
| Campo | Tipo | Obligatorio | Descripción |
|-------|------|-------------|-------------|
| enabled | boolean | Sí | true para habilitar, false para deshabilitar |

**Respuestas:**
- **200 OK:** Rental actualizado exitosamente
- **400 Bad Request:** Encabezado Authorization faltante
- **403 Forbidden:** Usuario no es el propietario ni ADMIN

**Ejemplo de solicitud:**
```bash
# Habilitar rental
curl -X PATCH "http://localhost:8080/api/v1/host/rentals/550e8400-e29b-41d4-a716-446655440000/enable?enabled=true" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

# Deshabilitar rental
curl -X PATCH "http://localhost:8080/api/v1/host/rentals/550e8400-e29b-41d4-a716-446655440000/enable?enabled=false" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

**Notas:**
- Solo el HOST que creó el rental o un usuario con rol ADMIN pueden habilitar/deshabilitarlo
- Los rentals deshabilitados no aparecen en las búsquedas de turistas
- Los rentals con reservas futuras no pueden ser deshabilitados

## Obtener Reservaciones de un Rental

Retorna las reservaciones de un rental específico.

**URL:** `GET /api/v1/host/rentals/{rentalId}/reservations`

**Encabezados:**
| Campo | Valor |
|-------|-------|
| Authorization | Bearer {TOKEN_JWT} |

**Parámetros de ruta:**
| Campo | Tipo | Descripción |
|-------|------|-------------|
| rentalId | UUID | ID del rental |

**Parámetros de query:**
| Campo | Tipo | Obligatorio | Default | Descripción |
|-------|------|-------------|---------|-------------|
| upcoming | boolean | No | true | true = reservaciones futuras, false = reservaciones pasadas |

**Respuestas:**
- **200 OK:** Lista de reservaciones del rental
- **400 Bad Request:** Encabezado Authorization faltante
- **403 Forbidden:** Usuario no es el propietario ni ADMIN

**Ejemplo de solicitud:**
```bash
# Reservaciones futuras
curl -X GET "http://localhost:8080/api/v1/host/rentals/550e8400-e29b-41d4-a716-446655440000/reservations?upcoming=true" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

# Reservaciones pasadas
curl -X GET "http://localhost:8080/api/v1/host/rentals/550e8400-e29b-41d4-a716-446655440000/reservations?upcoming=false" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

**Ejemplo de respuesta:**
```json
[{
  "id": "550e8400-e29b-41d4-a716-446655440001",
  "rentalId": "550e8400-e29b-41d4-a716-446655440000",
  "rentalName": "Beach House",
  "userName": "John",
  "userSurname": "Doe",
  "startingDate": "2026-05-01",
  "endDate": "2026-05-05",
  "isCancelled": false
}]
```

**Notas:**
- Solo el propietario del rental o un ADMIN pueden ver las reservaciones
- Las reservaciones canceladas no se muestran
- Por defecto retorna reservaciones futuras (`upcoming=true`)

## Cancelar Reserva

Cancela una reservación de un rental.

**URL:** `PATCH /api/v1/host/reservations/{reservationId}/cancel`

**Encabezados:**
| Campo | Valor |
|-------|-------|
| Authorization | Bearer {TOKEN_JWT} |

**Parámetros de ruta:**
| Campo | Tipo | Descripción |
|-------|------|-------------|
| reservationId | UUID | ID de la reservación |

**Respuestas:**
- **200 OK:** Reservación cancelada exitosamente
- **400 Bad Request:** Encabezado Authorization faltante
- **403 Forbidden:** No autorizado o reservación ya cancelada

**Ejemplo de solicitud:**
```bash
curl -X PATCH "http://localhost:8080/api/v1/host/reservations/550e8400-e29b-41d4-a716-446655440000/cancel" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

**Notas:**
- Solo el HOST que creó el rental, un ADMIN, o el turista que hizo la reservación pueden cancelarla
- No se puede cancelar una reservación que ya está cancelada
- Las reservaciones canceladas no aparecen en las listas de reservaciones