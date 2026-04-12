# Documentación del Turista

## Obtener Elementos por Categoría

Obtiene elementos (rentals, negocios o experiencias) según la categoría especificada, filtrados por la ciudad y país del usuario autenticado.

**URL:** `GET /api/v1/tourist/items`

**Encabezados:**
| Campo | Valor |
|-------|-------|
| Authorization | Bearer {TOKEN_JWT} |

**Parámetros:**
| Campo | Tipo | Obligatorio | Descripción |
|-------|------|-------------|-------------|
| category | String | Sí | Categoría: RENTAL, EXPERIENCE o BUSINESS |

**Respuestas:**
- **200 OK:** Elementos encontrados
- **401 Unauthorized:** Token inválido o usuario no encontrado

**Ejemplo de solicitud:**
```bash
curl -X GET "http://localhost:8080/api/v1/tourist/items?category=RENTAL" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

**Nota:** La ciudad y país se obtienen automáticamente del perfil del usuario autenticado (campos `current_city` y `current_country`).

## Actualizar Ubicación

Actualiza la ciudad y país del usuario autenticado.

**URL:** `PATCH /api/v1/tourist/location`

**Encabezados:**
| Campo | Valor |
|-------|-------|
| Authorization | Bearer {TOKEN_JWT} |
| Content-Type | application/json |

**Cuerpo de la solicitud:**
| Campo | Tipo | Obligatorio | Descripción |
|-------|------|-------------|-------------|
| city | String | Sí | Nueva ciudad |
| country | String | Sí | Nuevo país |

**Respuestas:**
- **200 OK:** Ubicación actualizada exitosamente
- **400 Bad Request:** Datos faltantes o inválidos
- **401 Unauthorized:** Token inválido

**Ejemplo de solicitud:**
```bash
curl -X PATCH "http://localhost:8080/api/v1/tourist/location" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "Content-Type: application/json" \
  -d '{"city": "Barcelona", "country": "ESPAÑA"}'
```
