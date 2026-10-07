# PLAN_MEJORAS_RRHH

**Proyecto:** ERP-EPMAPA-T\
**Módulo:** Recursos Humanos (RRHH)\
**Fecha:** 7 de octubre de 2026\
**Estado:** Planificación técnica y funcional de mejoras\
**Documento base:** [Estado actual del módulo de Recursos Humanos](ESTADO_MODULO_RRHH.md)

**Actualización:** Diez entregas implementadas en backend/frontend para solicitudes, saldos, movimientos, cancelación, reversión, ajustes, conciliación, bandeja, historial, actividad, exportaciones y calendario de aprobaciones. Consulte las secciones 43–52 para conocer pruebas, alcance, migraciones y pendientes de aceptación/despliegue.

**Guía de lectura:** Las secciones 5–26 describen el alcance objetivo; las secciones 29–33 presentan el roadmap y las prioridades; las secciones 36–42 permiten convertir el plan en entregas verificables. Las fases funcionales y las etapas del roadmap son agrupaciones distintas, no cronogramas comprometidos.

------------------------------------------------------------------------

## 1. Propósito

Este documento define la planificación de mejoras del módulo de Recursos
Humanos del ERP-EPMAPA-T. El objetivo es evolucionar el conjunto actual
de funcionalidades hacia una solución integral, trazable, segura y
modular para administrar el ciclo de vida del personal.

La planificación parte de las funcionalidades ya implementadas y propone
completar procesos incompletos, eliminar duplicidad funcional,
establecer una fuente única de información, normalizar estados,
fortalecer la seguridad y conectar los procesos de personal, asistencia,
vacaciones, nómina, selección, desarrollo, bienestar, documentos,
auditoría y reportes.

> **Regla de implementación:** ninguna funcionalidad descrita como
> propuesta debe asumirse como existente hasta que haya sido
> desarrollada, probada, migrada y validada funcionalmente.

------------------------------------------------------------------------

## 2. Objetivos

### 2.1 Objetivo general

Consolidar el módulo de RRHH como la fuente central de gestión del
talento humano del ERP-EPMAPA-T, garantizando integridad de datos,
automatización de procesos, trazabilidad, seguridad por roles e
indicadores confiables.

### 2.2 Objetivos específicos

1.  Definir una fuente única de empleados.
2.  Consolidar la ficha integral del funcionario.
3.  Normalizar catálogos, estados y transiciones.
4.  Completar contratos y acciones de personal.
5.  Fortalecer vacaciones, permisos y licencias.
6.  Implementar control de asistencia y horarios.
7.  Construir un motor de nómina parametrizable.
8.  Integrar selección, contratación y onboarding.
9.  Fortalecer evaluación, capacitación y carrera.
10. Implementar expediente documental real.
11. Incorporar seguridad y salud ocupacional.
12. Mejorar bienestar y clima laboral.
13. Implementar reportes operativos y gerenciales.
14. Rediseñar los indicadores del dashboard.
15. Garantizar auditoría completa.
16. Implementar seguridad RBAC.
17. Preparar un portal de autoservicio del empleado.
18. Crear una estrategia controlada de migración y compatibilidad.

------------------------------------------------------------------------

## 3. Diagnóstico resumido

Actualmente coexisten varias familias funcionales:

-   `/api/v1/rrhh`
-   `/api/personal`
-   `/api/th-*`
-   controladores tradicionales sin `/api`

Existen funcionalidades para empleados, selección, onboarding,
desarrollo, compensación, bienestar, cumplimiento, dashboard, personal
tradicional, acciones de personal, expedientes, vacaciones, permisos,
licencias y auditoría.

El principal problema arquitectónico es que no existe una sincronización
demostrada entre las diferentes representaciones de personal y talento
humano.

### 3.1 Riesgos principales

-   Duplicidad de empleados.
-   Datos laborales inconsistentes.
-   Estados almacenados como texto libre.
-   Procesos parcialmente implementados.
-   Nómina sin motor automático de cálculo.
-   Expediente que almacena metadatos pero no gestiona integralmente el
    archivo.
-   Solicitudes sin cancelación/reversión.
-   Ausencia de un módulo completo de asistencia.
-   Indicadores con fórmulas que requieren revisión.
-   Auditoría parcial.
-   Autorización por rol pendiente de verificación.
-   Ausencia de pruebas funcionales identificadas para RRHH.

------------------------------------------------------------------------

## 4. Arquitectura funcional objetivo

``` text
RRHH
|
+-- Dashboard
|
+-- Administración de Personal
|   +-- Empleados
|   +-- Datos personales
|   +-- Datos laborales
|   +-- Cargos
|   +-- Dependencias
|   +-- Áreas
|   +-- Contactos de emergencia
|   +-- Contratos
|   +-- Acciones de personal
|   +-- Historial laboral
|   +-- Expediente digital
|
+-- Asistencia
|   +-- Jornadas
|   +-- Horarios
|   +-- Turnos
|   +-- Marcaciones
|   +-- Atrasos
|   +-- Faltas
|   +-- Salidas anticipadas
|   +-- Horas extras
|   +-- Justificaciones
|
+-- Vacaciones, Permisos y Licencias
|   +-- Saldos
|   +-- Solicitudes
|   +-- Aprobaciones
|   +-- Rechazos
|   +-- Cancelaciones
|   +-- Reversiones
|   +-- Calendario
|
+-- Nómina
|   +-- Períodos
|   +-- Rubros
|   +-- Ingresos
|   +-- Descuentos
|   +-- Horas extras
|   +-- Beneficios
|   +-- Anticipos
|   +-- Préstamos
|   +-- Cálculo
|   +-- Revisión
|   +-- Aprobación
|   +-- Pago
|   +-- Reversión
|   +-- Roles individuales
|
+-- Reclutamiento y Selección
+-- Onboarding
+-- Evaluación de Desempeño
+-- Capacitación
+-- Planes de Carrera
+-- Mentorías
+-- Bienestar y Clima Laboral
+-- Seguridad y Salud Ocupacional
+-- Gestión Documental
+-- Reportes
+-- Auditoría
+-- Portal del Empleado
+-- Configuración
```

------------------------------------------------------------------------

# 5. FASE 1 - Consolidación del núcleo de personal

## 5.1 Fuente única de empleados

Se deberá definir una única entidad funcional como fuente oficial del
funcionario.

### Decisión requerida

Determinar si:

-   `Personal` se convierte en entidad maestra;
-   `RrhhEmployee` se convierte en entidad maestra; o
-   se crea una nueva entidad maestra y se migran ambas estructuras.

### Recomendación

Mantener una entidad maestra de empleado con identificador interno único
y relacionar todos los módulos mediante ese identificador.

No se recomienda mantener dos maestros independientes sincronizados
mediante lógica duplicada.

### Criterios de aceptación

-   Un funcionario posee un único ID maestro.
-   Cédula/identificación no puede duplicarse.
-   Código institucional no puede duplicarse.
-   Todos los nuevos módulos referencian el ID maestro.
-   Se documenta el mapeo de IDs históricos.
-   Se define estrategia de compatibilidad temporal con endpoints
    existentes.

------------------------------------------------------------------------

## 5.2 Ficha única del empleado

La pantalla del empleado será el centro del módulo.

### Pestañas propuestas

1.  Resumen
2.  Datos personales
3.  Información de contacto
4.  Información laboral
5.  Cargo y dependencia
6.  Contratos
7.  Acciones de personal
8.  Asistencia
9.  Vacaciones/permisos/licencias
10. Nómina
11. Evaluaciones
12. Capacitaciones
13. Carrera y mentoría
14. Beneficios e incentivos
15. Expediente documental
16. Contactos de emergencia
17. Historial
18. Auditoría

### Información adicional propuesta

-   Fotografía.
-   Jefe inmediato.
-   Lugar de trabajo.
-   Modalidad laboral.
-   Jornada.
-   Horario.
-   Cuenta bancaria, con permisos restringidos.
-   Datos requeridos para nómina.
-   Nivel académico.
-   Profesión.
-   Fecha de antigüedad.
-   Estado actual.
-   Motivo de inactivación/desvinculación.

------------------------------------------------------------------------

# 6. FASE 2 - Catálogos, estados y workflows

## 6.1 Catálogos centralizados

Crear catálogos administrables para:

-   estados laborales;
-   tipos de contrato;
-   cargos;
-   áreas;
-   dependencias;
-   jornadas;
-   horarios;
-   tipos de acción de personal;
-   tipos de permiso;
-   tipos de licencia;
-   tipos de documento;
-   motivos de ausencia;
-   rubros de nómina;
-   tipos de beneficio;
-   tipos de incentivo;
-   estados de procesos;
-   tipos de capacitación;
-   tipos de evaluación.

## 6.2 Estados laborales

Ejemplo:

``` text
ACTIVO
INACTIVO
VACACIONES
PERMISO
LICENCIA
SUSPENDIDO
DESVINCULADO
JUBILADO
```

Cada estado deberá tener:

-   código;
-   descripción;
-   activo;
-   orden;
-   color UI opcional;
-   reglas de transición.

## 6.3 Motor de workflow

No permitir cambios arbitrarios de estado desde el frontend.

Modelo recomendado:

``` text
ESTADO_ACTUAL -> ACCION -> ESTADO_DESTINO
```

Ejemplo:

``` text
SOLICITADA -> APROBAR -> APROBADA
SOLICITADA -> RECHAZAR -> RECHAZADA
APROBADA -> CANCELAR -> CANCELADA
CANCELADA -> REVERTIR -> REVERTIDA
```

Toda transición deberá registrar:

-   usuario;
-   fecha/hora;
-   estado anterior;
-   estado nuevo;
-   acción;
-   observación;
-   IP/origen cuando aplique.

------------------------------------------------------------------------

# 7. FASE 3 - Contratos y acciones de personal

## 7.1 Contratos

Completar CRUD y ciclo funcional.

### Funciones

-   Crear contrato.
-   Editar borrador.
-   Activar contrato.
-   Renovar.
-   Generar adenda.
-   Terminar.
-   Anular.
-   Consultar historial.
-   Adjuntar documento.
-   Alertar vencimiento.

### Campos mínimos

-   empleado;
-   tipo;
-   número;
-   fecha inicio;
-   fecha fin;
-   cargo;
-   dependencia;
-   remuneración;
-   jornada;
-   estado;
-   documento;
-   observación.

### Estados

``` text
BORRADOR
VIGENTE
POR_VENCER
VENCIDO
RENOVADO
TERMINADO
ANULADO
```

## 7.2 Acciones de personal

Mantener y ampliar:

-   INGRESO
-   MOVIMIENTO
-   ENCARGO
-   SUBROGACION
-   DESVINCULACION
-   REINCORPORACION

Agregar, según validación funcional:

-   CAMBIO_CARGO
-   CAMBIO_DEPENDENCIA
-   CAMBIO_REMUNERACION
-   CAMBIO_JORNADA
-   SUSPENSION
-   JUBILACION

### Regla principal

Una acción aprobada podrá actualizar automáticamente la ficha laboral y
conservar el valor anterior en el historial.

------------------------------------------------------------------------

# 8. FASE 4 - Expediente digital

Implementar gestión documental real.

## 8.1 Categorías

-   identificación;
-   contrato;
-   nombramiento;
-   títulos;
-   certificados;
-   acciones de personal;
-   evaluaciones;
-   capacitaciones;
-   permisos;
-   licencias;
-   documentos médicos con acceso restringido;
-   documentos administrativos;
-   otros.

## 8.2 Metadatos

Cada archivo deberá registrar:

``` text
id
employee_id
document_type_id
name
description
original_filename
storage_key
mime_type
size
hash
version
issue_date
expiration_date
confidential
status
created_by
created_at
updated_by
updated_at
```

## 8.3 Funcionalidades

-   carga;
-   descarga;
-   vista previa;
-   versionamiento;
-   reemplazo controlado;
-   vencimientos;
-   alertas;
-   eliminación lógica;
-   permisos por categoría;
-   auditoría de descarga;
-   búsqueda.

------------------------------------------------------------------------

# 9. FASE 5 - Vacaciones, permisos y licencias 2.0

## 9.1 Saldos

Administrar:

``` text
Saldo inicial
+ Días generados
+ Ajustes positivos
- Días utilizados
- Días reservados
- Ajustes negativos
= Saldo disponible
```

## 9.2 Reglas

-   calendario de feriados;
-   fines de semana configurables;
-   cálculo por días laborables;
-   solicitudes por horas cuando corresponda;
-   cruces de año;
-   acumulación;
-   caducidad si aplica;
-   vacaciones adelantadas configurables;
-   validación de solapamientos;
-   bloqueo de saldo al aprobar;
-   reintegro por cancelación/reversión.

## 9.3 Workflow

``` text
BORRADOR
   |
SOLICITADA
   |
EN_REVISION
 /       \
APROBADA RECHAZADA
   |
EJECUTADA
   |
CERRADA
```

Rutas adicionales:

``` text
SOLICITADA -> CANCELADA (sin consumo de saldo)
APROBADA -> REVERTIDA (reintegra únicamente un consumo registrado)
```

## 9.4 Aprobación multinivel

Configurable por:

-   jefe inmediato;
-   responsable de área;
-   RRHH;
-   autoridad competente.

## 9.5 Calendario

Vista:

