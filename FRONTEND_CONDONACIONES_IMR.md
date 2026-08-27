# Integracion Frontend - Condonaciones I/M/R

Este backend ya expone el flujo de registro, consulta, aprobacion y rechazo de solicitudes de exoneracion de intereses y multas en `condmultasintereses`.

## Base URL

Usar la misma base del backend actual, por ejemplo:

```text
http://localhost:9080
```

## Endpoints disponibles

### 1. Registrar solicitud

```http
POST /condmultasintereses?idusuario={idusuario}
Content-Type: application/json
```

Body:

```json
{
  "razoncondonacion": "Exoneracion por resolucion administrativa",
  "items": [
    {
      "idfactura": 1201,
      "totalinteres": 14.25,
      "totalmultas": 0
    },
    {
      "idfactura": 1202,
      "totalinteres": 0,
      "totalmultas": 2.35
    }
  ]
}
```

### 2. Listar solicitudes

```http
GET /condmultasintereses
```

Filtros soportados:

```text
estado
idfactura
idcliente
cuenta
nrofactura
usucrea
feccreaDesde
feccreaHasta
```

Ejemplo:

```http
GET /condmultasintereses?estado=PENDIENTE&cuenta=3012&feccreaDesde=2026-08-01&feccreaHasta=2026-08-27
```

### 3. Pendientes de aprobacion

```http
GET /condmultasintereses/pendientes
```

### 4. Detalle

```http
GET /condmultasintereses/{id}
```

### 5. Aprobar

```http
PUT /condmultasintereses/{id}/aprobar?idusuario={idusuario}
Content-Type: application/json
```

Body opcional:

```json
{
  "observacion": "Aprobado por revision documental"
}
```

### 6. Rechazar

```http
PUT /condmultasintereses/{id}/rechazar?idusuario={idusuario}
Content-Type: application/json
```

Body:

```json
{
  "observacion": "El documento de respaldo no corresponde"
}
```

## Flujo sugerido en frontend

## Pantalla 1: Registro de solicitud

### Paso 1

Buscar abonado o cuenta usando la fuente ya existente del sistema.

### Paso 2

Consultar facturas candidatas con los endpoints ya existentes:

```http
GET /facturas/remisiones?idcliente={idcliente}&fechatope={yyyy-MM-dd}
GET /facturas/remisiones/cuenta?idcliente={idcliente}&cuenta={cuenta}&fechatope={yyyy-MM-dd}
```

### Paso 3

Mostrar tabla con:

```text
Seleccion
Factura
Fecha
Capital o subtotal
Interes
Multa
Total
Estado
```

### Paso 4

Permitir seleccion multiple y construir el payload `items`.

Regla recomendada de UI:

- Si una factura tiene interes mayor a cero, habilitar interes.
- Si una factura tiene multa mayor a cero, habilitar multa.
- Si ambos valores son cero, no permitir seleccionarla.

### Paso 5

Antes de enviar, mostrar confirmacion:

```text
Esta accion registrara la solicitud para revision y aprobacion. La exoneracion todavia no sera aplicada hasta que un segundo usuario autorizado la apruebe.
```

### Paso 6

Enviar `POST /condmultasintereses?idusuario={idusuarioLogueado}`.

## Pantalla 2: Bandeja de pendientes

Consumir:

```http
GET /condmultasintereses/pendientes
```

O si necesitas filtros:

```http
GET /condmultasintereses?estado=PENDIENTE&cuenta={cuenta}
```

Columnas sugeridas:

```text
Id solicitud
Factura
Cliente
Cuenta
Interes
Multa
Razon
Usuario creador
Fecha creacion
Estado
```

## Pantalla 3: Detalle y decision

Al abrir un registro:

```http
GET /condmultasintereses/{id}
```

Mostrar:

```text
Factura
Numero de factura
Cliente
Cuenta
Valor interes
Valor multa
Razon de condonacion
Usuario creador
Fecha de creacion
Estado
Observacion de aprobacion o rechazo
```

Botones:

```text
APROBAR
RECHAZAR
```

Confirmacion antes de aprobar:

```text
Esta a punto de aprobar definitivamente esta exoneracion. Una vez aprobada, las facturas seleccionadas quedaran configuradas para no cobrar los intereses y/o multas correspondientes en Recaudacion.
```

## Validaciones de UI recomendadas

- No enviar una solicitud sin `razoncondonacion`.
- No enviar sin al menos un item.
- No permitir valores negativos.
- No permitir aprobar o rechazar sin `idusuario`.
- En rechazo, hacer obligatoria la observacion.
- Si el backend responde que el creador no puede aprobar, mostrar el mensaje tal cual.
- Si el backend responde que la factura ya fue pagada o la solicitud ya fue procesada, refrescar el listado.

## Respuesta esperada del backend

Ejemplo de item de respuesta:

```json
{
  "idcondmultainteres": 15,
  "idfactura": 1201,
  "nrofactura": "001-001-000000120",
  "idcliente": 88,
  "abonado": "Juan Perez",
  "cuenta": 3012,
  "totalinteres": 14.25,
  "totalmultas": 0.00,
  "razoncondonacion": "Exoneracion por resolucion administrativa",
  "estado": "PENDIENTE",
  "usucrea": 5,
  "usuarioCreador": "jperez",
  "feccrea": "2026-08-27T10:15:30",
  "idusaprueba": null,
  "usuarioAprueba": null,
  "fecaprobacion": null,
  "observacionAprobacion": null,
  "fechaFactura": "2026-07-31"
}
```

## Observacion importante

En este workspace no existe el frontend, por eso no se aplicaron cambios de UI directamente el 27 de agosto de 2026. Esta guia queda lista para implementarse en el proyecto frontend correspondiente.
