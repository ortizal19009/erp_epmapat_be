# Bloqueo al arrancar el historial de abonados

Log recibido: 2026-11-10 21:26, Hikari Apparent connection leak detected en AbonadoHistorialSchemaMigration.run.

Se confirmó en PostgreSQL que la migración estaba esperando `DROP TRIGGER IF EXISTS abonado_historial_update ON abonados`. Su sesión 1733890 estaba bloqueada por 1732943, una exportación COPY de respaldo con espera ClientWrite. Las consultas posteriores de abonados quedaron detrás de la petición de bloqueo exclusivo de la migración.

Se canceló únicamente la sentencia DROP TRIGGER de la sesión 1733890, verificando previamente PID, base, texto SQL y estado de espera. No se terminó la sesión del respaldo ni se modificaron abonados. Después no quedaron esperas Lock y el trigger de historial seguía presente. El health del backend respondió 404 tras abortarse el arranque; necesita redesplegar el WAR corregido.

Corrección: el runner comprueba tabla, índice, funciones y trigger existentes. Si están instalados, retorna sin repetir DDL. Cuando falta instalar, limita la espera de locks a 5 segundos y cada sentencia a 30 segundos. El script crea el trigger solo si falta; ya no lo elimina para volver a crearlo.

No se aumentó el umbral de Hikari ni se ocultó la advertencia. Una conexión retenida durante una espera prolongada puede disparar esa advertencia sin que exista una fuga permanente.

Validación: prueba unitaria de omisión de DDL y prueba con PostgreSQL local, transacción de solo lectura y un bloqueo ACCESS SHARE que simula el de un respaldo. El runner terminó sin esperar un bloqueo exclusivo. Ambas pruebas aprobadas, sin cambios persistentes.

Despliegue: sustituir `deploy/erp_epmapat-v0.1.war` por el nuevo artefacto y recrear el servicio con `docker compose --env-file .env.prod up -d --force-recreate erp_epmapat_be`. No basta reiniciar el artefacto anterior mientras el respaldo siga reteniendo locks.