-   mensual;
-   semanal;
-   por dependencia;
-   por empleado;
-   por tipo de ausencia.

------------------------------------------------------------------------

# 10. FASE 6 - Asistencia y control horario

Este módulo deberá conectarse con vacaciones y nómina.

## 10.1 Configuración

-   jornadas;
-   turnos;
-   horarios;
-   tolerancias;
-   días laborables;
-   feriados;
-   horarios especiales.

## 10.2 Marcaciones

Origen posible:

-   biométrico;
-   importación CSV/Excel;
-   API;
-   aplicación móvil;
-   registro manual autorizado.

## 10.3 Procesamiento

Por empleado/día:

``` text
Horario esperado
        +
Marcaciones
        +
Permisos/licencias/vacaciones
        =
Resultado diario
```

Resultados:

-   normal;
-   atraso;
-   falta;
-   salida anticipada;
-   permiso;
-   licencia;
-   vacaciones;
-   horas extras;
-   inconsistencia.

## 10.4 Justificaciones

Workflow:

``` text
GENERADA
-> JUSTIFICACION_SOLICITADA
-> APROBADA / RECHAZADA
```

## 10.5 Horas extras

-   solicitadas;
-   autorizadas;
-   trabajadas;
-   aprobadas;
-   enviadas a nómina.

------------------------------------------------------------------------

# 11. FASE 7 - Motor de nómina

## 11.1 Principio

La nómina no deberá recibir únicamente valores finales calculados
externamente. El sistema deberá disponer de un motor parametrizable y
reproducible.

## 11.2 Estructura

``` text
SUELDO BASE
+ INGRESOS
+ HORAS EXTRA
+ BENEFICIOS
+ BONIFICACIONES
+ OTROS
-----------------
TOTAL INGRESOS

- APORTES
- RETENCIONES
- ANTICIPOS
- PRESTAMOS
- DESCUENTOS
- OTROS
-----------------
NETO A PAGAR
```

## 11.3 Rubros

Cada rubro deberá definir:

-   código;
-   nombre;
-   tipo: ingreso/descuento;
-   fórmula;
-   prioridad;
-   imponible;
-   recurrente;
-   vigencia;
-   estado.

## 11.4 Períodos

``` text
ABIERTO
EN_CALCULO
CALCULADO
EN_REVISION
APROBADO
PAGADO
CERRADO
REVERTIDO
```

## 11.5 Funcionalidades

-   crear período;
-   cargar novedades;
-   importar novedades;
-   calcular;
-   recalcular;
-   validar inconsistencias;
-   revisar;
-   aprobar;
-   generar roles;
-   exportar;
-   registrar pago;
-   cerrar;
-   reversar con autorización.

## 11.6 Snapshot

Al cerrar una nómina se deberá almacenar una fotografía de:

-   parámetros;
-   fórmulas;
-   sueldo;
-   rubros;
-   porcentajes;
-   valores calculados.

Una modificación futura de parámetros no podrá alterar una nómina
histórica.

------------------------------------------------------------------------

# 12. FASE 8 - Reclutamiento y selección

## 12.1 Flujo objetivo

``` text
REQUISICION
-> APROBACION
-> VACANTE
-> PUBLICACION
-> POSTULACIONES
-> PRESELECCION
-> ENTREVISTA
-> EVALUACION
-> SELECCION
-> CONTRATACION
-> EMPLEADO
-> ONBOARDING
```

## 12.2 Mejoras

-   publicación/cierre de vacantes;
-   historial de etapas;
-   documentos de candidatos;
-   puntuación por criterios;
-   entrevistas programadas;
-   entrevistadores;
-   observaciones;
-   selección;
-   rechazo;
-   banco de candidatos;
-   conversión candidato -\> empleado.

### Regla crítica

La contratación deberá reutilizar los datos del candidato y evitar doble
digitación.

------------------------------------------------------------------------

# 13. FASE 9 - Onboarding

Crear plantillas de incorporación por cargo/dependencia.

Ejemplo:

``` text
Documentación entregada
Cuenta institucional
Accesos ERP
Correo
Equipos
Inducción
Seguridad ocupacional
Políticas
Presentación al área
Capacitación inicial
Evaluación de incorporación
```

Cada tarea deberá tener:

-   responsable;
-   fecha objetivo;
-   fecha cumplimiento;
-   estado;
-   evidencia;
-   observación.

------------------------------------------------------------------------

# 14. FASE 10 - Evaluación de desempeño

## 14.1 Estructura

-   períodos;
-   plantillas;
-   competencias;
-   objetivos;
-   indicadores;
-   ponderaciones;
-   evaluadores;
-   resultados;
-   observaciones;
-   planes de mejora.

## 14.2 Modalidades futuras

Preparar el modelo para:

-   90°;
-   180°;
-   270°;
-   360°.

## 14.3 Integración

``` text
Evaluación
   |
Brechas
   |
Plan de mejora
   |
Capacitación
   |
Seguimiento
   |
Nueva evaluación
```

------------------------------------------------------------------------

# 15. FASE 11 - Capacitación, carrera y mentoría

## 15.1 Capacitaciones

Agregar:

-   catálogo;
-   plan anual;
-   proveedor;
-   presupuesto;
-   asistentes;
-   asistencia;
-   certificado;
-   evaluación;
-   costo;
-   horas;
-   resultados.

## 15.2 Planes de carrera

Definir:

-   cargo actual;
-   cargo objetivo;
-   competencias requeridas;
-   brechas;
-   actividades;
-   hitos;
-   fecha objetivo;
-   porcentaje de avance.

## 15.3 Mentoría

Relacionar mentor y colaborador con:

-   objetivos;
-   sesiones;
-   compromisos;
-   seguimiento;
-   resultados.

------------------------------------------------------------------------

# 16. FASE 12 - Bienestar y clima laboral

## 16.1 Encuestas

Evolucionar de valores agregados hacia encuestas configurables.

Agregar:

-   preguntas;
-   dimensiones;
-   escalas;
-   anonimato;
-   participantes;
-   respuestas;
-   resultados;
-   segmentación autorizada.

## 16.2 Programas de bienestar

-   planificación;
-   presupuesto;
-   participantes;
-   asistencia;
-   evaluación;
-   resultados.

## 16.3 Conflictos laborales

Implementar workflow:

``` text
REPORTADO
-> EN_ANALISIS
-> EN_GESTION
-> RESUELTO
-> CERRADO
```

Acceso estrictamente restringido.

------------------------------------------------------------------------

# 17. FASE 13 - Seguridad y Salud Ocupacional

Propuesta de ampliación:

-   capacitaciones de seguridad;
-   incidentes;
-   accidentes;
-   inspecciones;
-   riesgos;
-   medidas preventivas;
-   entrega de EPP;
-   exámenes ocupacionales, con acceso restringido;
-   vencimientos;
-   seguimiento.

Este submódulo deberá ser validado con los responsables funcionales
antes de definir campos definitivos.

------------------------------------------------------------------------

# 18. FASE 14 - Portal del empleado

Crear autoservicio con permisos limitados.

## Funciones

El empleado podrá:

-   consultar su ficha;
-   actualizar información permitida;
-   consultar contratos;
-   descargar documentos autorizados;
-   consultar saldo de vacaciones;
-   solicitar vacaciones;
-   solicitar permisos/licencias;
-   adjuntar justificativos;
-   consultar asistencia;
-   presentar justificación;
-   consultar rol de pagos;
-   consultar capacitaciones;
-   consultar evaluaciones;
-   recibir notificaciones.

El empleado no deberá poder modificar directamente información laboral
crítica.

------------------------------------------------------------------------

# 19. FASE 15 - Dashboard RRHH 2.0

Separar indicadores en tableros.

## 19.1 Ejecutivo

-   empleados activos;
-   ingresos;
-   salidas;
-   rotación;
-   distribución por dependencia;
-   antigüedad;
-   tipos de contrato.

## 19.2 Asistencia

-   ausentismo;
-   atrasos;
-   faltas;
-   horas extras;
-   incidencias.

## 19.3 Nómina

-   costo total;
-   costo por dependencia;
-   ingresos;
-   descuentos;
-   horas extras;
-   evolución mensual.

## 19.4 Talento

-   vacantes;
-   tiempo de contratación;
-   capacitaciones;
-   horas de capacitación;
-   desempeño;
-   planes de carrera.

## 19.5 Vacaciones

-   saldo total;
-   empleados con saldo alto;
-   solicitudes;
-   próximas vacaciones;
-   distribución por dependencia.

### Regla

Toda fórmula deberá documentarse:

``` text
Nombre KPI
Objetivo
Fórmula
Numerador
Denominador
Filtros
Estados incluidos
Periodicidad
Fuente
Responsable funcional
```

------------------------------------------------------------------------

# 20. Reportes

## 20.1 Personal

-   listado de empleados;
-   empleados activos/inactivos;
-   por dependencia;
-   por cargo;
-   por contrato;
-   antigüedad;
-   cumpleaños;
-   contactos.

## 20.2 Contratos

-   vigentes;
-   vencidos;
-   próximos a vencer;
-   renovaciones.

## 20.3 Vacaciones

-   saldos;
-   solicitudes;
-   aprobadas;
-   pendientes;
-   historial.

## 20.4 Asistencia

-   marcaciones;
-   atrasos;
-   faltas;
-   horas extras;
-   novedades.

## 20.5 Nómina

-   resumen;
-   rol individual;
-   consolidado;
-   por dependencia;
-   rubros;
-   novedades;
-   histórico.

## 20.6 Desarrollo

-   evaluaciones;
-   capacitaciones;
-   horas;
-   costos;
-   planes de carrera.

### Formatos

-   PDF;
-   XLSX;
-   CSV cuando corresponda.

------------------------------------------------------------------------

# 21. Seguridad RBAC

## 21.1 Roles base

``` text
ADMIN_SISTEMA
ADMIN_RRHH
DIRECTOR_RRHH
ANALISTA_RRHH
NOMINA
JEFE_AREA
EMPLEADO
AUDITOR
SEGURIDAD_OCUPACIONAL
```

## 21.2 Permisos

Modelo:

``` text
RRHH.EMPLEADO.VER
RRHH.EMPLEADO.CREAR
RRHH.EMPLEADO.EDITAR
RRHH.CONTRATO.CREAR
RRHH.CONTRATO.APROBAR
RRHH.VACACION.APROBAR
RRHH.NOMINA.CALCULAR
RRHH.NOMINA.APROBAR
RRHH.NOMINA.REVERSAR
RRHH.DOCUMENTO.DESCARGAR
RRHH.AUDITORIA.VER
```

## 21.3 Seguridad adicional

-   autorización backend obligatoria;
-   no confiar únicamente en ocultar botones;
-   restricción por dependencia cuando corresponda;
-   protección de datos sensibles;
-   registro de exportaciones;
-   registro de descargas sensibles;
-   principio de mínimo privilegio.

------------------------------------------------------------------------

# 22. Auditoría

Crear auditoría transversal.

## Eventos

``` text
CREATE
UPDATE
DELETE_LOGICAL
STATUS_CHANGE
APPROVE
REJECT
CANCEL
REVERSE
LOGIN_RELATED_ACTION
UPLOAD
DOWNLOAD
EXPORT
PAYROLL_CALCULATE
PAYROLL_APPROVE
PAYROLL_REVERSE
```

## Datos mínimos

``` text
id
module
entity
record_id
action
previous_value
new_value
user_id
username
timestamp
ip
observation
```

Para cambios críticos se recomienda almacenar diferencias estructuradas
en JSON.

------------------------------------------------------------------------

# 23. Notificaciones

Crear servicio central de notificaciones.

Eventos:

-   contrato próximo a vencer;
-   documento próximo a vencer;
-   solicitud pendiente;
-   solicitud aprobada/rechazada;
-   saldo de vacaciones;
-   onboarding pendiente;
-   evaluación pendiente;
-   capacitación próxima;
-   nómina disponible;
-   incidencia de asistencia.

Canales inicialmente:

-   notificación interna;
-   correo institucional, si existe integración.

Preparar arquitectura para canales adicionales sin acoplar la lógica de
negocio.

------------------------------------------------------------------------

# 24. Propuesta de modelo de datos

Las siguientes entidades representan el objetivo conceptual. Los nombres
finales deberán adaptarse al estándar del proyecto.

``` text
rrhh_employee
rrhh_employee_contact
rrhh_employee_emergency_contact
rrhh_employee_job_history

rrhh_contract
rrhh_personnel_action

rrhh_document
rrhh_document_type
rrhh_document_version

rrhh_work_schedule
rrhh_shift
rrhh_attendance_mark
rrhh_attendance_day
rrhh_attendance_incident
rrhh_overtime

rrhh_leave_balance
rrhh_leave_request
rrhh_leave_transaction
rrhh_holiday

rrhh_payroll_period
rrhh_payroll_concept
rrhh_payroll_parameter
rrhh_payroll_employee
rrhh_payroll_detail
rrhh_payroll_novelty

rrhh_vacancy
rrhh_candidate
rrhh_candidate_stage_history
rrhh_interview
rrhh_onboarding
rrhh_onboarding_task

rrhh_performance_period
rrhh_performance_review
rrhh_competency
rrhh_performance_detail

rrhh_training
rrhh_training_attendee
rrhh_career_plan
rrhh_career_milestone
rrhh_mentoring

rrhh_climate_survey
rrhh_climate_question
rrhh_climate_response
rrhh_wellbeing_program
rrhh_conflict_case

rrhh_notification
rrhh_workflow_history
rrhh_audit
```

