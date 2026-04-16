# Documentación de Negocios

## Obtener Negocios del Usuario

Retorna todos los negocios del usuario autenticado.

**URL:** `GET /api/v1/businesses`

**Encabezados:**
| Campo | Valor |
|-------|-------|
| Authorization | Bearer {TOKEN_JWT} |

**Parámetros de query:**
| Campo | Tipo | Obligatorio | Default | Descripción |
|-------|------|-------------|---------|-------------|
| includeDisabled | boolean | No | false | Incluir negocios deshabilitados |

**Respuestas:**
- **200 OK:** Lista de negocios del usuario
- **400 Bad Request:** Encabezado Authorization faltante

**Ejemplo de solicitud:**
```bash
curl -X GET "http://localhost:8080/api/v1/businesses" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

curl -X GET "http://localhost:8080/api/v1/businesses?includeDisabled=true" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

**Ejemplo de respuesta:**
```json
[{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "Restaurante Mi Casa",
  "description": "Restaurant de comida tradicional",
  "contact": "1234567890",
  "city": "Madrid",
  "country": "ESPAÑA",
  "location": "Calle Principal 123",
  "menu": "Desayunos, Almuerzos, Cenas",
  "isEnable": true,
  "images": [
    { "id": "uuid1", "extension": "jpg" }
  ]
}]
```

**Notas:**
- El usuario debe tener un token JWT válido con rol BUSINESS
- Por defecto solo retorna negocios con `is_enable=true`
- El campo `isEnable` indica si el negocio está habilitado
- Cuando `includeDisabled=true`, retorna todos los negocios (habilitados y deshabilitados)

## Crear Negocio

Crea un nuevo negocio (business) asociado al usuario BUSINESS autenticado.

**URL:** `POST /api/v1/businesses`

**Encabezados:**
| Campo | Valor |
|-------|-------|
| Authorization | Bearer {TOKEN_JWT} |

**Cuerpo de la solicitud (JSON):**
| Campo | Tipo | Obligatorio | Descripción |
|-------|------|-------------|-------------|
| name | String | Sí | Nombre del negocio |
| description | String | No | Descripción del negocio |
| contact | String | Sí | Teléfono de contacto |
| city | String | Sí | Ciudad |
| country | String | Sí | País |
| location | String | No | Dirección/ubicación |
| menu | String | No | Menú del negocio (para restaurantes) |

**Imágenes (multipart/form-data):**
| Campo | Tipo | Obligatorio | Descripción |
|-------|------|-------------|-------------|
| images | File[] | No | Imágenes (máximo 3, formatos: jpg, png, webp) |

**Respuestas:**
- **201 Created:** Negocio creado exitosamente
- **400 Bad Request:** Datos faltantes, inválidos o más de 3 imágenes
- **401 Unauthorized:** Token inválido o usuario no tiene rol BUSINESS

**Ejemplo de solicitud:**
```bash
curl -X POST "http://localhost:8080/api/v1/businesses" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Restaurante Mi Casa",
    "description": "Restaurant de comida tradicional",
    "contact": "1234567890",
    "city": "Madrid",
    "country": "ESPAÑA",
    "location": "Calle Principal 123",
    "menu": "Desayunos, Almuerzos, Cenas"
  }' \
  -F "images=@photo1.jpg" \
  -F "images=@photo2.png"
