# Estado actual del módulo de reclamos

Fecha de revisión: 13 de noviembre de 2026.

## Alcance y criterio de revisión

Documento elaborado mediante revisión estática del código fuente del backend `erp_epmapat_be`, en `src/main`. Describe funciones implementadas y limitaciones visibles; no certifica el funcionamiento en producción. No se ejecutaron la aplicación, consultas a la base de datos ni pruebas HTTP. No incluye validación del frontend.

## Resumen del estado

El módulo dispone de una implementación básica de creación, consulta, actualización y eliminación (CRUD) de reclamos y de su catálogo de tipos. Usa controladores REST, servicios y repositorios Spring Data JPA.

El modelo permite almacenar información de asignación, contestación, terminación y notificación. Sin embargo, las operaciones revisadas se limitan a guardar los valores recibidos: no implementan un flujo de atención con transiciones de estado, plazos calculados, envío de notificaciones o historial de actuaciones.

| Capacidad | Estado observado en el código |
| --- | --- |
| Registrar, listar, consultar y actualizar reclamos | Implementado mediante API REST |
| Eliminar reclamos | Eliminación delegada al repositorio; respuesta booleana defectuosa |
| Administrar tipos de reclamo | CRUD implementado; mismo defecto en respuesta de eliminación |
| Registrar responsable y fecha de asignación | Campos disponibles; actualización manual mediante PUT |
| Registrar contestación y fecha de respuesta | Campos disponibles; actualización manual mediante PUT |
| Registrar terminación del reclamo | Campo disponible; sin operación específica de cierre |
| Registrar datos de notificación | Campos disponibles; sin envío automático identificado |
| Consultar por estado, fechas, responsable o tipo | Sin endpoints específicos identificados |
| Paginación y ordenamiento del listado | No expuestos; el listado recupera todos los registros |
| Historial de cambios y actuaciones | Sin implementación específica identificada |
| Pruebas automatizadas del módulo | No se identificaron pruebas específicas en `src/test` |

## Funciones actuales de reclamos

Rutas relativas declaradas en `ReclamosApi`; cualquier prefijo de despliegue debe verificarse en el entorno correspondiente.

| Método | Ruta | Función | Respuesta declarada o comportamiento |
| --- | --- | --- | --- |
| GET | `/reclamos` | Listar todos los reclamos | 200 y lista de entidades; sin filtros ni paginación |
| POST | `/reclamos` | Guardar un reclamo | 201 y entidad guardada; delega directamente a `save` |
| GET | `/reclamos/{idreclamo}` | Consultar por identificador | 200 con entidad; 404 si no existe |
| PUT | `/reclamos/{idreclamo}` | Actualizar un reclamo existente | 200 con entidad actualizada; 404 si no existe |
| DELETE | `/reclamos/{idreclamo}` | Eliminar por identificador | Delega a `deleteById`; si completa normalmente, devuelve 200 con `false` por el defecto descrito más adelante |

### Registro

`saveReclamos` recibe directamente una entidad `Reclamos` en el cuerpo de la solicitud y la entrega a `ReclamosS.save`, que delega al repositorio. El identificador usa `GenerationType.IDENTITY`.

El POST no exige explícitamente que el identificador llegue vacío. Por tanto, no separa mediante validaciones propias la creación de una posible actualización por `save` cuando se recibe un identificador; el resultado concreto depende de JPA y de los datos existentes.

### Actualización y atención

`updateReclamos` busca primero el registro y copia todos los campos de negocio y auditoría recibidos, manteniendo el identificador de la ruta. Permite modificar:

- Descripción y referencia: `observacion`, `referencia`.
- Asignación: `fechaasignacion`, `responsablereclamo`, `estado`.
- Ubicación: `referenciadireccion`, `piso`, `departamento`.
- Respuesta: `fechamaxcontesta`, `fechacontesta`, `contestacion`.
- Terminación: `fechaterminacion`.
- Módulo y clasificación: `modulo`, `idmodulo_modulos`, `idtpreclamo_tpreclamo`.
- Notificación: `notificacion`, `estadonotificacion`.
- Auditoría: `usucrea`, `feccrea`, `usumodi`, `fecmodi`.

La actualización no es parcial: un campo omitido que se deserialice como `null` se copia al registro y puede borrar su valor previo o provocar un rechazo de persistencia según las restricciones de la base de datos. No existe un endpoint PATCH en este controlador.