------------------------------------------------------------------------

# 25. Convenciones API

Consolidar gradualmente bajo:

``` text
/api/v1/rrhh
```

Ejemplos:

``` text
GET    /api/v1/rrhh/employees
GET    /api/v1/rrhh/employees/{id}
POST   /api/v1/rrhh/employees
PUT    /api/v1/rrhh/employees/{id}

GET    /api/v1/rrhh/employees/{id}/contracts
POST   /api/v1/rrhh/employees/{id}/contracts

GET    /api/v1/rrhh/employees/{id}/documents
POST   /api/v1/rrhh/employees/{id}/documents

GET    /api/v1/rrhh/leave/requests
POST   /api/v1/rrhh/leave/requests
POST   /api/v1/rrhh/leave/requests/{id}/approve
POST   /api/v1/rrhh/leave/requests/{id}/reject
POST   /api/v1/rrhh/leave/requests/{id}/cancel
POST   /api/v1/rrhh/leave/requests/{id}/reverse

GET    /api/v1/rrhh/attendance
POST   /api/v1/rrhh/attendance/import

GET    /api/v1/rrhh/payroll/periods
POST   /api/v1/rrhh/payroll/periods
POST   /api/v1/rrhh/payroll/periods/{id}/calculate
POST   /api/v1/rrhh/payroll/periods/{id}/approve
POST   /api/v1/rrhh/payroll/periods/{id}/pay
POST   /api/v1/rrhh/payroll/periods/{id}/reverse
```

### Estándar de respuesta

Definir estructura uniforme para:

-   datos;
-   errores;
-   validaciones;
-   paginación;
-   timestamp;
-   código de error;
-   correlation/request ID.

------------------------------------------------------------------------

# 26. Frontend Angular

## 26.1 Estructura sugerida

``` text
rrhh/
  dashboard/
  employees/
  contracts/
  personnel-actions/
  attendance/
  leave/
  payroll/
  recruitment/
  onboarding/
  performance/
  training/
  career/
  wellbeing/
  occupational-safety/
  documents/
  reports/
  audit/
  settings/
```

## 26.2 Componentes compartidos

Crear componentes reutilizables para:

-   tablas;
-   filtros;
-   paginación;
-   estados;
-   timeline;
-   carga de documentos;
-   aprobación;
-   historial;
-   auditoría;
-   selector de empleado;
-   selector de dependencia;
-   selector de período;
-   confirmaciones.

## 26.3 UX

-   diseño responsive;
-   formularios agrupados;
-   filtros persistentes;
-   breadcrumbs;
-   estados visuales consistentes;
-   confirmación en acciones críticas;
-   skeleton/loading;
-   mensajes de error claros;
-   validación frontend y backend;
-   accesibilidad básica.

------------------------------------------------------------------------

# 27. Migración

## 27.1 Etapa 1 - Inventario

Identificar:

-   tablas reales en producción;
-   cantidad de registros;
-   duplicados;
-   IDs;
-   dependencias;
-   relaciones;
-   endpoints consumidos por frontend.

## 27.2 Etapa 2 - Mapeo

Crear tabla de correspondencia:

``` text
origen
id_origen
id_maestro
tipo
fecha_migracion
```

## 27.3 Etapa 3 - Limpieza

Validar:

-   identificaciones duplicadas;
-   correos duplicados;
-   empleados sin cargo;
-   empleados sin dependencia;
-   estados incompatibles;
-   contratos inconsistentes.

## 27.4 Etapa 4 - Migración

-   respaldar;
-   ejecutar migración;
-   validar conteos;
-   validar relaciones;
-   comparar muestras;
-   generar informe.

## 27.5 Etapa 5 - Compatibilidad

Mantener temporalmente adaptadores para APIs antiguas cuando sea
necesario.

## 27.6 Etapa 6 - Retiro

Eliminar APIs antiguas únicamente después de comprobar que ningún
consumidor activo las utiliza.

------------------------------------------------------------------------

# 28. Pruebas

## 28.1 Unitarias

Servicios críticos:

-   vacaciones;
-   saldos;
-   asistencia;
-   horas extras;
-   nómina;
-   workflows;
-   permisos.

## 28.2 Integración

Validar:

-   empleado -\> contrato;
-   empleado -\> acción;
-   ausencia -\> saldo;
-   asistencia -\> nómina;
-   candidato -\> empleado;
-   evaluación -\> capacitación;
-   documento -\> empleado.

## 28.3 Seguridad

Probar por rol:

-   lectura;
-   creación;
-   edición;
-   aprobación;
-   rechazo;
-   reversión;
-   exportación;
-   documentos sensibles.

## 28.4 Nómina

Casos obligatorios:

-   sueldo normal;
-   ingreso adicional;
-   descuento;
-   horas extras;
-   ausencia;
-   anticipo;
-   múltiples rubros;
-   recálculo;
-   cierre;
-   reversión.

------------------------------------------------------------------------

# 29. Roadmap propuesto

## ETAPA 0 - Preparación

-   validar esquema productivo;
-   levantar pantallas actuales;
-   identificar consumidores;
-   definir fuente única.

## ETAPA 1 - Core RRHH

-   empleado maestro;
-   catálogos;
-   estados;
-   ficha única;
-   RBAC;
-   auditoría base.

**Prioridad: CRÍTICA**

## ETAPA 2 - Vida laboral

-   contratos;
-   acciones;
-   historial;
-   expediente.

**Prioridad: ALTA**

## ETAPA 3 - Tiempo y ausencias

-   vacaciones;
-   permisos;
-   licencias;
-   feriados;
-   horarios;
-   asistencia;
-   horas extras.

**Prioridad: ALTA**

## ETAPA 4 - Nómina

-   conceptos;
-   parámetros;
-   novedades;
-   cálculo;
-   aprobación;
-   pago;
-   rol;
-   reversión.

**Prioridad: CRÍTICA**

## ETAPA 5 - Talento

-   reclutamiento;
-   selección;
-   onboarding;
-   evaluación;
-   capacitación;
-   carrera;
-   mentoría.

**Prioridad: MEDIA**

## ETAPA 6 - Bienestar y seguridad

-   clima;
-   bienestar;
-   conflictos;
-   seguridad ocupacional.

**Prioridad: MEDIA**

## ETAPA 7 - Analítica

-   dashboard;
-   KPIs;
-   reportes;
-   exportaciones.

**Prioridad: ALTA**

## ETAPA 8 - Autoservicio

-   portal del empleado;
-   notificaciones;
-   solicitudes;
-   documentos;
-   roles.

**Prioridad: ALTA**

## ETAPA 9 - Estabilización

-   pruebas;
-   rendimiento;
-   seguridad;
-   migración;
-   documentación;
-   capacitación;
-   salida a producción.

**Prioridad: CRÍTICA**

------------------------------------------------------------------------

# 30. Dependencias entre fases

``` text
FUENTE UNICA DE EMPLEADOS
        |
        +--> CONTRATOS
        +--> ACCIONES
        +--> DOCUMENTOS
        +--> VACACIONES
        +--> ASISTENCIA
        +--> NOMINA
        +--> TALENTO
        +--> DASHBOARD

CATALOGOS + WORKFLOWS
        |
        +--> VACACIONES
        +--> CONTRATOS
        +--> NOMINA
        +--> SELECCION

ASISTENCIA
        |
        +--> HORAS EXTRA
        +--> NOVEDADES
        +--> NOMINA

SELECCION
        |
        +--> CONTRATACION
        +--> EMPLEADO
        +--> ONBOARDING
```

No iniciar el rediseño profundo de nómina o asistencia sin resolver
primero la identidad maestra del empleado.

------------------------------------------------------------------------

# 31. Definición de terminado

Una funcionalidad se considerará terminada únicamente cuando tenga:

-   modelo de datos;
-   migración SQL;
-   backend;
-   validaciones;
-   seguridad;
-   auditoría;
-   frontend;
-   estados/workflow;
-   manejo de errores;
-   pruebas unitarias;
-   pruebas de integración;
-   documentación API;
-   pruebas funcionales;
-   aceptación del responsable funcional;
-   despliegue validado.

------------------------------------------------------------------------

# 32. Criterios técnicos generales

1.  Evitar estados como texto libre.
2.  Evitar eliminación física de información laboral crítica.
3.  Utilizar transacciones en operaciones financieras y de saldos.
4.  Implementar bloqueo/concurrencia donde existan saldos o cálculos
    críticos.
5.  Mantener trazabilidad de aprobaciones.
6.  No confiar en valores financieros calculados únicamente por
    frontend.
7.  Mantener snapshots de procesos cerrados.
8.  Centralizar catálogos.
9.  Implementar paginación en listados grandes.
10. Aplicar índices según filtros reales.
11. Proteger documentos sensibles.
12. No exponer datos sensibles en logs.
13. Usar identificadores internos estables.
14. Mantener compatibilidad durante la migración.
15. Documentar reglas funcionales.

------------------------------------------------------------------------

# 33. Prioridades inmediatas

Antes de desarrollar nuevas pantallas se recomienda ejecutar estas
tareas:

### P0 - Crítico

-   definir empleado maestro;
-   validar esquema productivo;
-   identificar duplicidad entre `Personal` y `RrhhEmployee`;
-   definir catálogos de estados;
-   verificar autorización por rol;
-   establecer auditoría transversal.

### P1 - Alta

-   completar contratos;
-   expediente documental;
-   vacaciones 2.0;
-   cancelación/reversión;
-   horarios y asistencia;
-   motor de nómina.

### P2 - Media

-   reclutamiento integral;
-   onboarding;
-   desempeño;
-   capacitación;
-   carrera;
-   bienestar.

### P3 - Evolutiva

-   portal del empleado;
-   notificaciones avanzadas;
-   analítica ampliada;
-   automatizaciones.

------------------------------------------------------------------------

# 34. Resultado esperado

Al implementar y validar este plan, RRHH dejará de funcionar como un conjunto
de registros parcialmente independientes y pasará a operar como un
sistema conectado.

``` text
CANDIDATO
   |
CONTRATACION
   |
EMPLEADO
   |
+-- CONTRATO
+-- ACCIONES
+-- EXPEDIENTE
+-- ASISTENCIA --------+
+-- VACACIONES --------+--> NOMINA
+-- CAPACITACION
+-- EVALUACION
+-- CARRERA
+-- BIENESTAR
   |
HISTORIAL + AUDITORIA
   |
DASHBOARD + REPORTES
```

La ficha del empleado será el eje central y cada proceso deberá generar
trazabilidad, actualizar los datos que corresponda y alimentar
indicadores confiables.

------------------------------------------------------------------------

# 35. Próximos documentos técnicos recomendados

A partir de este plan deberán elaborarse documentos específicos para
implementación:

1.  `RRHH_MODELO_DATOS.md`
2.  `RRHH_API_SPEC.md`
3.  `RRHH_WORKFLOWS.md`
4.  `RRHH_ROLES_PERMISOS.md`
5.  `RRHH_NOMINA.md`
6.  `RRHH_ASISTENCIA.md`
7.  `RRHH_VACACIONES.md`
8.  `RRHH_MIGRACION.md`
9.  `RRHH_FRONTEND.md`
10. `RRHH_TEST_PLAN.md`

El primero recomendado es **`RRHH_MODELO_DATOS.md`**, porque permitirá
definir qué tablas actuales se conservan, cuáles se modifican, cuáles se
crean y cómo se relacionará el empleado maestro con todos los procesos.

------------------------------------------------------------------------

# 36. Alcance confirmado y decisiones pendientes

## 36.1 Línea base

La línea base es la revisión estática del backend del 6 de octubre de 2026. El documento de estado contiene 94 rutas declaradas y distingue operaciones implementadas, parciales y de consulta. Su existencia no confirma despliegue, autorización efectiva ni disponibilidad en pantallas.

Las mejoras de asistencia, motor de nómina, portal, respuestas individuales de clima y seguridad ocupacional integral son ampliaciones propuestas. El frontend Angular de la sección 26 es una propuesta que deberá contrastarse con el repositorio del cliente. No se ha inspeccionado ese repositorio ni la base productiva.

## 36.2 Registro de decisiones

| ID | Decisión a resolver | Responsable funcional propuesto | Evidencia requerida | Bloquea |
|---|---|---|---|---|
| D01 | Elegir maestro de empleados y mantener referencias externas | RRHH + arquitectura | Inventario de relaciones, consumidores y correspondencia de IDs | Migración y nuevos procesos integrados |
| D02 | Definir catálogo de estados y relación con actividad | RRHH | Tabla de estados, transiciones y efectos | Cambios generales de estado |
| D03 | Definir cómputo de vacaciones por régimen y tipo de ausencia | RRHH | Ejemplos de días calendario/laborables, feriados, fracciones y cruces de años | Nuevo cálculo de días y acumulación |
| D04 | Definir aprobadores, delegación y separación de funciones | RRHH + responsables de área | Matriz de autorización y reemplazos con vigencia | Aprobación multinivel y autoservicio |
| D05 | Definir rubros, redondeo, vigencias y reglas de nómina | Nómina + RRHH + finanzas | Casos calculados y parámetros aprobados | Motor y aceptación de nómina |
| D06 | Definir acceso y conservación de documentos sensibles | RRHH + gestión documental | Clasificación, permisos y política de conservación | Expediente integral |
| D07 | Acordar fórmulas, filtros y estados de los indicadores | RRHH + responsables de reportes | Ficha de cada KPI con ejemplos esperados | Dashboard 2.0 |
| D08 | Confirmar origen de marcaciones y jornadas | RRHH + TI | Formato de importación, zona horaria y reglas de turnos | Asistencia y horas extras |
| D09 | Acordar alcance inicial y responsables de aceptación | Responsable del proyecto | Backlog priorizado y responsables asignados | Compromisos de fecha y alcance |

