# Expediente digital por empleado

La ruta `/#/th-files?idpersonal=2` conserva el empleado solicitado y abre una vista nueva de expediente digital. El registro anterior basado solamente en nombre y ruta se amplía con adjuntos y metadatos.

## Opciones disponibles

- Resumen del empleado e indicadores calculados con sus documentos: total, activos sin vencer, próximos a vencer en 30 días y archivados.
- Carpetas por categoría: contratos, identificación, títulos, certificados, hoja de vida, acciones de personal, vacaciones/permisos, evaluaciones, capacitaciones, documentos médicos y otros. Son categorías documentales; no crean automáticamente solicitudes ni evaluaciones en otros módulos.
- Búsqueda por nombre, número, emisor u observaciones; filtros por estado y vencimiento; paginación de nueve documentos por página.
- Formulario de registro con dos orígenes: **archivo adjunto** o **enlace/ruta de referencia**.
- Nombre, categoría, número, emisor, emisión, vencimiento opcional, versión y observaciones.
- Detalle del documento, descarga autenticada de adjuntos y apertura de referencias HTTP/HTTPS.
- Archivar y restaurar sin borrar archivos. No se incorporó eliminación definitiva.
- Diseño adaptable a escritorio y móvil; formulario con foco inicial, navegación de teclado, cierre con Escape y protección frente a cierres durante el guardado.

Las referencias antiguas con origen nulo se mantienen como referencias y conservan su ruta. No se convierten automáticamente en archivos adjuntos ni se intenta leer rutas arbitrarias del servidor.

## Archivos adjuntos

Se admiten PDF, JPG/JPEG, PNG, DOCX y XLSX de hasta **10 MB por documento**. Se comprueban extensión, tamaño y cabecera básica del contenido. Esto no equivale a un análisis antivirus ni a validar internamente todos los documentos de Office.

Se reutiliza `StorageService` y el proveedor configurado en cada entorno (Nextcloud o local), sin nuevas dependencias. Los adjuntos se guardan bajo:

```text
RRHH/EXPEDIENTES/{idpersonal}/{UUID}/...
```

El backend registra nombre original, tamaño, tipo y hash SHA-256. Esos datos y el origen del documento se calculan en el servidor. Una ruta de referencia enviada por el cliente no habilita la descarga desde almacenamiento.

La creación del registro y su auditoría comparten la transacción de base de datos. Si esta revierte después de crear un adjunto con ruta conocida, se intenta limpiar ese archivo nuevo. Un fallo del proveedor antes de devolver su ruta o un fallo de esa limpieza pueden requerir conciliación; no se afirma una transacción distribuida entre PostgreSQL y Nextcloud.

El número de versión es un metadato manual, entre 1 y 9999. Cada registro es independiente: no se implementó reemplazo automático de binarios, agrupación de revisiones ni numeración automática. Así se evita sobrescribir documentos históricos al registrar uno nuevo.

## Permisos y auditoría

Las operaciones comprueban usuario WEB activo y acceso al módulo 5, ventana `th-files`: permiso 1 para consultar/descargar y 2 para registrar/archivar/restaurar. Se conserva la excepción del administrador activo con ID 1. Son permisos de la ventana para consultar expedientes; no se agregaron restricciones diferenciadas por categoría médica o por empleado.

El actor proviene de `jwtUserId`, no de un `usucrea` enviado por el navegador. Se registra auditoría de creación y cambio de estado. La tabla no se modifica desde la consulta y las respuestas de documentos personales incluyen `Cache-Control: no-store`.

La descarga recibe un ID de documento y comprueba el permiso antes de buscarlo. Solo usa rutas de adjuntos creadas dentro de la carpeta del empleado. No acepta una ruta libre de descarga y envía `Content-Disposition: attachment` y `X-Content-Type-Options: nosniff`.

## API

| Operación | Ruta |
|---|---|
| Permisos efectivos | `GET /api/th-files/permissions` |
| Documentos de un empleado | `GET /api/th-files/persona/{idpersonal}` |
| Registrar referencia | `POST /api/th-files` con JSON |
| Registrar adjunto | `POST /api/th-files/upload` con multipart: `metadata` JSON y `file` |
| Descargar adjunto | `GET /api/th-files/{id}/download` |
| Archivar/restaurar | `PATCH /api/th-files/{id}/estado`, JSON `{"estado":"ARCHIVADO"}` o `{"estado":"ACTIVO"}` |

La consulta por empleado conserva el contrato de lista anterior para la ficha individual. Los registros se ordenan por ID descendente en la pantalla, sin mezclar respuestas pendientes si se cambia de empleado.

## Migración y puesta en marcha

Aplicar **antes de ejecutar el backend actualizado**:

`src/main/resources/sql/2026-10-15_th_employee_files_metadata.sql`

Añade nueve columnas opcionales a `th_employee_files`: descripción, número, emisor, dos fechas y cuatro metadatos del adjunto. La operación es transaccional, repetible y mantiene los registros existentes. Requiere la tabla base de expedientes; no sustituye su migración inicial.

El proyecto no ejecuta automáticamente estos SQL al desplegar. Para usar la nueva pantalla hacen falta el esquema actualizado y el backend nuevo. Los permisos de la ventana deben estar habilitados para los usuarios correspondientes. El proveedor de almacenamiento debe estar configurado y disponible para cargar/descargar adjuntos; las referencias no escriben archivos en él.

No se aplicó esta migración a producción ni se modificó Nextcloud real durante el trabajo. Tampoco se verificaron credenciales o disponibilidad de almacenamiento mediante una carga real.

## Verificación

- **16 pruebas backend**: permisos, actor, registro de referencia y multipart, hash/ruta, rechazo de archivos o metadatos inválidos, limpieza tras fallo/rollback, archivo lógico y restricciones de descarga.
- **7 pruebas frontend**: empleado correcto, filtros/indicadores, respuestas tardías, carga y doble envío, referencias, conservación del formulario al fallar, validación, permisos, enlaces seguros y renderizado.
- Migración real ejecutada dos veces en PostgreSQL temporal, conservando un documento anterior y sus metadatos, y comprobando su FK al empleado.
- Compilación Angular de desarrollo correcta.
- Vistas del componente real renderizadas con datos sintéticos a 1440 px y 430 px, además del formulario en escritorio y móvil. A 430 px, el ancho del contenido es 430 px: sin desbordamiento horizontal. No es una prueba de extremo a extremo contra el backend o Nextcloud reales.

Comandos backend:

```powershell
mvn '-Dtest=ThEmployeeFile*Test' test
./scripts/test-th-files-postgres.ps1
```

Desde `erp_epmapat_fe`:

```powershell
npx ng test --configuration=rrhh --ts-config=tsconfig.th-files-spec.json --watch=false --browsers=ChromeHeadless --include=src/app/componentes/rrhh/th-files/th-files.component.spec.ts
npx ng build --configuration development --progress=false
```

El script PostgreSQL usa un clúster temporal en `127.0.0.1:55443/employee_files_test`, no lee credenciales de producción y detiene la instancia al terminar.

Evidencias locales bajo `target/`: `th-files-tests.log`, `th-files-frontend-tests.log`, `th-files-postgres.log`, `th-files-build.log` y `th-files-visual/`. Las imágenes y HTML de revisión usan datos de prueba, no documentos de empleados reales.