## Datos del reclamo

Entidad: `Reclamos`. Tabla: `reclamos`.

| Campo | Tipo Java | Uso observable |
| --- | --- | --- |
| `idreclamo` | Long | Clave primaria autogenerada |
| `observacion` | String | Texto del reclamo u observación |
| `referencia` | Long | Referencia numérica; sin relación JPA ni significado de negocio definido en esta entidad |
| `fechaasignacion` | Date | Fecha de asignación |
| `estado` | Long | Estado numérico; sin catálogo o enumeración definido en el módulo revisado |
| `referenciadireccion` | String | Referencia de dirección |
| `piso` | String | Piso |
| `departamento` | String | Departamento; su significado funcional no está especificado en el código |
| `fechamaxcontesta` | Date | Fecha límite de contestación |
| `fechacontesta` | Date | Fecha de contestación |
| `contestacion` | String | Texto de respuesta |
| `fechaterminacion` | Date | Fecha de terminación |
| `responsablereclamo` | String | Responsable almacenado como texto; sin relación JPA a usuario |
| `modulo` | String | Módulo almacenado como texto |
| `notificacion` | String | Información textual de notificación |
| `estadonotificacion` | Long | Estado numérico de notificación; sin valores documentados en el módulo |
| `idtpreclamo_tpreclamo` | TpReclamoM | Relación ManyToOne LAZY con el tipo de reclamo |
| `idmodulo_modulos` | Modulos | Relación ManyToOne LAZY con el módulo |
| `usucrea`, `usumodi` | Long | Identificadores de usuario de creación y modificación |
| `feccrea`, `fecmodi` | Date | Fechas de creación y modificación |

Todas las fechas de `Reclamos` tienen `@Temporal(TemporalType.DATE)`: su mapeo de persistencia es de fecha, sin hora. El formato JSON efectivo debe comprobarse con la configuración de serialización del entorno; `@DateTimeFormat` por sí solo no establece un contrato JSON.

No se observa una relación JPA directa desde `Reclamos` hacia cliente, abonado, cuenta o factura. No debe asumirse que `referencia` representa alguna de estas entidades sin confirmar la convención utilizada por los consumidores.

## Catálogo de tipos de reclamo

El catálogo permite administrar una descripción y sus campos de auditoría en la tabla `tpreclamo`.

| Método | Ruta | Función | Respuesta declarada o comportamiento |
| --- | --- | --- | --- |
| GET | `/tpreclamo` | Listar tipos | 200 con lista completa |
| POST | `/tpreclamo` | Guardar tipo | 200 con entidad guardada; delega a `save` |
| GET | `/tpreclamo/{idtpreclamo}` | Consultar tipo por ID | 200; 404 si no existe |
| PUT | `/tpreclamo/{idtpreclamo}` | Actualizar descripción y auditoría | 200; 404 si no existe |
| DELETE | `/tpreclamo/{idtpreclamo}` | Eliminar tipo | Delega al repositorio; respuesta booleana defectuosa |

Campos: `idtpreclamo`, `descripcion`, `usucrea`, `feccrea`, `usumodi`, `fecmodi`. No se observan reglas propias que impidan eliminar un tipo utilizado; las restricciones existentes en la base de datos deben verificarse.

Hay dos entidades que mapean la misma tabla: `Tpreclamo` es utilizada por el CRUD del catálogo y `TpReclamoM` por la relación del reclamo. Sus fechas tienen anotaciones diferentes: `TpReclamoM` declara `@Temporal(DATE)` y `Tpreclamo` no lo declara. Esta duplicidad requiere revisión para mantener consistente el mapeo.

## Servicios y persistencia

Los repositorios `ReclamosR` y `TpreclamoR` extienden `JpaRepository` y no declaran consultas personalizadas.

- `ReclamosS` delega realmente `findAll()`, `save(...)`, `findById(...)`, `deleteById(...)` y `delete(...)` al repositorio.
- `TpreclamoServicio` delega realmente `findAll()`, `save(...)`, `findById(...)` y `deleteById(...)`.
- Ambos servicios implementan la interfaz completa de sus repositorios, pero dejan los demás métodos con retornos constantes (`null`, `false`, `0`, o un Optional vacío en un caso) o sin operaciones. Esos métodos no deben considerarse capacidades implementadas del servicio.