Estado inicial de D01–D09: **POR DEFINIR**. Los responsables son funciones propuestas, no asignaciones a personas.

Las reglas laborales, porcentajes y obligaciones aplicables deben validarse antes de implementarse con fuentes vigentes y el responsable competente. Este plan no fija tasas ni convierte el cálculo por días laborables de la sección 9.2 en una regla universal. El cómputo dependerá de D03.

------------------------------------------------------------------------

# 37. Backlog de implementación trazable

Prioridades: **P0** integridad y control; **P1** operación principal; **P2** ampliación funcional; **P3** evolución. Las dependencias expresan condiciones de cierre; se puede avanzar en diseño o inventario mientras se resuelven.

Todos los ítems se registraron inicialmente como **PENDIENTE**. La sección 43 registra la primera implementación y actualiza su seguimiento; editar el plan por sí solo no constituye una entrega funcional.

## 37.1 Núcleo, seguridad y datos

| ID | Prioridad | Brecha o necesidad | Entrega y criterio de aceptación | Dependencias |
|---|---|---|---|---|
| RRHH-01 | P0 | Familias de personal independientes | Inventario de tablas, FK y clientes; decisión D01 y mapa de IDs sin pérdida de referencias | Acceso de lectura al esquema y consumidores |
| RRHH-02 | P0 | Dos scripts de marzo definen tablas coincidentes | Comparación con esquema instalado; migración versionada y reproducible sobre base nueva y existente | RRHH-01 |
| RRHH-03 | P0 | Estados libres; `active` puede contradecir `employmentStatus` | Catálogos, transiciones y reglas coherentes en POST, PUT y PATCH; valores desconocidos rechazados y datos históricos mapeados | D02, RRHH-01 |
| RRHH-04 | P0 | Autorización efectiva sin verificar; actores recibidos en JSON | Matriz por operación y ámbito; actor obtenido de sesión autenticada; acceso indebido rechazado aunque el cliente cambie IDs | D04 |
| RRHH-05 | P0 | Metadatos y logs parciales | Historial de cambios y decisiones críticas con actor, fecha, motivo y correlación; no admite actor arbitrario del cliente | RRHH-04 |
| RRHH-06 | P1 | Catálogos con listar/guardar | Actualización e inactivación controladas; no rompe referencias históricas; filtros y paginación documentados | RRHH-01, RRHH-03 |
| RRHH-07 | P1 | Fichas y contactos en APIs distintas | Ficha integral con identidad única, contactos y relaciones verificadas; cliente compatible durante transición | RRHH-01, RRHH-04, RRHH-06 |

## 37.2 Vida laboral y documentos

| ID | Prioridad | Brecha o necesidad | Entrega y criterio de aceptación | Dependencias |
|---|---|---|---|---|
| RRHH-08 | P1 | Contratos V1 de consulta | Crear, editar borrador, aprobar, renovar y terminar; valida vigencias y conserva versiones e historial | D02, RRHH-07 |
| RRHH-09 | P1 | Acción `ThAction` registra sin aplicar movimiento | Aprobación y aplicación única de efectos en ficha e historial; reversión autorizada sin borrar evidencia | RRHH-03, RRHH-05, RRHH-08 |
| RRHH-10 | P1 | Archivos de V1 de consulta y `Th*` con metadatos | Carga binaria, consulta, descarga autorizada, versiones y vencimiento; fallo de almacenamiento no deja registro utilizable sin archivo | D06, RRHH-04, RRHH-07 |

## 37.3 Vacaciones, permisos y licencias

| ID | Prioridad | Brecha comprobada | Entrega y criterio de aceptación | Dependencias |
|---|---|---|---|---|
| RRHH-11 | P0 | POST acepta estado y días suministrados | Alta solo en estado inicial permitido; días positivos y validados por backend; ID y aprobación no manipulables en creación | D03, RRHH-04 |
| RRHH-12 | P0 | Lectura y escritura de saldo sin bloqueo explícito en servicio | Control de concurrencia y doble aprobación; dos operaciones simultáneas no sobregiran saldo ni descuentan dos veces | RRHH-11 |
| RRHH-13 | P1 | Descuento al año inicial; saldo sin movimientos | Distribución por ejercicio y libro de movimientos conciliable; no duplica acumulación al reejecutar proceso | D03, RRHH-01, RRHH-12 |
| RRHH-14 | P1 | Sin cancelación ni reversión | Motivo, autorización y reintegro trazable; reintento no devuelve días dos veces | RRHH-05, RRHH-13 |
| RRHH-15 | P1 | Solo aprobar/rechazar, sin etapas configurables | Bandejas, delegación y niveles según D04; solo el aprobador vigente puede resolver la etapa | RRHH-03, RRHH-04, RRHH-14 |

Los ajustes RRHH-11 y RRHH-12 pueden realizarse en las rutas actuales antes de migrar el maestro; deben conservar compatibilidad y tener pruebas de regresión. No requieren esperar al rediseño completo del módulo.

## 37.4 Asistencia y compensación

| ID | Prioridad | Brecha o necesidad | Entrega y criterio de aceptación | Dependencias |
|---|---|---|---|---|
| RRHH-16 | P1 | Jornadas y marcaciones propuestas | Horarios con vigencia e importación que no duplica marcas; turnos nocturnos y marcas incompletas producen resultado verificable | D08, RRHH-07 |
| RRHH-17 | P1 | Integración de novedades propuesta | Justificaciones y horas extras aprobadas se incorporan una sola vez al período; rechazos no alimentan pago | RRHH-15, RRHH-16 |
| RRHH-18 | P1 | Nómina guarda importes enviados | Conceptos y parámetros versionados, cálculo backend reproducible y detalle conciliado con neto; detecta fórmulas circulares | D05, RRHH-07 |
| RRHH-19 | P1 | Nómina sin aprobar/pagar/cerrar/revertir | Estados, separación de funciones, snapshot y reversión; no permite modificar período cerrado ni confundir registro de pago con transferencia bancaria | RRHH-04, RRHH-05, RRHH-18 |
| RRHH-20 | P1 | Beneficios/incentivos solo crean y listan | Edición y finalización con vigencia; integración de rubros que evita doble contabilización | RRHH-03, RRHH-18 |

El diseño de nómina es prioritario por impacto; su implementación depende de datos y reglas aprobados. RRHH-18 puede validarse con novedades controladas antes de automatizar RRHH-17.

## 37.5 Talento, bienestar y cumplimiento

| ID | Prioridad | Brecha o necesidad | Entrega y criterio de aceptación | Dependencias |
|---|---|---|---|---|
| RRHH-21 | P2 | Selección con estados y etapas libres | Publicación/cierre, historial de etapas, entrevistas y selección autorizada; transición inválida rechazada | RRHH-03, RRHH-04 |
| RRHH-22 | P2 | Onboarding sin alta automática ni tareas | Conversión autorizada sin duplicar empleado y checklist con responsables; reintento devuelve el mismo vínculo | RRHH-07, RRHH-08, RRHH-21 |
| RRHH-23 | P2 | Evaluaciones como registro agregado | Períodos, criterios, pesos y cierre; puntuación reproducible y evidencia protegida | RRHH-03, RRHH-07 |
| RRHH-24 | P2 | Capacitación, carrera y mentoría parciales | Participantes, hitos, evidencias y cierre; horas por empleado conciliadas con reporte | RRHH-07, RRHH-23 |
| RRHH-25 | P2 | Clima guarda indicadores agregados | Cuestionario y respuestas, política de anonimato y agregados calculados; no revela identidad en resultados anónimos | D06, RRHH-04 |
| RRHH-26 | P2 | Bienestar y conflictos con ciclo parcial | Inscripciones, seguimiento y cierre; expedientes de conflicto restringidos y resolución trazable | RRHH-03, RRHH-05 |
| RRHH-27 | P2 | Auditorías y políticas sin actualización específica | Seguimiento de hallazgos, versiones de políticas, aprobación y publicación; conserva evidencia de versiones anteriores | RRHH-03, RRHH-05 |
| RRHH-28 | P2 | Seguridad ocupacional integral propuesta | Riesgos, incidentes y planes correctivos según alcance acordado; información sensible accesible solo por permisos específicos | D06, RRHH-04, RRHH-07 |

## 37.6 Analítica, autoservicio y salida

| ID | Prioridad | Brecha o necesidad | Entrega y criterio de aceptación | Dependencias |
|---|---|---|---|---|
| RRHH-29 | P1 | Dashboard usa 30 días fijos y estados particulares | Fichas KPI y consultas corregidas; períodos distintos, filtros y estados dan resultados contrastados con datos de prueba | D07, RRHH-03 |
| RRHH-30 | P1 | Reportes integrales propuestos | PDF/XLSX/CSV según reporte; totales coinciden con consultas y exportaciones respetan ámbito del usuario | RRHH-04, RRHH-29 |
| RRHH-31 | P3 | Portal del empleado propuesto | Lectura propia, solicitudes y documentos; cambiar `employeeId` no permite leer o modificar a otro funcionario | RRHH-04, RRHH-10, RRHH-15 |
| RRHH-32 | P3 | Avisos y recordatorios propuestos | Eventos con reintentos y deduplicación; un fallo de entrega no revierte negocio ni envía duplicados | RRHH-05; evento funcional implementado |
| RRHH-33 | P0 | Compatibilidad y esquema productivo por comprobar | Ensayo de migración, conciliación, recuperación y transición de consumidores; retiro solo con evidencia de desuso | RRHH-01, RRHH-02; funcionalidades de la entrega |
| RRHH-34 | P1 | No se identifican pruebas específicas por nombre | Pruebas de negocio, integración y seguridad de cada entrega; evidencia de aceptación y documentación de API | Ítems incluidos en la entrega |

------------------------------------------------------------------------

# 38. Primera entrega recomendada

**Objetivo:** controlar los riesgos del flujo actual de solicitudes y preparar la consolidación sin exigir el desarrollo de todas las ampliaciones.

Incluye el inventario y decisiones de RRHH-01/02/03/04, los ajustes de RRHH-11/12 y la auditoría necesaria de RRHH-05. Las reglas de días se conservarán hasta acordar D03; se validará su coherencia en backend. Las rutas de compatibilidad se documentarán por consumidor.

Orden sugerido:

1. Inventariar esquema, clientes y estados existentes; resolver D01–D04 para el alcance de esta entrega.
2. Documentar contrato de creación, cálculo de días y matriz de permisos del flujo actual.
3. Implementar validaciones, identidad autenticada y control de concurrencia de solicitud/saldo.
4. Añadir evidencia de auditoría y pruebas de errores, reintentos y operaciones simultáneas.
5. Ensayar migraciones necesarias y validar el cliente consumidor.
6. Registrar aceptación funcional y habilitar la entrega con seguimiento.

Criterios de salida: no se puede crear una solicitud ya aprobada, descontar días negativos, suplantar al aprobador ni consumir saldo dos veces para una misma solicitud. Se mantienen los contratos compatibles acordados y se documentan los casos históricos que requieran corrección.

No se fijan fechas ni duración: dependen del inventario, del equipo disponible y de las decisiones funcionales. Esta entrega no incluye el nuevo motor de nómina, biométricos ni el portal.

------------------------------------------------------------------------

# 39. Matriz mínima de validación

| Caso | Escenario | Resultado esperado de la mejora | Ítems |
|---|---|---|---|
| V01 | Crear con estado `APROBADA` o campos de aprobación | Rechaza el payload según contrato; no persiste aprobación ni afecta saldo | RRHH-11 |
| V02 | Días cero, negativos o inconsistentes con fechas | Rechaza o calcula por regla aprobada; nunca incrementa saldo al aprobar | RRHH-11 |
| V03 | Solicitud solapada, incluyendo creación simultánea | Una política única impide reservas incompatibles; error funcional y sin duplicación | RRHH-12 |
| V04 | Dos solicitudes concurrentes consumen un mismo saldo insuficiente para ambas | Como máximo se aprueba el consumo disponible; saldo e historial conciliados | RRHH-12 |
| V05 | Dos aprobaciones simultáneas de la misma solicitud | Un solo descuento y una sola transición; reintento responde según contrato | RRHH-12 |
| V06 | Fallo entre movimiento de saldo y actualización de solicitud | Transacción revierte ambos cambios; no deja aprobación parcial | RRHH-12 |
| V07 | Solicitud atraviesa diciembre/enero | Distribución exacta por ejercicio según D03, sin cargar todo por defecto al año inicial | RRHH-13 |
| V08 | Reversión repetida de una aprobación | Un solo reintegro, con motivo y vínculo al movimiento original | RRHH-14 |
| V09 | Actor del JSON diferente del usuario autenticado | Auditoría y autorización usan identidad autenticada; suplantación rechazada | RRHH-04/05 |
| V10 | Usuario consulta documento, salario o solicitud de otra dependencia | Respeta permisos y ámbito; no expone contenido ni URL reutilizable sin autorización | RRHH-04/10 |
| V11 | Crear, editar y cambiar estado de empleado | `active` y estado laboral mantienen la regla de D02 en las tres operaciones | RRHH-03 |
| V12 | Recalcular nómina y luego modificar parámetros futuros | Cálculo reproducible; período cerrado permanece inmutable | RRHH-18/19 |
| V13 | Consultar dashboard en rango corto, largo y sin registros | Denominadores y estados correctos; cero/no aplica según ficha KPI, sin errores | RRHH-29 |
| V14 | Migrar y repetir ensayo | Conteos, referencias y saldos conciliados; correspondencias no duplicadas | RRHH-33 |
| V15 | Convertir candidato dos veces | Un solo empleado vinculado, conservando historial de selección | RRHH-22 |

