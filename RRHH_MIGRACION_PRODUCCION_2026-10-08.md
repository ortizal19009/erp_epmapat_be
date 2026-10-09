# Corrección del esquema TH Leave en producción

Revisión: 2026-10-08. Base consultada en modo de solo lectura: `192.168.0.46:5432/ErpEpmapaT`, esquema `public`.

El error `SQLState 42703: column thleavebal0_.version does not exist` confirma que el backend desplegado exige una columna que todavía no existe en la base. El parámetro `spring.jpa.hibernate.ddl-auto=none` impide que Hibernate la cree; guardar SQL en `src/main/resources/sql` o desplegar el WAR no ejecuta esos archivos automáticamente.

La consulta actual del esquema confirma:

| Elemento | Estado |
|---|---|
| `th_leave_balances.version` | Ausente |
| `th_leave_requests.resuelto_por` | Ausente |
| `th_leave_requests.fecha_resolucion` | Ausente |
| `th_leave_requests.motivo_resolucion` | Ausente |
| `th_leave_movements` | Tabla ausente |
| `th_audit_log.detalle` | Ya es `text` |

## Script preparado

`src/main/resources/sql/2026-10-08_rrhh_leave_production_repair.sql` reúne, en este orden, las tres migraciones originales:

1. `2026-10-06_rrhh_leave_movements.sql`.
2. `2026-10-06_rrhh_leave_balance_adjustments.sql`.
3. `2026-10-06_rrhh_leave_balance_version.sql`.

La ejecución conjunta utiliza una sola transacción, comprueba la base y las tablas necesarias, fija `search_path=public` y verifica los campos antes de confirmar. Si alguna sentencia falla y se ejecuta con `ON_ERROR_STOP=1`, la conexión termina y PostgreSQL revierte la transacción sin dejar migraciones parciales. El límite de espera por bloqueos es de cinco segundos.

Agrega estructura y una versión inicial de cero. No cambia los días asignados, disponibles o usados ni reconstruye movimientos históricos. Los históricos previos seguirán sin movimientos nuevos; esto es deliberado y coincide con las migraciones originales.

## Prueba realizada

Se ejecutó en PostgreSQL temporal, puerto local 55441, sobre tablas iniciales sin los campos faltantes. Se creó después un movimiento `AJUSTE` válido y se repitió el script. Ambas ejecuciones finalizaron correctamente:

- Saldo de prueba conservado: `20.00`.
- Versión inicial: `0`.
- Movimiento existente conservado: uno.
- La instancia temporal fue detenida después de la prueba.

Evidencia local: `target/rrhh-repair-test.log`. No se ejecutó esta migración en producción.

## Aplicación pendiente de autorización

Antes de ejecutar, obtener un respaldo verificable y coordinar una ventana sin escrituras del módulo de permisos/vacaciones. La operación toma bloqueos de esquema; si no los consigue dentro del límite, debe revisarse el error y reintentarse en una ventana adecuada, sin retirar los límites indiscriminadamente.

Desde el directorio del backend, con un usuario autorizado para modificar el esquema:

```bash
psql -X -h 192.168.0.46 -p 5432 -U postgres -d ErpEpmapaT -W -v ON_ERROR_STOP=1 -f src/main/resources/sql/2026-10-08_rrhh_leave_production_repair.sql
```

La contraseña se solicita interactivamente; no debe copiarse dentro de la sentencia ni compartirse por chat. El script imprime los elementos verificados al terminar.

Después de confirmar la migración, volver a consultar saldos y solicitudes con un token válido en el backend `192.168.0.165:8080` y verificar que desaparezcan los errores de columnas/tablas ausentes. Cambiar CORS o aumentar tiempos de conexión no corrige este error de esquema. No hace falta redesplegar el WAR para que PostgreSQL exponga una columna recién creada, siempre que el backend ya esté ejecutando la revisión compatible.

Si el usuario afirma haber ejecutado anteriormente las tres migraciones y estos elementos continúan ausentes, se debe revisar la salida exacta de ejecución y `SELECT current_database(), current_schema(), inet_server_addr();`: pueden haberse ejecutado contra otra base/esquema, haber fallado o haberse revertido. La ausencia actual confirma el resultado pendiente, no cuál de esas situaciones ocurrió.
