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