Las pruebas se añaden al implementar cada capacidad. Este documento no indica que ya existan o que hayan pasado.

------------------------------------------------------------------------

# 40. Migración y operación de las entregas

Cada entrega que cambie datos deberá registrar:

- Esquema origen/destino, precondiciones y compatibilidad de clientes.
- Conteos, duplicados, referencias huérfanas, saldos e importes antes y después.
- Tratamiento explícito de registros inválidos; no transformar estados desconocidos silenciosamente.
- Ensayo con datos representativos y protección de información sensible.
- Respaldo y procedimiento de recuperación probado; distinguir reversión de aplicación de recuperación de datos.
- Responsable de ejecución y aceptación, ventana acordada y criterios para detener la migración.
- Seguimiento de errores, aprobaciones fallidas, discrepancias de saldo y notificaciones pendientes.

El uso de `CREATE TABLE IF NOT EXISTS` no actualiza por sí mismo restricciones o columnas de tablas existentes. RRHH-02 debe comprobar ambos escenarios y establecer el mecanismo de migraciones compatible con el proyecto.

No se retirarán las rutas `/personal`, `/api/personal` o `/api/th-*` por el solo hecho de crear rutas V1. Se requiere evidencia de consumidores migrados y una correspondencia de IDs comprobada.

------------------------------------------------------------------------

# 41. Seguimiento de avance

Estados del backlog: **PENDIENTE → LISTO PARA DESARROLLO → EN DESARROLLO → EN VALIDACIÓN → TERMINADO**. **BLOQUEADO** exige motivo, responsable y condición de desbloqueo. Un ítem pasa a listo cuando tiene alcance, reglas y dependencias suficientes para implementarse.

Para cada ID de la sección 37 se utilizará esta ficha:

```text
ID: RRHH-XX
Estado: PENDIENTE
Responsable técnico: POR ASIGNAR
Responsable funcional: POR ASIGNAR
Entrega objetivo: POR DEFINIR
Decisiones y dependencias:
Tareas backend / SQL / frontend:
Criterios de aceptación:
Pruebas y evidencia:
PR o commit:
Migración y recuperación:
Resultado de validación funcional:
Fecha y evidencia de despliegue:
Bloqueos:
```

El avance se calculará únicamente sobre los ítems comprometidos en una entrega. Un ítem terminado debe cumplir la sección 31 y tener evidencia; un documento de diseño o una ruta creada por separado no cuenta como funcionalidad terminada. No se ha establecido un porcentaje global de avance.

------------------------------------------------------------------------

# 42. Referencias técnicas y control documental

| Fuente local | Uso en este plan |
|---|---|
| [ESTADO_MODULO_RRHH.md](ESTADO_MODULO_RRHH.md) | Inventario y cobertura funcional del backend |
| [RrhhEmployeeService.java](src/main/java/com/epmapat/erp_epmapat/rrhh/servicio/RrhhEmployeeService.java) | Estados, bandera de actividad y consultas relacionadas |
| [ThLeaveRequestServicio.java](src/main/java/com/epmapat/erp_epmapat/rrhh/servicio/ThLeaveRequestServicio.java) | Creación, aprobación, rechazo y consumo de saldo |
| [ThLeaveRequestR.java](src/main/java/com/epmapat/erp_epmapat/rrhh/repositorio/ThLeaveRequestR.java) | Regla actual de solapamiento |
| [ThLeaveBalanceServicio.java](src/main/java/com/epmapat/erp_epmapat/rrhh/servicio/ThLeaveBalanceServicio.java) | Saldo por persona/año y valores iniciales |
| [RrhhCompensationService.java](src/main/java/com/epmapat/erp_epmapat/rrhh/servicio/RrhhCompensationService.java) | Persistencia de importes de nómina, beneficios e incentivos |
| [RrhhDashboardService.java](src/main/java/com/epmapat/erp_epmapat/rrhh/servicio/RrhhDashboardService.java) | Fórmulas, filtros y estados utilizados por indicadores |
| [RrhhSupport.java](src/main/java/com/epmapat/erp_epmapat/rrhh/servicio/RrhhSupport.java) | Metadatos de auditoría recibidos desde DTO y paginación |
| [Scripts SQL](src/main/resources/sql/) | Línea base del esquema y revisión de migraciones |

| Fecha | Cambio documental | Implementación asociada |
|---|---|---|
| 2026-10-06 | Completa el plan existente con decisiones D01–D09, backlog RRHH-01–34, primera entrega y validación V01–V15 | Ninguna; pendientes de desarrollo y validación |

Al cambiar el alcance o implementar una mejora se actualizarán el estado del módulo, la ficha del backlog y sus evidencias para mantener ambos documentos consistentes.

------------------------------------------------------------------------

# 43. Primera entrega implementada: solicitudes y saldos

Fecha: **6 de octubre de 2026**. Estado de entrega: **EN VALIDACIÓN**. Código y pruebas locales completos para el alcance descrito; aceptación funcional y despliegue pendientes.

## 43.1 Cambios backend

- `/api/th-leave/**` requiere JWT WEB, también al desplegar el WAR bajo un contexto.
- Verifica que el usuario siga activo. Aplica el módulo WEB RRHH (ID `5`) y la ventana `th-leave` o `/th-leave`: nivel `1` para consultar y `2` o superior para escribir. Conserva la excepción del administrador ID `1` del sistema, siempre que esté activo.
- El creador y el aprobador se obtienen del JWT. `aprobadorId` enviado por clientes antiguos se ignora; no concede identidad ni permisos.
- Nuevo `GET /api/th-leave/permissions` devuelve `userId` y `canWrite` para la pantalla.
- La creación de solicitud no admite ID, aprobación previa, metadatos de modificación ni estado distinto de `SOLICITADA`. Normaliza el tipo y calcula los días calendario inclusivos; rechaza días enviados que no coincidan.
- Bloquea la fila de persona antes de comprobar solapamientos, la solicitud antes de resolverla y el saldo anual antes de consumirlo. Las transacciones mantienen saldo y aprobación consistentes.
- La aprobación de vacaciones rechaza saldos inactivos y solicitudes históricas con días incoherentes. El rechazo exige motivo.
- La creación de saldo impide sobrescribir un ID y exige importes no negativos y disponibles igual a asignados menos usados; no fija días de derecho por defecto.
- Los errores de negocio devuelven un mensaje para la pantalla y los conflictos de bloqueo se traducen a HTTP `409`.
- Se corrigió el nombre de entidad de las consultas JPQL de `PersonalR` a `RrhhLegacyPersonal`, de acuerdo con el modelo existente.

Como medida temporal, vacaciones que crucen años se registran en solicitudes separadas por ejercicio; las históricas con ese cruce requieren revisión antes de aprobar. Continúa pendiente la distribución automática de RRHH-13 y la definición D03. El cálculo mantiene días calendario; no se incorpora todavía una regla de días laborables.

## 43.2 Cambios frontend

Proyecto: `../erp_epmapat_fe`.

- Pantalla «Vacaciones, permisos y licencias» consulta permisos al iniciar y muestra modo de consulta cuando corresponde.
- Las acciones de escritura se ocultan o deshabilitan según permiso, estado y operación pendiente; evita envíos repetidos.
- Envía únicamente los campos de negocio; omite actor, estado, días y disponible precalculados.
- Valida fechas y muestra el cálculo de días calendario y el mensaje para cruces de año.
- Maneja errores de carga y escritura, limpia datos al cambiar de persona y descarta respuestas de cargas anteriores.
- Tras completar una operación actualiza saldos/solicitudes; ante conflicto vuelve a consultar el estado vigente.
- El formulario de saldo inicia en cero y solicita el valor asignado; deja el disponible al cálculo del backend.

Los archivos de autenticación y perfil que ya tenían modificaciones en el frontend se conservaron sin cambios adicionales de esta entrega.

## 43.3 Evidencia de pruebas

| Verificación | Resultado |
|---|---|
| Compilación backend y build Angular de desarrollo | Correctos |
| Pruebas backend de servicios, permisos, controlador y filtro JWT | 25 aprobadas |
| Integración contra PostgreSQL 18 temporal en `127.0.0.1:55439/rrhh_test` | 4 aprobadas |
| Pruebas de componente Angular en Chrome Headless | 9 aprobadas |

La integración comprueba solapamiento simultáneo, doble aprobación, dos solicitudes compitiendo por un saldo insuficiente y rollback completo cuando falla la auditoría. Utiliza una base aislada y no las propiedades de conexión del ERP. El servidor temporal fue detenido después de la verificación.

Se configuraron explícitamente los proveedores JUnit 5 y TestNG de Surefire para ejecutar estas pruebas y conservar las suites TestNG existentes; la selección automática anterior no ejecutaba JUnit 5. El frontend incorpora una configuración de pruebas de RRHH que excluye scripts globales de AdminLTE y specs ajenos con errores previos; no representa una ejecución exitosa de toda la suite del proyecto.

Comandos reproducibles:

```powershell
# Backend: pruebas unitarias y de contrato; integración se omite sin su entorno temporal.
mvn "-Dtest=ThLeave*Test" test

# Backend: crea una instancia temporal, ejecuta las 29 pruebas y detiene el servidor.
./scripts/test-rrhh-postgres.ps1

# Frontend, desde erp_epmapat_fe:
npm run test:rrhh
npx ng build --configuration development
```

El script admite `-PostgresBin` para indicar otra instalación de PostgreSQL. El cluster detenido permanece bajo `target/` para inspección. Las pruebas de integración solo se habilitan con `RRHH_TEST_POSTGRES=true` y usan exclusivamente el destino fijo de pruebas.

## 43.4 Seguimiento del backlog y cierre pendiente

| ID | Estado actualizado | Alcance restante |
|---|---|---|
| RRHH-11 | EN VALIDACIÓN | Validación funcional de reglas actuales y payloads con consumidores reales |
| RRHH-12 | EN VALIDACIÓN | Verificación en el entorno de aceptación antes del despliegue |
| RRHH-04 | EN DESARROLLO | Esta entrega protege `th-leave`; faltan permisos finos por operación, dependencia, delegación y demás APIs |
| RRHH-05 | EN DESARROLLO | Actor autenticado para solicitudes; falta auditoría transversal, diferencias y correlación |
| RRHH-34 | EN DESARROLLO | Pruebas del alcance actual implementadas; faltan las de las siguientes entregas |

RRHH-01/02/03 y los demás ítems conservan su estado pendiente salvo evidencia posterior. No se declara terminado el plan completo.

Antes de habilitar esta entrega, comprobar JWT del cliente, usuario activo, módulo WEB RRHH y permisos de la ventana para cada operador. No se conceden permisos automáticamente ni se ejecutan migraciones sobre producción. No hay columnas nuevas requeridas: verificar que existan las tablas `th_leave_*` y la restricción única de persona/año del script de mayo.

Quedan pendientes la aceptación con RRHH, la revisión de datos históricos y el despliegue coordinado de backend/frontend. La siguiente entrega propuesta es RRHH-13/14, una vez acordadas las reglas de movimientos, cruces de ejercicio y reintegro.

------------------------------------------------------------------------

# 44. Segunda entrega: movimientos, cancelación y reversión

Fecha: **6 de octubre de 2026**. Estado: **EN VALIDACIÓN**. Esta sección actualiza el seguimiento de la entrega anterior; no acredita despliegue ni aceptación de todas las reglas del plan.

## 44.1 Reglas implementadas

| Operación | Estado requerido | Estado final | Efecto en vacaciones |
|---|---|---|---|
| Cancelar | `SOLICITADA` | `CANCELADA` | Ninguno: todavía no existía consumo |
| Revertir | `APROBADA` | `REVERTIDA` | Reintegra exactamente el consumo registrado |
| Revertir permiso/licencia | `APROBADA` | `REVERTIDA` | No modifica el saldo de vacaciones |

Las dos acciones requieren motivo no vacío, JWT y permiso de escritura vigente de `th-leave`. Actor, fecha y motivo de resolución se almacenan por separado; se conservan aprobador, fecha y observación de aprobación originales. La bitácora recibe eventos `CANCEL` o `REVERSE`.

La solicitud se bloquea antes de comprobar su estado. Para revertir vacaciones también se bloquea el saldo referenciado por el consumo. Una segunda resolución devuelve conflicto y no repite el reintegro. Solicitudes canceladas o revertidas dejan de bloquear las fechas para nuevas solicitudes.

