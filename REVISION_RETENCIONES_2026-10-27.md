# Revision e integracion de retenciones ? 27 de octubre de 2026

## Resultado

Se revisaron los tres proyectos: ERP Angular, ERP Spring Boot y sri-files.
Se corrigio el codigo local de frontend y backend para usar el contrato que publica
sri-files en `http://192.168.0.33:9096`. No se desplegaron cambios.
Los cambios previos en docker-compose.yml y application.yml de los microservicios se conservaron.

## Problemas encontrados y correcciones

- La pantalla procesaba directamente contra sri-files y omitia la persistencia del ERP.
  Ahora las acciones individual y masiva invocan `POST /api/sri/retenciones/procesar?idretencion=...`.
- El ERP invocaba rutas antiguas `/api/singsend/retencion/string` y
  `/api/singsend/retenciones/xml`, ausentes del OpenAPI desplegado.
  Ahora envia XML a `POST /api/v1/retenciones` y recupera comprobante y metadatos
  mediante `GET /api/v1/autorizacion/{clave}?includeXml=true`.
- La consulta de la pantalla solo cambiaba valores en memoria. Ahora usa
  `POST /api/sri/retenciones/consultar?idretencion=...`, que guarda autorizacion y XML en el ERP.
- Las descargas esperaban XML/PDF de una respuesta que solo devolvia metadatos.
  El PDF usa el endpoint del ERP existente; el XML usa el nuevo
  `GET /api/sri/retenciones/xml-autorizado?idretencion=...`.
- El XML completo se conserva con estado, numero, fecha, ambiente y comprobante firmado en CDATA.
  Se comprueba que la clave y el tipo del comprobante recuperado correspondan a la retencion.
- Un fallo de PDF/correo despues de autorizar devuelve `emailEnviado=false` con detalle,
  sin convertir el resultado fiscal en un fracaso de emision. El reenvio es una accion separada.
- El reenvio exige XML autorizado existente y ya no regenera XML plano ni reinicia el estado fiscal.
- Se protege la regeneracion de XML y clave de retenciones con estado autorizado.
- El destinatario explicito sustituye los correos registrados; acepta coma y punto y coma.
- Se elimina el registro duplicado de correo en Angular: el backend registra el envio.
- Se refresca la entidad JPA despues del upsert nativo para evitar usar el XML previo en memoria.
- Los eventos SSE actualizan tambien el resultado mostrado; NO_AUTORIZADO no se anuncia como exito.

## Pruebas realizadas

### Red y contrato desplegado

- `GET http://192.168.0.33:9096/api/v1/erp/ping`: HTTP 200, status UP.
- OpenAPI del mismo servicio: confirma las rutas v1 de envio, validacion y autorizacion.
- `POST /api/v1/retenciones/validar` con el fixture golden existente: HTTP 200,
  valid=false; detecta ausencia de impuestos y advierte digito verificador incorrecto.
  Es una prueba negativa de validacion, no una emision.
  requestId: `9a80cbf2-b863-4d85-89a4-cce121d82acb`.
- `http://192.168.0.165:8080/v3/api-docs`: HTTP 200; publica el flujo ERP de
  procesar, consultar, PDF y correo. El nuevo endpoint xml-autorizado requiere desplegar este backend.
- `http://localhost:9080`: sin conexion durante la revision; no se arranco la aplicacion
  para evitar tareas programadas contra la base y documentos reales.

### Automatizadas

- Backend: 8 pruebas aprobadas. Incluyen contrato HTTP simulado, pendiente, rechazo,
  clave ajena, prevencion de reemision autorizada, fallo SMTP posterior a autorizacion,
  XML generado con impuestos, PDF real Jasper leido con PDFBox y preparacion de ambos adjuntos
  para el destinatario explicito. Repositorios, SRI y transporte de correo estan simulados.
- Frontend: 19 pruebas aprobadas en ChromeHeadless. Contratos HTTP, extraccion de errores,
  estados, procesamiento por ID, rechazo sin falso mensaje de exito y habilitacion del PDF.
- sri-files: 17 pruebas existentes aprobadas: RetencionXmlCaracterizacionTest y SriControllerHelperTest.
- Compilacion Java y TypeScript comprobadas. No se modifico el codigo de sri-files porque
  el ERP se adapto a sus rutas existentes.

Comandos desde cada proyecto:

```powershell
# erp_epmapat_be
mvn -q '-Dtest=RetencionDocumentosTest,RetencionProcesamientoSRIServiceTest,RetencionSRIServiceTest' test

# erp_epmapat_fe
npx tsc -p tsconfig.app.json --noEmit
npx ng test --configuration=rrhh --ts-config=tsconfig.retenciones-spec.json --watch=false --browsers=ChromeHeadless '--include=src/app/servicios/contabilidad/retenciones-sri*.spec.ts' '--include=src/app/componentes/contabilidad/retenciones/retenciones/retenciones.component.spec.ts'

# microservicesEpmapa-T/sri-files
mvn -q '-Dtest=RetencionXmlCaracterizacionTest,SriControllerHelperTest' test
```

## Despliegue y validacion operativa pendientes

1. Desplegar primero el backend y luego Angular. Mantener
   `SRI_MICROSERVICE_BASE_URL=http://192.168.0.33:9096` y comprobar que API_URL
   del frontend apunte al backend correspondiente.
2. En ambiente SRI de pruebas, elegir una retencion y validar su XML antes de procesarla.
3. Procesarla una vez; si queda pendiente, usar Consultar autorizacion, que no vuelve a emitir.
4. Confirmar numero, fecha, ambiente y XML autorizado persistidos; recargar la pantalla.
5. Descargar RIDE y XML, contrastar montos y datos con el documento autorizado.
6. Enviar a un buzon de prueba y verificar recepcion de ambos adjuntos y registro de correo.

No se emitieron retenciones reales ni se enviaron correos reales. No se comprobaron
certificado/firma, aceptacion final del SRI, persistencia en PostgreSQL real ni entrega SMTP
con una retencion del usuario. La generacion del RIDE mantiene la plantilla y los datos
contables existentes; debe contrastarse con el XML autorizado durante la validacion operativa.
Las pruebas automatizadas no sustituyen ese recorrido despues del despliegue.


## Ajuste posterior: descarga por clave de acceso

Se corrigio Angular para que Descargar XML use directamente:
`GET http://192.168.0.33:9096/api/v1/autorizacion/{claveAcceso}/xml`.
Este ajuste reemplaza, para la descarga de la pantalla, el uso del endpoint ERP por ID
mencionado arriba. El ID del ERP sigue siendo util para las operaciones contables y
la sincronizacion; la recuperacion documental usa la clave SRI de 49 digitos.

El endpoint devuelve JSON. Angular extrae `xmlAutorizado`, conserva su texto UTF-8
para el archivo y muestra mensaje, detalle y requestId cuando no hay XML disponible.
No consulta usando el ID como si fuera una clave.

En la comprobacion de la clave `2210202607046002881000120010010000069577596000015`,
el servicio respondio HTTP 202, estado PENDIENTE_CONSULTA y xmlAutorizado vacio.
Esto registra el resultado de esa consulta puntual; no demuestra ausencia definitiva de autorizacion.
El error previo HTTP 404 correspondia a la ruta por ID en localhost; no prueba un error de la clave SRI.
Los errores ERR_CONNECTION_RESET/REFUSED del stream SSE pertenecen a otra solicitud al backend.

Validacion del ajuste: 21 pruebas frontend aprobadas en ChromeHeadless, compilacion
TypeScript sin errores y git diff --check correcto. No se desplego Angular ni se emitieron documentos.


## Comparacion ERP / Bruno y timeout

La consulta local `GET /retenciones/156` devolvio secuencial 6937 y clave
`2708202607046002881000120010010000069377396000011`. La clave compartida para
Bruno (`2210202607046002881000120010010000069577596000015`) corresponde al
secuencial 6957: no son la misma retencion.

La repeticion directa de /autorizacion/{clave6957}/xml desde este equipo recibio
HTTP 202 PENDIENTE_CONSULTA con Connect timed out en la dependencia SRI.
La repeticion de POST consultar?idretencion=156 recibio HTTP 500 con timeout remoto;
no reprodujo el HTTP 422 previamente observado. Se necesita el cuerpo de aquel 422
para atribuirle una causa concreta.

Se agrego Accept: application/json a la consulta del backend y a la descarga Angular.
El backend anteriormente recibio un error del microservicio serializado como XML (MapN).
La seleccion de clave de la pantalla usa claves persistidas validas; ya no calcula
una clave para intentar recuperar autorizaciones. Los mensajes de recuperacion muestran
la clave consultada para poder comparar exactamente con Bruno.

Pruebas del ajuste: 5 pruebas de procesamiento backend y 23 frontend aprobadas;
compilacion TypeScript correcta. Cambios locales, requieren reiniciar/recompilar backend
y actualizar frontend. El aviso Angular development mode no causa estos fallos.
Pendiente comparar la URL y los parametros exactos de la solicitud exitosa de Bruno.
