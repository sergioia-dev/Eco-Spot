# Documentación del Turista

## Crear Reserva

Crea una nueva reservación para un rental.

**URL:** `POST /api/v1/tourist/rentals/{rentalId}/reservations`

**Encabezados:**
| Campo | Valor |
|-------|-------|
| Authorization | Bearer {TOKEN_JWT} |

**Parámetros de ruta:**
| Campo | Tipo | Descripción |
|-------|------|-------------|
| rentalId | UUID | ID del rental |

**Cuerpo de la solicitud:**
| Campo | Tipo | Descripción |
|-------|------|-------------|
| startingDate | Date | Fecha de inicio (YYYY-MM-DD) |
| endDate | Date | Fecha de fin (YYYY-MM-DD) |

**Respuestas:**
- **201 Created:** Reservación creada
- **400 Bad Request:** Authorization faltante o fechas inválidas
- **403 Forbidden:** Fechas superpuestas con otra reservación o rental no disponible

**Ejemplo de solicitud:**
```bash
curl -X POST "http://localhost:8080/api/v1/tourist/rentals/550e8400-e29b-41d4-a716-446655440000/reservations" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "Content-Type: application/json" \
  -d '{"startingDate": "2026-05-01", "endDate": "2026-05-05"}'
```

**Notas:**
- Las reservaciones son por días (no por horas)
- Las fechas deben ser futuras
- No se pueden crear reservaciones con fechas que superen otras reservaciones existentes
- El host del rental puede cancelar la reservación posteriormente

## Cancelar Reserva

Cancela una reservación existente. Solo el turistas que hizo la reservación puede cancelarla.

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
- Solo el turista que hizo la reservación puede cancelarla
- El host del rental o un ADMIN también pueden cancelar la reservación
- No se puede cancelar una reservación que ya está cancelada
- Las reservaciones canceladas no aparecen en las listas de reservaciones

## Obtener Elementos por Ubicación

Obtiene todos los elementos (rentals, negocios y experiencias) filtrados por la ciudad y país del usuario autenticado.

**URL:** `GET /api/v1/tourist/items`

**Encabezados:**
| Campo | Valor |
|-------|-------|
| Authorization | Bearer {TOKEN_JWT} |

**Respuestas:**
- **200 OK:** Elementos encontrados
- **400 Bad Request:** Token no proporcionado
- **401 Unauthorized:** Token inválido o usuario no encontrado

**Ejemplo de solicitud:**
```bash
curl -X GET "http://localhost:8080/api/v1/tourist/items" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

**Estructura de respuesta:**
```json
{
  "experiences": [
    { "id": "...", "name": "...", "city": "...", "country": "...", ... }
  ],
  "rentals": [
    { "id": "...", "name": "...", "city": "...", "country": "...", ... }
  ],
  "businesses": [
    { "id": "...", "name": "...", "city": "...", "country": "...", ... }
  ]
}
```

**Nota:** 
- La ciudad y país se obtienen automáticamente del perfil del usuario autenticado (campos `current_city` y `current_country`).
- Cada lista contiene un máximo de 10 elementos.

## Buscar Elementos

Busca elementos por nombre. Opcionalmente filtra por categoría.

**URL:** `GET /api/v1/tourist/search`

**Encabezados:**
| Campo | Valor |
|-------|-------|
| Authorization | Bearer {TOKEN_JWT} |

**Parámetros:**
| Campo | Tipo | Obligatorio | Descripción |
|-------|------|-------------|-------------|
| searchBy | String | Sí | Término de búsqueda (busca en nombres) |
| category | String | No | Categoría: RENTAL, BUSINESS o EXPERIENCE. Si se omite, busca en todas |

**Respuestas:**
- **200 OK:** Elementos encontrados
- **400 Bad Request:** searchBy faltante o categoría inválida
- **401 Unauthorized:** Token inválido

**Ejemplo de solicitud (todas las categorías):**
```bash
curl -X GET "http://localhost:8080/api/v1/tourist/search?searchBy=beach" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

**Ejemplo de solicitud (con categoría):**
```bash
curl -X GET "http://localhost:8080/api/v1/tourist/search?category=RENTAL&searchBy=beach" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

**Nota:** Los resultados se ordenan prioritizando los elementos del país del usuario autenticado.
