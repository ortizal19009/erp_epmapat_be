# Matriz de estados - Gestión Documental

## Estados principales
- BORRADOR
- EN_REVISION
- EMITIDO
- DERIVADO
- RECIBIDO
- ARCHIVADO
- ANULADO

## Reglas aplicadas en backend (WorkflowDocumentosService)

### Emitir
- Permitidos: `BORRADOR`, `EN_REVISION`
- Resultado: `EMITIDO`

### Recibir
- Permitidos: `EMITIDO`, `DERIVADO`, `RECIBIDO`
- Resultado: `RECIBIDO`

### Derivar
- Permitidos: `RECIBIDO`, `EN_REVISION`, `DERIVADO`
- Resultado: `DERIVADO` (si estaba `RECIBIDO` o `EN_REVISION`)

### Atender derivación
- Permitidos: `DERIVADO`, `EN_REVISION`, `RECIBIDO`
- Resultado documento: `EN_REVISION` (si estaba `DERIVADO` o `RECIBIDO`)
- Resultado derivación: `EN_GESTION`

### Responder derivación
- Permitidos: `DERIVADO`, `EN_REVISION`, `RECIBIDO`
- Resultado derivación: `RESPONDIDO` (con `respondido_en`, `cerrado_en`)
- Resultado documento: `estado_respuesta = RESPONDIDO`

## Nota
Estas reglas reducen transiciones inválidas y dejan una base para la siguiente fase: auditoría y trazabilidad completa de actor/rol en cada evento.