La reversión devuelve todo el consumo de esa solicitud; no implementa devoluciones parciales, reapertura, aprobación multinivel ni reglas automáticas para días ya disfrutados. Estas decisiones funcionales siguen pendientes. La acción está disponible a los operadores con el permiso actual de escritura, sin ampliar todavía la matriz por rol/dependencia.

## 44.2 Libro de movimientos

Se agrega `th_leave_movements`, con entradas que las APIs solo crean y consultan:

- **APERTURA:** saldo disponible al iniciar el libro y fotografía de asignados/usados. Se genera al crear un saldo o, para un saldo existente, al primer consumo nuevo. No atribuye consumos previos a solicitudes particulares.
- **CONSUMO:** importe negativo, solicitud, saldo anterior/posterior, actor, fecha y motivo al aprobar vacaciones.
- **REINTEGRO:** importe positivo, vínculo al consumo original y fotografías del saldo al revertir.

El disponible se concilia con la suma de apertura, consumos y reintegros. Los días usados previos se conservan en la apertura. Antes de consumir o reintegrar, el saldo debe cuadrar internamente y coincidir con el último movimiento; cambios externos requieren conciliación. Un saldo inactivo impide consumos nuevos, pero admite restitución de un consumo registrado coherente.

No se permite revertir una aprobación histórica sin consumo registrado. No se reconstruyen débitos ni se fabrican devoluciones desde las fechas de una solicitud antigua. Esos casos requieren un procedimiento posterior de conciliación, todavía no implementado.

La base añade unicidad de apertura por saldo, tipo de movimiento por solicitud y referencia a consumo reintegrado, así como controles de signo, importes y cuadratura. La operación de saldo, estado, movimiento y auditoría es transaccional.

## 44.3 API y frontend

Nuevas rutas bajo `/api/th-leave`:

```text
POST /requests/{idrequest}/cancelar   { "motivo": "Cambio de fechas" }
POST /requests/{idrequest}/revertir   { "motivo": "Corrección de aprobación" }
GET  /movements/persona/{idpersonal}?anio=2026
```

El año en la consulta es opcional. La respuesta de movimientos es un DTO con IDs, ejercicio, tipo, días, valores antes/después, actor, fecha, motivo y referencia al consumo original; no serializa la ficha personal completa.

La pantalla Angular incorpora botones de cancelación/reversión según permiso y estado, confirmación y motivo obligatorio. Añade filtros de solicitudes `CANCELADA`/`REVERTIDA`, datos de resolución en el detalle y tabla de movimientos con filtro por año y paginación visual. Al completar una acción recarga solicitudes, saldos y movimientos. Ante conflicto muestra el motivo y conserva el estado obtenido del backend.

## 44.4 Migración requerida y compatibilidad

Aplicar [2026-10-06_rrhh_leave_movements.sql](src/main/resources/sql/2026-10-06_rrhh_leave_movements.sql) **antes de iniciar el backend de esta entrega**. El ERP tiene `spring.jpa.hibernate.ddl-auto=none`; la aplicación no crea estas estructuras automáticamente.

La migración crea la tabla de movimientos y agrega `resuelto_por`, `fecha_resolucion` y `motivo_resolucion` a las solicitudes. Se verificó su ejecución repetida contra PostgreSQL temporal. No modifica solicitudes aprobadas ni distribuye saldos históricos y no fue aplicada a producción.

Mantiene las rutas existentes de creación, aprobación y rechazo; la pantalla actualizada requiere las nuevas rutas de movimientos. Backend, frontend y esquema deben desplegarse de forma coordinada. No debe retirarse la tabla ni borrar movimientos para volver a una versión anterior; la recuperación de datos y compatibilidad del código se planifican antes de desplegar.

## 44.5 Pruebas y pendientes

La suite de esta entrega verifica motivos, estados incompatibles, identidad autenticada, bloqueo de lectura/escritura, conservación de aprobación original, reintegro único, creación de solicitudes en fechas liberadas, apertura de saldos nuevos/existentes y datos filtrados por ejercicio. La integración también comprueba dos reversiones simultáneas, aprobación contra cancelación, rollback de reintegro/auditoría, conciliación de usados históricos y detección de ajustes externos.

Resultados locales: **45 pruebas backend aprobadas** (32 de servicios/contrato/seguridad y 13 de integración PostgreSQL), **15 pruebas frontend aprobadas** y build Angular de desarrollo correcto. La suite backend compila el código modificado. PostgreSQL temporal fue detenido al finalizar. Estos resultados sustituyen los totales de la primera entrega para el estado actual; no incluyen aceptación funcional ni toda la suite de otros módulos.

Comandos: `./scripts/test-rrhh-postgres.ps1` en backend y `npm run test:rrhh` en frontend. El build se verifica con `npx ng build --configuration development`.

| ID | Estado actualizado | Pendiente |
|---|---|---|
| RRHH-13 | EN DESARROLLO | Libro implementado; faltan distribución anual, acumulación, ajustes y reglas D03 |
| RRHH-14 | EN VALIDACIÓN | Cancelación pendiente y reversión íntegra implementadas; validar reglas de disfrute y casos históricos con RRHH |
| RRHH-05 | EN DESARROLLO | Eventos de cancelación/reversión registrados; sigue pendiente la auditoría transversal |
| RRHH-34 | EN DESARROLLO | Pruebas de esta entrega agregadas; futuras capacidades requieren sus propias evidencias |

Siguen pendientes aceptación funcional, matriz fina de permisos, conciliación de aprobaciones antiguas y despliegue. Cruces de año continúan requiriendo solicitudes separadas, hasta resolver D03 y completar RRHH-13.


## 45. Tercera entrega: ajustes y apertura del libro

Estado: EN VALIDACIÓN. Implementación local de backend y frontend; pendiente aceptación funcional y despliegue.

- `POST /api/th-leave/balances/{idbalance}/ajustar`: recibe `dias` con signo, `motivo` obligatorio (hasta 2000 caracteres) y `clave` UUID. Identidad obtenida del JWT y permiso de escritura requerido. Aumenta/disminuye asignados y disponibles conservando usados. Admite hasta dos decimales, rechaza cero, saldos inactivos, disponibles negativos y diferencias entre saldo y libro.
- `POST /api/th-leave/balances/{idbalance}/abrir-libro`: registra una apertura del saldo vigente; repetir no duplica movimientos. Conserva los consumos anteriores sin reconstruir aprobaciones históricas.
- Ambos procedimientos bloquean el saldo dentro de una transacción. Cada ajuste queda registrado en el libro con usuario, fecha, motivo y valores anteriores/posteriores. Una clave repetida con el mismo actor y contenido devuelve éxito sin volver a aplicar; con contenido diferente devuelve 409. El frontend conserva la clave ante una respuesta incierta y bloquea clics mientras procesa.
- La pantalla incorpora acciones de apertura y un formulario de ajuste con confirmación, validación y actualización del historial. El acceso de consulta no habilita estas acciones.

### Migración y verificación

Antes de arrancar el backend actualizado, aplicar en este orden:

1. `src/main/resources/sql/2026-10-06_rrhh_leave_movements.sql`.
2. `src/main/resources/sql/2026-10-06_rrhh_leave_balance_adjustments.sql`.

La segunda migración agrega la clave de operación, su índice único por saldo y restricciones para ajustes. Ambas se verifican ejecutándolas dos veces en PostgreSQL temporal. No se modificó la base de producción.

Validación local: 52 pruebas backend (34 de servicios/contrato/seguridad y 18 de PostgreSQL), 17 pruebas frontend y build Angular de desarrollo. Incluye identidad y permisos de nuevos endpoints, reintentos concurrentes, disminuciones concurrentes, límites, motivos, conservación de usados, apertura repetida y detección de cambios externos. Los avisos de compilación Angular por archivos ajenos sin uso continúan.

RRHH-13 sigue EN DESARROLLO: ajustes y aperturas implementados; quedan distribución anual, acumulación y reglas D03. Siguen pendientes permisos por rol/dependencia, conciliación histórica, aceptación y despliegue. El inventario incorpora dos rutas más (100 endpoints respecto del conteo inicial de 94).

## 46. Cuarta entrega: verificación de saldos y exportación CSV

Estado: **EN VALIDACIÓN**. Implementación local de backend y frontend; pendiente aceptación de RRHH y despliegue. Avanza RRHH-13 y un reporte concreto de RRHH-30 sin resolver ni asumir D03, D04 o D07.

### 46.1 Comportamiento implementado

La verificación compara los días asignados, usados y disponibles del saldo con el libro. Suma apertura, consumos, reintegros y ajustes; verifica continuidad entre movimientos, cuadratura, conservación de asignados en consumos/reintegros, referencias de reintegro y correspondencia con la solicitud. La apertura conserva usados anteriores: no vuelve a descontarlos.

| Estado del libro | Significado |
|---|---|
| `COINCIDE` | Valores actuales y secuencia del libro coinciden; las advertencias históricas se muestran por separado |
| `DIFERENCIA` | Hay diferencias de importes o una inconsistencia en los movimientos, su secuencia o referencias |
| `SIN_LIBRO` | Existe saldo válido, pero no tiene movimientos; los importes esperados y diferencias se muestran sin dato, no como cero |
| `SIN_SALDO` | Hay solicitudes resueltas para el ejercicio sin saldo registrado |
| `SALDO_INVALIDO` | Valores nulos, negativos, sin cuadratura, año inválido o más de un saldo para el ejercicio |

El resultado incluye diferencias firmadas (actual menos libro) en las tres magnitudes, totales por tipo, número de movimientos, alertas y los IDs de vacaciones `APROBADA`/`REVERTIDA` sin consumo registrado. No incluye permisos/licencias en este pendiente financiero. Los ejercicios se ordenan de reciente a antiguo. Solicitudes históricas se agrupan por año de inicio; las que no tienen fecha y los saldos sin año permanecen visibles como ejercicio desconocido al consultar todos los años. Los cruces históricos requieren revisión individual, sin distribución automática entre ejercicios.

La consulta y exportación son de lectura. No corrigen saldos, no registran aperturas, no generan ajustes y no fabrican consumos históricos. `COINCIDE` no certifica derechos acumulados ni todo el histórico. Los casos detectados requieren un procedimiento posterior con evidencia y decisión funcional.

### 46.2 API, seguridad y pantalla

| Método | Ruta | Contrato |
|---|---|---|
| GET | `/api/th-leave/balances/persona/{idpersonal}/conciliacion` | JSON de persona, fecha de consulta y ejercicios; `anio` opcional, entre 1900 y 9999 |
| GET | `/api/th-leave/balances/persona/{idpersonal}/conciliacion.csv` | Nueva verificación exportada como CSV UTF-8 con BOM, delimitador `;` y fecha de consulta; mismo filtro opcional |

Ambas rutas exigen JWT, usuario activo y el permiso actual de consulta. No introducen nuevos ámbitos por dependencia. Un personal inexistente devuelve 404 y parámetros inválidos devuelven 400. Los reportes usan `Cache-Control: no-store`; el CSV es un adjunto, escapa comillas y neutraliza fórmulas en celdas de texto, conservando valores numéricos con signo. Los DTO no exponen la ficha completa ni motivos de solicitudes.

El servicio consulta los datos en una transacción de lectura con aislamiento `REPEATABLE_READ` para utilizar una misma instantánea PostgreSQL. La pantalla muestra estado, fecha, comparaciones y advertencias por año, con acciones «Verificar saldo» y «Exportar CSV» también para usuarios de consulta. El CSV corresponde a una nueva consulta y puede cambiar si se registraron operaciones después de la verificación visible.

La pantalla invalida el reporte al actualizar datos/cambiar ejercicio y descarta respuestas antiguas al cambiar persona. Desactiva consultas/exportaciones duplicadas mientras procesa, descarga el archivo y libera su URL temporal; interpreta los errores JSON que llegan como Blob durante una exportación fallida.

### 46.3 Verificación y pendientes

- **69 pruebas backend aprobadas:** 48 de servicios/contrato/seguridad/exportación y 21 de integración PostgreSQL. Incluye saldos sin libro, históricos sin consumo, datos inválidos, duplicados, ejercicios ausentes, diferencias firmadas, secuencia inconsistente aunque sus totales coincidan, reintegro ausente, consumo vinculado a persona/ejercicio incompatible, CSV y permisos.
- **24 pruebas frontend aprobadas:** incluye consulta con acceso de lectura, filtro por año, invalidación de respuestas antiguas, descarga, liberación de URL y errores en exportación.
- Build Angular de desarrollo correcto; el backend compila durante la suite. Persisten avisos previos de archivos Angular sin uso y bindings SLF4J múltiples.
- PostgreSQL temporal detenido al finalizar. Ninguna consulta ni cambio en la base de producción.

Comandos de verificación: `./scripts/test-rrhh-postgres.ps1` en backend; `npm run test:rrhh` y `npx ng build --configuration development` en frontend. Los resultados corresponden a las pruebas de RRHH incluidas, no a toda la suite del ERP ni a aceptación funcional.

Esta entrega **no necesita una nueva migración**. Siguen siendo necesarias las dos migraciones y su orden indicados en la sección 45 antes del primer arranque de estas entregas. El inventario añade dos rutas respecto de la tercera entrega: **102 endpoints declarados**, frente a 94 en la línea base.

