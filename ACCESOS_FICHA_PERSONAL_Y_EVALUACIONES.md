# Accesos de la ficha y evaluaciones reales

Implementación del 15 de octubre de 2026.

## Comportamiento

La ficha `/#/info-personal` ofrece tres accesos visibles. Cada botón se habilita únicamente en su pestaña correspondiente; en Resumen, Historial y Usuario los tres permanecen deshabilitados. Se eliminaron los botones duplicados dentro de los paneles.

- **Registrar vacaciones** abre `/th-leave?idpersonal=...` con el empleado seleccionado y reutiliza las solicitudes, saldos y permisos existentes.
- **Añadir documento** abre `/th-files?idpersonal=...` con el mismo empleado y reutiliza el formulario de registro del expediente.
- **Registrar evaluación** abre `/th-performance?idpersonal=...`, ahora conectado a un registro real y un historial paginado.

Las pantallas permiten volver a la ficha. La ficha admite `idpersonal` en la URL y conserva compatibilidad con `idpersonalToInfo` en la sesión. Un enlace con un empleado inexistente o inválido no selecciona silenciosamente el primer empleado de la lista. La entrada general sin parámetro conserva la selección inicial existente.

El registro de documentos continúa usando nombre, ruta y metadatos del formulario actual: este cambio no agrega un servicio nuevo de carga binaria de archivos. La ficha muestra los nombres y fechas usando los campos reales del backend y elimina el puntaje ficticio de 92%.

## Evaluaciones

La pantalla de desempeño reemplaza los indicadores estáticos por:

- Selección del empleado.
- Período libre de hasta 40 caracteres, fecha y evaluador.
- Puntaje global manual entre 0 y 100, con hasta dos decimales.
- Observaciones obligatorias y plan de mejora opcional, hasta 2.000 caracteres cada uno.
- Historial del empleado, ordenado por fecha y número de registro descendentes, paginado de 20 en 20.
- Confirmación de guardado, prevención de doble envío mientras hay una petición y conservación del formulario si el servidor falla.

Este primer registro no calcula rúbricas ponderadas, no firma ni aprueba evaluaciones y no permite modificar o eliminar resultados existentes. Se admiten varias evaluaciones del mismo empleado/período; no se supone un único evaluador o ciclo institucional.

### Integración backend

Se añadió `/api/th-performance`:

| Operación | Endpoint |
|---|---|
| Consultar permisos efectivos | `GET /permissions` |
| Historial del empleado | `GET /persona/{idpersonal}?page=0&size=20` |
| Registrar resultado | `POST /api/th-performance` |

Payload de registro:

```json
{
  "idpersonal": 7,
  "periodo": "2026 - segundo semestre",
  "fecha": "2026-10-15",
  "puntaje": 90.25,
  "evaluador": "Jefatura inmediata",
  "observaciones": "Cumplimiento de objetivos del período.",
  "planMejora": "Seguimiento del siguiente período."
}
```

El usuario que registra y la fecha/hora del registro se obtienen en el servidor, sin aceptar al actor enviado por el cliente. Las evaluaciones se vinculan al catálogo `personal` de la ficha. El registro y su entrada de auditoría `PERSONAL / EVALUACION_REGISTRADA` usan una misma transacción.

El backend comprueba usuario activo y permisos WEB del módulo 5, ventana `th-performance`: nivel 1 para consulta y nivel 2 para registrar, con la excepción administrativa existente del usuario 1 activo. La consulta no usa caché HTTP y la paginación admite hasta 100 resultados por página.

Existe otra API histórica de evaluaciones sobre `rrhh_employees`. Sus identificadores no equivalen necesariamente a `personal.idpersonal`. No se reutilizaron ni se emparejaron por coincidencia numérica. La tabla nueva `th_performance_reviews` mantiene la identidad correcta de esta ficha sin modificar la API histórica.

## Base de datos y puesta en marcha

Script nuevo: `src/main/resources/sql/2026-10-15_th_performance_reviews.sql`.