```

**Notas:**
- Solo usuarios con rol BUSINESS pueden acceder a este endpoint
- Las imágenes se almacenan en la carpeta `/images/` con nombres UUID
- La tabla `images` guarda el ID (UUID) y la extensión del archivo
- El negocio se crea con `is_enable = true` por defecto
- El negocio aparece en las búsquedas de turistas según su ciudad y país

## Actualizar Negocio

Actualiza un negocio existente. Solo el BUSINESS que creó el negocio o un ADMIN pueden actualizarlo.

**URL:** `PUT /api/v1/businesses/{businessId}`

**Encabezados:**
| Campo | Valor |
|-------|-------|
| Authorization | Bearer {TOKEN_JWT} |

**Parámetros de ruta:**
| Campo | Tipo | Descripción |
|-------|------|-------------|
| businessId | UUID | ID del negocio a actualizar |

**Cuerpo de la solicitud (JSON):**
| Campo | Tipo | Obligatorio | Descripción |
|-------|------|-------------|-------------|
| name | String | Sí | Nombre del negocio |
| description | String | No | Descripción del negocio |
| contact | String | Sí | Teléfono de contacto |
| city | String | Sí | Ciudad |
| country | String | Sí | País |
| location | String | No | Dirección/ubicación |
| menu | String | No | Menú del negocio (para restaurantes) |

**Imágenes (multipart/form-data):**
| Campo | Tipo | Obligatorio | Descripción |
|-------|------|-------------|-------------|
| images | File[] | No | Imágenes (máximo 3, formatos: jpg, png, webp). Si se envían, reemplazan todas las imágenes existentes |

**Respuestas:**
- **200 OK:** Negocio actualizado exitosamente
- **400 Bad Request:** Datos faltantes, inválidos o más de 3 imágenes
- **401 Unauthorized:** Token inválido
- **403 Forbidden:** Usuario no es el propietario ni ADMIN

**Ejemplo de solicitud:**
```bash
curl -X PUT "http://localhost:8080/api/v1/businesses/550e8400-e29b-41d4-a716-446655440000" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Restaurante Mi Casa Actualizado",
    "description": "Nueva descripción",
    "contact": "9876543210",
    "city": "Barcelona",
    "country": "ESPAÑA",
    "location": "Nueva Calle 456",
    "menu": "Solo cenas"
  }' \
  -F "images=@newphoto1.jpg"
```

**Notas:**
- Solo el BUSINESS que creó el negocio o un usuario con rol ADMIN pueden actualizarlo
- Las imágenes existentes serán eliminadas y reemplazadas por las nuevas
- Todos los campos son requeridos (full update)
- El negocio debe existir previamente

## Eliminar Negocio

Elimina un negocio existente. Solo el BUSINESS que creó el negocio o un ADMIN pueden eliminarlo.

**URL:** `DELETE /api/v1/businesses/{businessId}`

**Encabezados:**
| Campo | Valor |
|-------|-------|
| Authorization | Bearer {TOKEN_JWT} |

**Parámetros de ruta:**
| Campo | Tipo | Descripción |
|-------|------|-------------|
| businessId | UUID | ID del negocio a eliminar |

**Respuestas:**
- **200 OK:** Negocio eliminado exitosamente
- **400 Bad Request:** Encabezado Authorization faltante
- **403 Forbidden:** Usuario no es el propietario, no es ADMIN, o negocio no existe

**Ejemplo de solicitud:**
```bash
curl -X DELETE "http://localhost:8080/api/v1/businesses/550e8400-e29b-41d4-a716-446655440000" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

**Notas:**
- Solo el BUSINESS que creó el negocio o un usuario con rol ADMIN pueden eliminarlo
- Todas las imágenes asociadas al negocio también serán eliminadas (archivos y registros en la base de datos)
- Eliminar un negocio que no existe retorna 403 Forbidden

## Habilitar/Deshabilitar Negocio

Habilita o deshabilita un negocio existente.

**URL:** `PATCH /api/v1/businesses/{businessId}`

**Encabezados:**
| Campo | Valor |
|-------|-------|
| Authorization | Bearer {TOKEN_JWT} |

**Parámetros de ruta:**
| Campo | Tipo | Descripción |
|-------|------|-------------|
| businessId | UUID | ID del negocio |

**Parámetros de query:**
| Campo | Tipo | Obligatorio | Descripción |
|-------|------|-------------|-------------|
| enabled | boolean | Sí | true para habilitar, false para deshabilitar |

**Respuestas:**
- **200 OK:** Negocio actualizado exitosamente
- **400 Bad Request:** Encabezado Authorization faltante
- **403 Forbidden:** Usuario no es el propietario ni ADMIN

**Ejemplo de solicitud:**
```bash
# Habilitar negocio
curl -X PATCH "http://localhost:8080/api/v1/businesses/550e8400-e29b-41d4-a716-446655440000?enabled=true" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

# Deshabilitar negocio
curl -X PATCH "http://localhost:8080/api/v1/businesses/550e8400-e29b-41d4-a716-446655440000?enabled=false" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

**Notas:**
- Solo el BUSINESS que creó el negocio o un usuario con rol ADMIN pueden habilitar/deshabilitarlo
- Los negocios deshabilitados no aparecen en las búsquedas de turistas