| Ítem | Seguimiento tras esta entrega |
|---|---|
| RRHH-13 | EN DESARROLLO: libro, ajustes, apertura y verificación implementados; faltan acumulación y distribución anual según D03 |
| RRHH-30 | EN DESARROLLO: un CSV de verificación disponible; reportes integrales, PDF/XLSX y ámbitos finos pendientes |
| RRHH-33 | EN DESARROLLO: herramienta de lectura para detectar brechas; siguen pendientes conciliación con evidencia y ensayo del esquema productivo |
| RRHH-34 | EN VALIDACIÓN para las cuatro entregas; pendiente aceptación de RRHH y validación de despliegue |

Siguiente avance posible: bandeja de solicitudes con filtros y paginación para los operadores actuales; aprobación multinivel y delegaciones siguen dependiendo de D04.

## 47. Quinta entrega: bandeja general de solicitudes

Estado: **EN VALIDACIÓN**. Backend y frontend implementados localmente; aceptación funcional y despliegue pendientes. Avanza la bandeja de RRHH-15 sin asumir niveles de aprobación, delegaciones o restricciones por dependencia que siguen pendientes de D04.

### 47.1 Consulta y contrato

Nueva ruta `GET /api/th-leave/requests/bandeja`, con JWT, usuario activo y el permiso vigente de consulta. Respuesta con `contenido`, `pagina`, `tamano`, `total_elementos` y `total_paginas`. El DTO incluye ID y nombre del empleado, datos de la solicitud, aprobación y resolución; no serializa ficha completa, email, dirección ni contactos. Usa `Cache-Control: no-store`.

| Parámetro | Regla |
|---|---|
| `estado` | Por defecto `SOLICITADA`; admite los cinco estados existentes y `TODAS`/`TODOS` para consultar todos. Vacío omite el filtro |
| `tipo` | Opcional: `VACACION`, `PERMISO`, `LICENCIA`; `TODAS`/`TODOS` o vacío omiten el filtro |
| `idpersonal` | Opcional; entero positivo. Sin valor consulta todos los empleados autorizados por el permiso actual. Una persona sin resultados devuelve una página vacía |
| `desde`, `hasta` | Opcionales, formato ISO `YYYY-MM-DD`; requieren orden válido cuando se indican ambos |
| `pagina` | Índice desde cero; por defecto 0, rango 0–100000 |
| `tamano` | Por defecto 20; mínimo 1, máximo 100 |

Las fechas filtran por **intersección inclusiva**: fin de solicitud mayor o igual a `desde` e inicio menor o igual a `hasta`. Se puede indicar una sola frontera. No exige que el inicio caiga dentro del período. Las fechas ausentes no producen una intersección válida cuando se filtra por esa frontera.

Filtros de estado/tipo reconocen espacios y mayúsculas/minúsculas. Valores desconocidos, paginación fuera de límites, personal no positivo y rangos invertidos devuelven 400. El resultado se ordena por `idrequest` ascendente para paginar de forma estable; no recibe expresiones de ordenación arbitrarias.

La paginación y filtros se ejecutan en base de datos. El personal se carga junto con la página mediante `EntityGraph`, evitando consultas de ficha por fila. Contenido y total usan una transacción de lectura `REPEATABLE_READ`. Las rutas antiguas de consulta por persona/estado conservan sus contratos.

### 47.2 Pantalla y decisiones

La acción «Abrir bandeja general de solicitudes» ofrece filtros por empleado, estado, tipo, fechas y tamaño de página (10/20/50), resultados con identificación mínima, contador y botones anterior/siguiente. Los usuarios con permiso de consulta pueden abrir la bandeja y ver detalles; los de escritura pueden aprobar o rechazar solicitudes pendientes. También permite abrir los saldos y solicitudes del empleado de una fila sin cambiar los filtros de la bandeja.

Las decisiones reutilizan los endpoints y reglas transaccionales existentes. La confirmación identifica solicitud y empleado, el rechazo exige motivo y el actor procede del JWT. Tras una decisión correcta o un conflicto 409 se actualizan la bandeja y los datos del empleado seleccionado; la decisión no cambia automáticamente la selección de empleado. Si desaparece la última fila de una página, vuelve a la última página disponible. Cambiar filtros/cerrar la bandeja invalida respuestas anteriores; se bloquean envíos y consultas duplicados durante su procesamiento.

Se corrigió una brecha relacionada en aprobación: un tipo histórico como `" vacacion "` debe interpretarse como vacaciones, descontar saldo y persistir su tipo canónico. Un tipo desconocido se rechaza con 409 antes de aprobar o registrar movimientos. Esto conserva el catálogo existente, sin introducir nuevos tipos ni reglas de días.

### 47.3 Evidencia y seguimiento

- **82 pruebas backend aprobadas:** 57 de servicios/contrato/seguridad y 25 de PostgreSQL. La entrega añade filtros combinados, fechas que se cruzan con el rango y fronteras individuales, paginación estable, límites, bandeja de varios empleados, desaparición de pendientes tras resolver, conservación de terminales, DTO limitado, acceso de consulta y tipos históricos al aprobar.
- **31 pruebas frontend aprobadas:** incluye acceso de lectura, consultas duplicadas, descarte de respuestas antiguas, decisión para otro empleado, refresco tras éxito/409, página vaciada, rangos invertidos y navegación a saldos/historial.
- Build Angular de desarrollo correcto y compilación backend durante la suite. Los avisos anteriores de archivos Angular sin uso y bindings SLF4J múltiples permanecen.
- PostgreSQL temporal detenido al finalizar. No se ejecutó el servidor ERP ni se modificó la base productiva.

Verificación: `./scripts/test-rrhh-postgres.ps1` en backend; `npm run test:rrhh` y `npx ng build --configuration development` en frontend. El inventario ahora contiene **103 endpoints declarados**.

No requiere una nueva migración. Para desplegar las entregas acumuladas deben aplicarse las dos migraciones en el orden indicado en la sección 45. La bandeja no incorpora aprobación multinivel, delegaciones, ámbitos por dependencia ni acciones masivas; RRHH-15 permanece **EN DESARROLLO** hasta resolver D04 e implementar esos controles. RRHH-34 sigue EN VALIDACIÓN para estas cinco entregas; falta aceptación funcional.

## 48. Sexta entrega: historial de solicitudes y evidencia

Estado: **EN VALIDACIÓN**. Implementación local en backend y frontend; aceptación de RRHH y despliegue pendientes. Avanza la consulta de evidencia de RRHH-05 y la revisión de solicitudes de RRHH-15.

### 48.1 Historial implementado

Nueva ruta `GET /api/th-leave/requests/{idrequest}/historial`, protegida por JWT, usuario activo y permiso vigente de lectura. Respuesta con:

- `solicitud`: información actual de la solicitud y nombre/ID del empleado mediante el DTO limitado de la bandeja. No serializa la ficha completa ni sus contactos.
- `consultado_en`: fecha/hora de consulta.
- `eventos`: registros reales de `th_audit_log` para la entidad exacta `TH_LEAVE_REQUEST` y ese ID de solicitud. Incluye ID de evento, acción, detalle/motivo, usuario y fecha; ordena cronológicamente, por ID en empates y deja fechas ausentes al final. Conserva acciones desconocidas para no ocultar evidencia.
- `movimientos`: únicamente movimientos del libro vinculados a esa solicitud, ordenados por ID. Incluye consumo/reintegro, actor, fecha, motivo, ejercicio/saldo y referencia al consumo original. No añade aperturas ni ajustes generales del saldo que no están vinculados a la solicitud.
- `advertencias`: eventos esperados ausentes para el estado vigente, eventos sin fecha/usuario, estados desconocidos y consumo/reintegro ausente cuando se requiere para vacaciones aprobadas/revertidas.

La consulta se realiza en una transacción de lectura `REPEATABLE_READ` para conservar la misma instantánea en solicitud, eventos y movimientos. Un ID no positivo devuelve 400 y una solicitud inexistente devuelve 404. La respuesta usa `Cache-Control: no-store`.

No se crean eventos a partir de los metadatos de aprobación, no se inventan fechas/actores y no se reconstruyen consumos anteriores. Las advertencias identifican evidencia pendiente; una lista sin advertencias no certifica todos los derechos laborales ni la integridad de procesos externos. La verificación financiera de la sección 46 sigue siendo la herramienta para comprobar saldo/secuencia del libro.

### 48.2 Pantalla

«Detalle» en la lista por empleado y en la bandeja abre información vigente e historial, también para usuarios de lectura. La pantalla presenta eventos con acción en español, usuario, fecha y motivo, y los movimientos financieros con signo, saldos anterior/posterior y origen del reintegro. Muestra advertencias o ausencia de evidencia, permite actualizar el historial y cerrar el detalle.

Una respuesta correcta actualiza el estado y metadatos mostrados desde el servidor. Cambiar solicitud, refrescar datos, cerrar el detalle o invalidar el detalle de la bandeja descarta respuestas antiguas. Se bloquean consultas duplicadas para la misma solicitud mientras carga. Los errores del historial se muestran dentro del detalle y permiten reintentar sin borrar mensajes de otras operaciones.

### 48.3 Verificación y seguimiento

- **93 pruebas backend aprobadas:** 64 de servicios/contrato/seguridad y 29 de PostgreSQL. Incluye permiso de lectura, rechazo de acceso, DTO limitado, 400/404, orden y empates de eventos, fechas ausentes, acciones desconocidas y falta de evidencia histórica.
- La integración persiste auditoría real a través del repositorio dentro de la transacción del servicio de solicitudes: verifica creación/aprobación/reversión, motivos de rechazo/cancelación, exclusión de otras solicitudes/entidades y ausencia de evento de aprobación/movimientos después de rollback. No basta con comprobar invocaciones de un mock.
- **38 pruebas frontend aprobadas:** incluye consulta de lectura, estado vigente del servidor, respuestas tardías, cambio de persona/solicitud, cierre de bandeja, prevención de duplicados, reintento y aislamiento de mensajes de error.
- Build Angular de desarrollo correcto y compilación backend durante la suite. Persisten los avisos anteriores de archivos Angular sin uso y bindings SLF4J múltiples.
- PostgreSQL temporal detenido al finalizar; no se ejecutó el servidor ERP ni se modificó la base productiva.

Comandos: `./scripts/test-rrhh-postgres.ps1`, `npm run test:rrhh` y `npx ng build --configuration development`. El inventario actualizado contiene **104 endpoints declarados**.

Esta entrega no necesita una nueva migración. Reutiliza `th_audit_log`, definido en el script base `2026-05-05_add_th_leave_tables.sql` y usado por los comandos de solicitudes existentes; las dos migraciones de movimientos/ajustes siguen requiriéndose en el orden de la sección 45.

RRHH-05 sigue **EN DESARROLLO**: se consulta evidencia del flujo de solicitudes, pero faltan auditoría transversal, correlación y controles de otros procesos. La ruta global de compatibilidad `/api/th-audit` mantiene su implementación actual; esta entrega no acredita su autorización ni sustituye la revisión de sus consumidores. RRHH-04/15 siguen pendientes de ámbitos finos y D04. RRHH-34 queda EN VALIDACIÓN para las seis entregas, pendiente aceptación y despliegue.

## 49. Séptima entrega: estado del saldo, versión y auditoría

Estado: **EN VALIDACIÓN**. Backend y frontend implementados localmente; aceptación funcional y despliegue pendientes. Avanza el control del saldo y su trazabilidad de RRHH-13/05, utilizando el campo de actividad existente sin definir acumulación ni reglas D03.

### 49.1 Actividad y concurrencia

La nueva acción permite activar/inactivar un saldo con motivo obligatorio (hasta 2000 caracteres), actor obtenido del JWT y permiso vigente de escritura. El saldo se bloquea y se compara la versión recibida con la versión persistida. Una versión desactualizada devuelve 409 y no modifica actividad, importes ni auditoría.

`ThLeaveBalance` incorpora `version` gestionada por JPA mediante `@Version`. Consumir, reintegrar, ajustar o cambiar actividad incrementa esta versión cuando cambia el saldo. Las lecturas existentes la incluyen; el alta no admite que el cliente la establezca. Este control corresponde a las operaciones JPA y no sustituye la revisión de escrituras externas ni la conciliación del libro.

Inactivar conserva asignados, usados, disponibles y movimientos. Impide nuevas aprobaciones de vacaciones y ajustes, manteniendo consulta, apertura del libro y reintegros del consumo registrado. No cancela ni rechaza solicitudes pendientes automáticamente. Reactivar exige importes no nulos/no negativos que cuadren, año válido, un único saldo para el ejercicio y coincidencia con el último movimiento cuando existe. Un saldo legado válido sin libro puede activarse; esto no reconstruye consumos ni certifica sus derechos históricos.

Si el estado solicitado ya coincide y la versión es vigente, responde sin modificar ni crear un evento. Reintentar con la versión anterior después de un cambio devuelve 409; no repite el cambio ni su auditoría. No se implementan cierres de ejercicio, distribución anual ni una matriz nueva de aprobadores.

### 49.2 API e historial

| Método | Ruta | Contrato |
|---|---|---|
| POST | `/api/th-leave/balances/{idbalance}/estado` | JSON `{activo, version, motivo}`; escritura requerida; 204 al completar; 400 si faltan datos, 404 si no existe y 409 ante versión antigua o activación no conciliable |
| GET | `/api/th-leave/balances/{idbalance}/historial-estado` | Estado/version actuales, ID de saldo/persona, año y eventos reales de `TH_LEAVE_BALANCE`; consulta requerida, `Cache-Control: no-store` |