Crea la tabla e índice del historial, con FK al empleado y restricciones de puntaje/campos obligatorios. El script es transaccional y repetible. No modifica saldos de vacaciones ni las migraciones anteriores de RRHH.

Para usar el registro real, aplicar ese SQL contra la base del entorno elegido y ejecutar el backend actualizado. Los `.sql` en recursos **no se ejecutan automáticamente**, porque el proyecto mantiene `ddl-auto=none` y no se añadió un ejecutor automático. Para producción hace falta una aplicación coordinada de la migración y el backend; no se realizó despliegue ni escritura de producción en este trabajo.

El frontend de desarrollo apunta a `localhost:9080` según `environment.ts`. Un backend anterior o una base sin la migración no ofrecen el nuevo registro. Los botones de vacaciones y documentos reutilizan sus servicios existentes.

## Validación

- Backend: **10 pruebas** de permisos, validación, actor autenticado, empleado inexistente, paginación y auditoría.
- Frontend: **65 pruebas** en ChromeHeadless: 11 de contexto/accesos/evaluaciones y las 54 existentes de vacaciones.
- Compilación Angular de desarrollo correcta.
- PostgreSQL temporal independiente: inserción y consulta de un resultado, repetición de migración sin pérdida, restricciones de puntaje, FK de empleado y observaciones.

Comandos:

```powershell
mvn '-Dtest=ThPerformance*Test' test
./scripts/test-th-performance-postgres.ps1
```

Desde `erp_epmapat_fe`:

```powershell
npx ng test --configuration=rrhh --ts-config=tsconfig.employee-actions-spec.json --watch=false --browsers=ChromeHeadless --include=src/app/componentes/rrhh/shared/personal-context.spec.ts --include=src/app/componentes/rrhh/personal/info-personal/info-personal.component.spec.ts --include=src/app/componentes/rrhh/th-performance/th-performance.component.spec.ts --include=src/app/componentes/rrhh/th-leave/th-leave.component.spec.ts
npx ng build --configuration development --progress=false
```

El script PostgreSQL usa exclusivamente un clúster temporal en `127.0.0.1:55442/performance_test`, con datos sintéticos, y lo detiene al finalizar. No utiliza credenciales de producción.

## Visibilidad de acciones de usuario vinculado

La ficha y el menú del listado muestran exclusivamente Crear usuario si `tieneUsuario=false`, o Resetear contraseña si `tieneUsuario=true`. Si el estado todavía no está disponible, ambas acciones permanecen ocultas. Los métodos también rechazan invocaciones incompatibles y se vuelve a consultar el empleado después de una creación exitosa.

En la ficha individual, la acción correspondiente queda habilitada únicamente al seleccionar la pestaña **Usuario**. Fuera de ella permanece visible y deshabilitada. Los métodos de creación y reseteo comprueban también la pestaña activa y el ID del empleado antes de invocar el servicio. Cinco pruebas de la ficha verifican navegación, bloqueo fuera de Usuario, vínculo compatible y datos incompletos.

Los endpoints de consulta de Personal (listado, búsqueda y detalle) incorporan campos derivados de solo lectura: `tieneUsuario`, `idusuarioVinculado` y `usuarioSistema`. Se resuelven desde `usuarios.personal_idpersonal`, en una consulta por lote, sin exponer contraseñas ni inferir coincidencias por cédula. Una cuenta inactiva también conserva el vínculo. Si hay varias cuentas vinculadas, se informa la primera por ID para mantener una salida determinista y se evita ofrecer crear otra.

Este ajuste no requiere migración SQL; sí ejecutar el backend actualizado para que el frontend reciba esos campos. Controla la visibilidad y las invocaciones del cliente; no incorpora los endpoints de creación o reseteo que la pantalla ya referenciaba y que siguen pendientes en el backend inspeccionado.

Validación adicional: cuatro pruebas backend del vínculo, serialización y consulta por lote; seis pruebas frontend de acciones y accesos; compilación Angular correcta. Evidencia local: `target/personal-user-state-tests.log` y `target/personal-user-state-build.log`.
