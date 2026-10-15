# Reparación local de RRHH — 15 de octubre de 2026

## Diagnóstico
El log de las 14:42 corresponde al perfil `local`, puerto 9080. Spring Boot inició correctamente. Las consultas de vacaciones fallaban con SQLState 42703 por ausencia de `th_leave_requests.fecha_resolucion` y `th_leave_balances.version`.

La inspección directa confirmó que `localhost:5432/ErpEpmapaT` también carecía de `th_leave_movements`, `th_performance_reviews` y los metadatos nuevos del expediente digital.

## Corrección aplicada
Se respaldaron las tablas públicas `th_*` y `personal` en formato PostgreSQL custom antes de aplicar las migraciones existentes, en este orden:

1. `src/main/resources/sql/2026-10-08_rrhh_leave_production_repair.sql`
2. `src/main/resources/sql/2026-10-15_th_employee_files_metadata.sql`
3. `src/main/resources/sql/2026-10-15_th_performance_reviews.sql`

Destino comprobado: servidor PostgreSQL local `::1`, puerto 5432, base `ErpEpmapaT`. No se modificó producción.

Respaldo local: `target/rrhh-local-before-20261015-144528.dump`. Este archivo está en una carpeta de compilación; conservar una copia fuera de `target` antes de ejecutar `mvn clean` si se necesita retenerlo.

## Verificación
Las tres transacciones finalizaron con COMMIT. Se verificó la existencia de las columnas y tablas y se ejecutaron SELECT de las columnas afectadas sin errores. El índice del respaldo es legible con `pg_restore --list`.

Pendiente de comprobación en la sesión del navegador: recargar la ficha del empleado y abrir vacaciones, documentos y evaluaciones. No se realizó una prueba HTTP autenticada con el usuario conectado.

Los archivos SQL de resources no se ejecutan automáticamente; Hibernate está configurado con `ddl-auto=none`.