Los cambios efectivos registran `ACTIVATE`/`DEACTIVATE` con usuario, fecha, motivo y estado/versiones anteriores y posteriores. Saldo, versión y evento se confirman en la misma transacción: un fallo de auditoría revierte todo. El historial usa una misma instantánea de lectura y orden cronológico con desempate por ID; no devuelve la ficha completa ni fabrica eventos de cambios antiguos.

La tabla de saldos muestra su estado y permite activar/inactivar a operadores de escritura. Usuarios de consulta pueden abrir el historial de estado. La confirmación explica los efectos, el motivo es obligatorio y se envía la versión consultada. Un 409 conserva el mensaje y actualiza datos; consultas antiguas del historial se descartan al cambiar de saldo/refrescar. El historial muestra advertencias de ausencia de registros anteriores y permite cerrar el panel.

### 49.3 Migración requerida

Antes de arrancar esta versión de backend, aplicar las migraciones acumuladas en este orden, sobre el esquema base del módulo:

1. `src/main/resources/sql/2026-10-06_rrhh_leave_movements.sql`.
2. `src/main/resources/sql/2026-10-06_rrhh_leave_balance_adjustments.sql`.
3. **`src/main/resources/sql/2026-10-06_rrhh_leave_balance_version.sql`**.

La tercera agrega `version BIGINT NOT NULL DEFAULT 0` y valida su signo, asigna cero a versiones ausentes y conserva los importes existentes. También asegura que `th_audit_log.detalle` sea `TEXT`, como el script base, para registrar motivos de hasta 2000 caracteres sin truncamiento. La entidad de auditoría se alinea con ese tipo. No genera eventos históricos.

Esta secuencia actualiza las instrucciones de despliegue de entregas anteriores. Se requiere despliegue coordinado: el backend nuevo espera la columna y las escrituras de una versión antigua de la aplicación no usan el control JPA incorporado. No se aplicó ninguna migración ni cambio en producción.

### 49.4 Evidencia y pendientes

- **106 pruebas backend aprobadas:** 71 de servicios/contrato/seguridad y 35 de PostgreSQL. Incluye estado/versión/motivo obligatorios, actor del token, acceso de lectura/escritura, rechazo de versión en creación, activación/inactivación e historial.
- Integración: dos inactivaciones simultáneas aplican un único cambio; aprobación contra inactivación tiene un único resultado válido; auditoría fallida revierte estado/version/evento; saldo inactivo bloquea consumos/ajustes y admite reintegro; diferencias externas o saldos duplicados impiden reactivar; ajustes invalidan versiones anteriores; un estado ya vigente no agrega eventos. Se verificó un motivo largo sin truncamiento.
- La migración se ejecuta dos veces en PostgreSQL temporal sobre una fila legada sin columna de versión, comprobando versión cero y conservación de disponibles. También se prueba ampliar una columna de detalle `VARCHAR(255)` a `TEXT`.
- **42 pruebas frontend aprobadas:** envío de versión sin actor del cliente, motivo/acceso, clics duplicados, refresco ante 409, consulta de lectura y descarte de respuestas antiguas.
- Build Angular de desarrollo correcto y compilación backend durante la suite; siguen los avisos previos de archivos Angular sin uso y bindings SLF4J múltiples. PostgreSQL temporal detenido al terminar.

Comandos: `./scripts/test-rrhh-postgres.ps1`, `npm run test:rrhh` y `npx ng build --configuration development`. El inventario ahora contiene **106 endpoints declarados**. La evidencia corresponde a RRHH, no a toda la suite del ERP ni a aceptación funcional.

RRHH-13 y RRHH-05 siguen **EN DESARROLLO**: falta acumulación/distribución anual según D03, conciliación histórica con evidencia, auditoría transversal y correlación. RRHH-04/15 siguen dependiendo de D04 para ámbitos y niveles. RRHH-34 queda EN VALIDACIÓN para estas siete entregas, pendiente aceptación y despliegue.

## 50. Octava entrega: exportación de la página de solicitudes

Estado: **EN VALIDACIÓN**. Avanza el reporte operativo RRHH-30 sobre la bandeja existente. No cambia estados, saldos ni reglas de aprobación.

### 50.1 API y alcance

`GET /api/th-leave/requests/bandeja.csv` requiere permiso de consulta y acepta los mismos filtros y paginación de `/requests/bandeja`: `estado`, `tipo`, `idpersonal`, `desde`, `hasta`, `pagina` (desde cero) y `tamano` (1–100). Reutiliza la consulta de página con instantánea de lectura, validación, intersección inclusiva de fechas y orden por ID. No exporta todas las páginas ni carga toda la tabla.

El archivo contiene página (desde uno), tamaño y total de solicitudes, identificadores, nombres, tipo, estado, fechas, días, motivos y metadatos de resolución de la proyección limitada. No incluye la ficha completa. Para una página vacía devuelve solamente la cabecera. Responde con UTF-8/BOM, delimitador `;`, adjunto y `Cache-Control: no-store`. Escapa comillas, delimitadores y saltos de línea y neutraliza fórmulas en campos de texto, conservando días como números.

### 50.2 Frontend

El botón **Exportar página CSV** está disponible con resultados cargados para usuarios de consulta y escritura. Envía filtros actuales, número y tamaño de la página. La pantalla informa que la descarga vuelve a consultar datos y puede reflejar cambios posteriores a la búsqueda. No promete una copia de la instantánea anterior.

Bloquea clics duplicados; cambiar filtros, cerrar o refrescar la bandeja descarta descargas pendientes. Libera la URL temporal y elimina el enlace de descarga. Los errores JSON recibidos como Blob se muestran con su explicación y permiten reintentar.

### 50.3 Validación y despliegue

- Backend: **110 pruebas aprobadas**, 75 de servicios/contrato/seguridad y 35 de PostgreSQL temporal. Incluye formato CSV, texto con fórmulas/comillas/saltos de línea, números, página vacía, permisos y delegación exacta de filtros/paginación.
- Frontend: **45 pruebas aprobadas**, incluyendo exportación con lectura, prevención de duplicados, liberación de URL, descarte de respuestas antiguas y errores Blob.
- Compilación Angular de desarrollo y backend correctas. Comandos: `./scripts/test-rrhh-postgres.ps1`, `npm run test:rrhh`, `npx ng build --configuration development`.

No requiere una nueva migración; siguen siendo necesarias las tres de la sección 49 en ese orden. Inventario actualizado: **107 endpoints declarados**. No se modificó producción. Aceptación y despliegue pendientes; RRHH-30 continúa EN DESARROLLO porque faltan otros reportes del módulo.

## 51. Novena entrega: calendario de ausencias aprobadas

Estado: **EN VALIDACIÓN**. Implementación local de backend/frontend del calendario descrito en la sección 9.5, con vistas mensual y semanal, filtros por empleado y tipo. No introduce reglas de acumulación, días laborables ni nuevas autorizaciones por dependencia.

### 51.1 Consulta acotada

`GET /api/th-leave/requests/calendario` exige permiso de consulta y fechas ISO `desde`/`hasta`; admite `tipo`, `idpersonal` y `pagina` desde cero. Rechaza rangos invertidos, ausentes, de más de 42 días o fuera de 1900–9999, así como filtros y paginación inválidos. El tamaño fijo es 100 solicitudes por página.

Reutiliza la consulta paginada con estado **APROBADA**, intersección inclusiva de fechas, normalización de valores legados y orden estable por ID. La lectura de filas y total utiliza una instantánea de PostgreSQL; devuelve la proyección limitada de la bandeja y `Cache-Control: no-store`. No carga todas las páginas ni genera días de ausencia en la base.

Solicitudes pendientes, rechazadas, canceladas o revertidas quedan excluidas. Una solicitud que comienza antes del rango y termina dentro o después aparece si lo intersecta. Consultar no cambia estados, movimientos ni importes.

### 51.2 Pantalla

La vista mensual muestra los días del mes de la fecha seleccionada; la semanal usa lunes a domingo, incluso cruzando meses o años. La aritmética usa fechas UTC para conservar fechas civiles. Los días incluyen nombre de la semana y las solicitudes de la página que los intersectan, con acceso al detalle e historial existente.

La pantalla muestra total de solicitudes, cantidad cargada y navegación de páginas, y advierte que **los días sin eventos de una página no acreditan disponibilidad ni asistencia**. Sólo representa aprobaciones registradas, sin cálculo de feriados o días laborables. No se agrega filtro por dependencia porque su ámbito y maestro siguen pendientes.

Cambiar filtros o cerrar descarta respuestas anteriores. Se bloquean consultas duplicadas, se muestran errores propios del calendario y se permite reintentar. Una mutación local exitosa o un conflicto 409 invalida el calendario para volver a consultar; no conserva aprobaciones potencialmente antiguas. También se corrige una tilde en el botón de exportación de la entrega anterior.

### 51.3 Evidencia y pendientes

- **116 pruebas backend aprobadas:** 79 de servicios/contrato/seguridad y 37 de PostgreSQL. Incluye límites del rango, filtros, lectura/autorización, fechas obligatorias, intersección de aprobaciones, exclusión tras reversión y 101 filas legadas divididas en páginas de 100 y 1 sin repetir IDs.
- **51 pruebas frontend aprobadas:** incluye febrero bisiesto, semana diciembre/enero, extremos de años admitidos, intersección inclusiva, lectura, errores/reintento, prevención de duplicados, respuestas tardías, paginación e invalidación tras mutaciones.
- Compilación backend y Angular de desarrollo correctas. Comandos: `./scripts/test-rrhh-postgres.ps1`, `npm run test:rrhh`, `npx ng build --configuration development`. PostgreSQL temporal detenido al finalizar.

No requiere una nueva migración; se mantienen las tres de la sección 49 en ese orden. Inventario: **108 endpoints declarados**. No se modificó producción. La aceptación de RRHH y el despliegue siguen pendientes; los ámbitos por dependencia, reglas D03 y aprobadores D04 no se consideran resueltos por esta entrega.

## 52. Décima entrega: exportación anual del libro de movimientos

Fecha: **7 de octubre de 2026**. Estado: **EN VALIDACIÓN**. Avanza el reporte operativo RRHH-30 y la evidencia del libro de RRHH-13, sin cambiar importes ni reglas de vacaciones.

### 52.1 API y contenido

`GET /api/th-leave/movements/persona/{idpersonal}/libro.csv?anio=2026` exige permiso de consulta, ID positivo y año obligatorio entre 1900 y 9999. Devuelve todos los movimientos registrados de ese empleado/año, ordenados por ID de movimiento descendente, utilizando la consulta de lectura existente. No exporta otros empleados ni otros años; no permite omitir el año para descargar el histórico completo.

Columnas: empleado, movimiento, saldo, año, solicitud, movimiento origen, tipo, días con signo, disponibles antes/después, usados antes/después, asignados, actor, fecha y motivo. El vínculo de reintegro conserva el ID del consumo original. No devuelve ficha personal, clave interna del ajuste ni documentos.

Responde con adjunto, UTF-8/BOM, delimitador `;` y `Cache-Control: no-store`. Escapa comillas/saltos de línea y neutraliza fórmulas en texto, manteniendo importes negativos como números. Un libro vacío, incluido un ID positivo sin registros, entrega sólo cabecera; no acredita existencia del empleado ni derechos históricos. La apertura sigue representando el saldo vigente al iniciar el libro y no reconstruye consumos anteriores.

### 52.2 Frontend

**Exportar libro anual CSV** está disponible con permiso de consulta, empleado y año seleccionados. Exporta el año completo, no sólo la página visible; el texto de ayuda explica que consulta datos actuales al descargar.

Bloquea clics duplicados y descarta respuestas tras cambiar año, empleado o refrescar datos, incluyendo refrescos posteriores a mutaciones. Elimina el enlace y libera la URL temporal al descargar. Interpreta errores JSON recibidos como Blob para mostrar el motivo y permitir reintentar.

### 52.3 Evidencia y pendientes

- **122 pruebas backend aprobadas:** 84 de servicios/contrato/seguridad y 38 de PostgreSQL temporal. Incluye año obligatorio/límites, lectura y acceso denegado, formatos/escape, importes negativos, libro vacío y consumo/ajuste/reintegro reales con vínculos conservados y exclusión de otro año. Exportar no modifica movimientos.
- **54 pruebas frontend aprobadas:** incluye exportación con lectura, año obligatorio, prevención de duplicados, descarte por refresco/cambio de año, liberación de URL y mensajes Blob.
- Compilación backend y Angular de desarrollo correctas. Comandos: `./scripts/test-rrhh-postgres.ps1`, `npm run test:rrhh`, `npx ng build --configuration development`. PostgreSQL temporal detenido al finalizar. Persisten los avisos previos de SLF4J y archivos Angular sin uso.

Inventario: **109 endpoints declarados**. No requiere una nueva migración: siguen las tres de la sección 49. La consulta anual materializa el libro de ese año, como la lectura existente; no implementa streaming ni descarga masiva entre empleados. No se modificó producción. RRHH-30/13 continúan EN DESARROLLO por sus restantes reportes y reglas; aceptación y despliegue pendientes.