## Hallazgos y limitaciones

### 1. Respuesta incorrecta al eliminar

Ambos controladores usan una expresión equivalente a:

```java
return ResponseEntity.ok(!(servicio.findById(id) != null));
```

`findById` devuelve un `Optional`, que normalmente no es `null` aunque no exista el registro. Por eso, si la eliminación y la consulta posterior terminan sin excepciones, la respuesta será `false` incluso cuando se haya eliminado correctamente. Además, no se hace una comprobación explícita previa para responder 404 en DELETE.

### 2. Sin reglas explícitas de atención

No se identificaron validaciones de transiciones de estado, campos obligatorios por etapa, coherencia entre fechas, cálculo del vencimiento, asignación automática o bloqueo de modificaciones después del cierre. Los valores de `estado` y `estadonotificacion` no tienen un significado numérico definido en las clases revisadas.

### 3. Validación y auditoría recibidas del cliente

Los controladores reciben entidades directamente, sin DTO ni `@Valid`, y las entidades del módulo no declaran restricciones Bean Validation. Las fechas y usuarios de auditoría se reciben del cliente; incluso los datos de creación se sobrescriben en PUT. No se observa generación automática de auditoría ni un historial en estos componentes. Esto no descarta restricciones adicionales en la base de datos.

### 4. Datos de notificación sin proceso de envío

Los campos `notificacion` y `estadonotificacion` se almacenan, pero no se identificó integración específica del módulo para enviar correo, SMS u otra comunicación, ni para registrar intentos o confirmaciones de entrega.

### 5. Consultas y funciones pendientes

No se identificaron endpoints propios de búsqueda por referencia, responsable, tipo, estado o fechas; tampoco de vencidos, indicadores, reportes, adjuntos o seguimiento de actuaciones. La búsqueda textual en `src` por reclamo no mostró otras implementaciones específicas de estas funciones.

### 6. Verificación de integración pendiente

Las entidades se devuelven directamente y `Reclamos` contiene relaciones LAZY. Debe verificarse el JSON efectivo y la carga de relaciones en pruebas HTTP. Los controladores revisados no declaran autorización por roles; este documento no certifica la seguridad global del sistema ni la de su infraestructura.

## Pendientes recomendados

1. Corregir la respuesta de eliminación y definir el comportamiento para IDs inexistentes o tipos utilizados.
2. Acordar y documentar los estados y transiciones del reclamo y de la notificación.
3. Incorporar validaciones de entrada y de fechas; gestionar la auditoría desde el servidor.
4. Simplificar los servicios para evitar métodos de repositorio expuestos sin implementación y unificar las entidades de tipo de reclamo.
5. Incorporar filtros y paginación según las necesidades de atención.
6. Si el proceso lo requiere, implementar historial, notificaciones, adjuntos e indicadores.
7. Validar con pruebas de integración el CRUD, los errores, las relaciones y el flujo de atención que se defina.

## Archivos fuente revisados

Rutas relativas a este documento:

- [ReclamosApi.java](../src/main/java/com/epmapat/erp_epmapat/controlador/ReclamosApi.java)
- [ReclamosS.java](../src/main/java/com/epmapat/erp_epmapat/servicio/ReclamosS.java)
- [ReclamosR.java](../src/main/java/com/epmapat/erp_epmapat/repositorio/ReclamosR.java)
- [Reclamos.java](../src/main/java/com/epmapat/erp_epmapat/modelo/Reclamos.java)
- [TpreclamoApi.java](../src/main/java/com/epmapat/erp_epmapat/controlador/TpreclamoApi.java)
- [TpreclamoServicio.java](../src/main/java/com/epmapat/erp_epmapat/servicio/TpreclamoServicio.java)
- [TpreclamoR.java](../src/main/java/com/epmapat/erp_epmapat/repositorio/TpreclamoR.java)
- [Tpreclamo.java](../src/main/java/com/epmapat/erp_epmapat/modelo/Tpreclamo.java)
- [TpReclamoM.java](../src/main/java/com/epmapat/erp_epmapat/modelo/TpReclamoM.java)
- [ResourceNotFoundExcepciones.java](../src/main/java/com/epmapat/erp_epmapat/excepciones/ResourceNotFoundExcepciones.java)
