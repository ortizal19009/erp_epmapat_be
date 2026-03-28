# Paridad Python -> Java (Gestión Documental)

Fecha: 2026-03-02
Módulo Java: `erp_epmapat_v2.0/gestiondocumental`

## 1) Endpoints Python detectados (api/*.py)

Base Python observada:
- `documentos_api.py`
- `emision_api.py`
- `recepcion_api.py`

Endpoints:
- `GET /documents/dashboard`
- `GET /documents/{doc_id}`
- `PUT /documents/{doc_id}`
- `DELETE /documents/{doc_id}`
- `POST /documents/{doc_id}/issue`
- `POST /documents/{doc_id}/receive`
- `GET /documents/receptions/pending`

## 2) Endpoints Java disponibles

### Documentos
Base Java:
- `/api/documents`
- alias `/api/documentos`

Incluye:
- `GET /dashboard`
- `GET /{docId}`
- `PUT /{docId}`
- `DELETE /{docId}`
- `POST /{docId}/issue` (alias `/emitir`)
- `POST /{docId}/receive` (alias `/recibir`)
- `GET /receptions/pending` (alias `/recepciones/pendientes`)

### Más allá de Python base (extendido en Java)
- Catálogos: entidades/dependencias/tipos/lookups
- Workflow: assign/derive/bulk/read/responses/timeline/relations
- Files: add/list/get metadata
- Alerts: generate/list/dispatch
- Configuración: system-settings, CCD, TRD, case-files

## 3) Matriz de paridad mínima (Python -> Java)

- `GET /documents/dashboard` -> `GET /api/documents/dashboard` ✅
- `GET /documents/{doc_id}` -> `GET /api/documents/{docId}` ✅
- `PUT /documents/{doc_id}` -> `PUT /api/documents/{docId}` ✅
- `DELETE /documents/{doc_id}` -> `DELETE /api/documents/{docId}` ✅
- `POST /documents/{doc_id}/issue` -> `POST /api/documents/{docId}/issue` ✅
- `POST /documents/{doc_id}/receive` -> `POST /api/documents/{docId}/receive` ✅
- `GET /documents/receptions/pending` -> `GET /api/documents/receptions/pending` ✅

## 4) Estado técnico actual

- Compilación Java (`mvn -pl gestiondocumental -DskipTests compile`): ✅
- Arquitectura por capas aplicada:
  - `controller/` ✅
  - `service/` ✅
  - `model/repository/dto` ✅ (catálogo)

## 5) Smoke E2E sobre GesDoc (local :9095)

Condiciones de prueba:
- app levantada con `SPRING_CLOUD_CONFIG_ENABLED=false`, `EUREKA_CLIENT_ENABLED=false`
- datasource directo a `jdbc:postgresql://localhost:5432/GesDoc`

Resultados:
- `POST /api/documentos/{docId}/asignar` ✅
- `POST /api/documentos/{docId}/derivar` ✅
- `POST /api/documentos/alerts/generate` ✅
- `POST /api/documentos/alerts/dispatch` ✅
- `GET /api/lookups/usuarios` ✅ (corregido; antes 500)
- `POST /api/documentos/{docId}/emitir` ✅ (tras crear `series_numeracion` para tipo/dependencia/año)
- `POST /api/documentos/{docId}/recibir` ✅ (usando `receiver_id=persona_id`)

Observación funcional importante:
- `recibir` espera `receiver_id` de **persona** (tabla `personas.id`), no `usuarios.id`.

## 6) Pendientes

1. cubrir con el mismo patrón por capas (model/repository/dto) los bloques de documentos/workflow/configuración en profundidad total.
2. conectar por gateway con rutas finales y validar con frontend Angular legado.
