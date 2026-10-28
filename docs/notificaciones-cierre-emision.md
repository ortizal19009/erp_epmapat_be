# Cierre de rutas: correos y cobertura de apertura

Al cerrar y calcular una ruta desde Lecturas o Emisiones, se guarda primero el cierre y luego se solicita POST /rutasxemision/{id}/notificar-cierre. Los importes se consultan en el backend, no se aceptan importes ni destinatarios enviados por el navegador.

Cada lectura calculada (estado 1, total1 e idfactura registrados) genera un mensaje NOTIFICACION en la cola existente. El correo destaca el valor actual y separa los pendientes anteriores de consumo, servicios y convenios, con sus intereses y total consolidado. Las facturas de la ruta actual se excluyen de los pendientes para evitar sumarlas dos veces.

## Entorno y remitente

- Perfil local (predeterminado) o cualquier perfil distinto de prod/production: todos los destinatarios se reemplazan por alexis.ortiz81@outlook.com. El asunto incluye [PRUEBA]. No se agregan copias ni copias ocultas.
- SPRING_PROFILES_ACTIVE=prod (o production): destinatario exclusivamente idresponsable.email, validado como una sola direccion. Si no es valido se omite. No se sustituye por el correo del titular.
- El entorno lo determina el perfil del backend, independientemente de la direccion IP o hostname usado por el navegador.
- Configurar una cuenta de correo activa predeterminada para NOTIFICACION o una cuenta general predeterminada en la administracion de correos existente. Reutiliza el worker y su configuracion SMTP/API; no se agregan credenciales al codigo.

El resultado informa encolados, duplicados, sinCorreoValido, sinCalcular y bloqueados (lista negra existente). Encolado no significa entregado: el estado y los errores de transporte se consultan en la bandeja de correos existente.

Un bloqueo transaccional por ruta y una correlacion por factura/cuenta/entorno evitan duplicados al repetir la solicitud. Para reintentar la preparacion, usar nuevamente POST /rutasxemision/{id}/notificar-cierre. Los mensajes ya creados no se duplican; los fallos de entrega se gestionan desde la bandeja existente. Si falla la preparacion de correos, el frontend conserva el cierre y muestra un aviso.

## Apertura

Generar/completar y validar la apertura toman las cuentas con estado 1 o 2 (excluye los estados 0 y 3). Incluyen las rutas que tienen esas cuentas aunque la ruta este inactiva. La validacion comprueba la existencia de una lectura por emision/cuenta, sin compensar faltantes con otras lecturas.

La respuesta agrega cuentasPendientes, cuentasSinRuta y aperturaCompleta. Las cuentas sin ruta se muestran para corregir su asignacion; impiden declarar la apertura completa. Despues de asignar una ruta, ejecutar Completar pendientes otra vez. Validar apertura no modifica lecturas ni facturas.

Las pruebas automaticas usan dobles de correo: no envian correos reales ni abren emisiones de la base activa.

Para acelerar la verificacion en bases con historiales grandes, aplicar src/main/resources/sql/2026-10-28_emisiones_cobertura_indexes.sql fuera de una transaccion. El script no modifica datos.

Pruebas del backend: `./mvnw -Dtest=CierreRutaNotificacionServicioTest,EmisionAperturaCoberturaTest test`.
Pruebas del frontend: `npx ng test --configuration=emisiones --watch=false --browsers=ChromeHeadless --include=src/app/servicios/rutasxemision.service.spec.ts --include=src/app/componentes/rutas/rutas/rutas.component.spec.ts`.

## Plantilla institucional

HTML editable: src/main/resources/templates/emails/cierre-ruta.html. Se renderiza mediante Thymeleaf al crear el mensaje en la cola. Vista previa con datos ficticios: docs/previews/cierre-ruta-ejemplo.html. Los mensajes que ya estaban encolados conservan su cuerpo anterior.

Logo publico HTTPS tomado del sitio institucional. Los datos de contacto se verificaron el 28 de octubre de 2026 en https://epmapatulcan.gob.ec/contactenos/ y el horario de la matriz en https://epmapatulcan.gob.ec/puntos-de-recaudacion/.

Propiedades opcionales para actualizar la identidad y el contacto sin modificar el Java:
- notificaciones.cierre.logo-url
- notificaciones.cierre.contacto.direccion
- notificaciones.cierre.contacto.telefono
- notificaciones.cierre.contacto.telefono-url (por ejemplo tel:+59362980021)
- notificaciones.cierre.contacto.email
- notificaciones.cierre.contacto.horario

Pruebas adicionales de renderizado y vista previa: ./mvnw -Dtest=CierreRutaCorreoPlantillaTest test.
