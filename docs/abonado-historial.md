# Historial de cambios de una cuenta

## Plan y alcance implementado

1. Persistir cada modificacion real de la cuenta, con valores anteriores y nuevos, fecha y hora, usuario, nombre del usuario, modulo y observacion.
2. Registrar automaticamente desde Modificar abonado y Tramites de agua. Identificar tambien suspensiones, habilitaciones y traslados desde Rutas.
3. Consultar el historial desde una pestana junto a Recargos en Detalles abonado, con tabla paginada (10 operaciones por pagina), estados de carga/error/vacio y boton Actualizar.
4. Validar categorias, responsables de pago, medidores, propietarios y rutas, ademas de cualquier otro campo de negocio que cambie en abonados.

## Adaptacion tecnica

- Migracion `src/main/resources/sql/2026-11-06_abonado_historial.sql`: tabla `abonado_historial`, indice por cuenta/fecha y trigger AFTER UPDATE de abonados. Se ejecuta al arrancar el backend mediante `AbonadoHistorialSchemaMigration`, dentro de una transaccion. Es idempotente y no borra historial al reinstalarse. El usuario de conexion necesita permisos de creacion de tabla, funcion y trigger, como en las migraciones existentes.
- El trigger compara OLD y NEW y conserva un registro por operacion con todos sus campos modificados; ignora los campos de auditoria y los guardados sin cambios. Usa la misma transaccion del UPDATE: un rollback tambien revierte el historial. Captura actualizaciones JPA y SQL, incluso fuera de los modulos adaptados.
- Los nombres de propietarios/responsables, categorias, rutas y operador se guardan en el momento del cambio. Los valores conservan tambien los identificadores; no dependen de nombres posteriores.
- Los modulos adaptados transmiten contexto local a la transaccion mediante `set_config(..., true)`; no se reutiliza entre solicitudes. El endpoint PUT existente admite el parametro opcional `modulo` (ABONADOS por defecto), preservando compatibilidad con clientes existentes.
- GET `/abonados/{idabonado}/historial?page=0&size=10`: content, number, size, totalElements, totalPages, first y last. Cuenta inexistente devuelve 404; paginacion negativa o tamano fuera de 1..100 devuelve 400. El orden es fecha e identificador descendentes. Las fechas se presentan en America/Guayaquil (UTC-05).
- No hay endpoints de edicion o eliminacion del historial. Los registros se agregan al guardar cambios reales de la cuenta.

## Limites

- El historial comienza al instalar la migracion. La auditoria generica anterior se conserva, pero no se convierte porque solo contiene estados anteriores y no permite reconstruir de forma fiable todos los valores nuevos y modulos.
- Crear una solicitud en Tramites de agua no equivale a cambiar la cuenta. El historial se genera cuando se aplica el cambio al abonado.
- Las modificaciones externas o flujos sin contexto se muestran como OTROS. Solo se atribuye el usuario de la fila cuando sus metadatos de modificacion cambian; en otro caso se muestra sin usuario informado para no atribuir un cambio al operador de una operacion anterior.
- La identidad del operador sigue el mecanismo existente del sistema (`usumodi` enviado por el cliente); esta adaptacion no introduce un sistema nuevo de autenticacion.

## Validacion y despliegue

- Pruebas de integracion PostgreSQL: `AbonadoHistorialIntegrationTest` (variable HISTORIAL_TEST_URL apuntando a una base de pruebas y usuario historial_test). Crea y elimina exclusivamente un esquema aleatorio de pruebas. Verifica migracion repetida, valores/nombres historicos, cambios multiples, no-op, rollback, contexto, nulos, paginacion y validacion de cuenta.
- Compilar backend y frontend, desplegar ambos y reiniciar backend para activar la migracion. Despues verificar con una cuenta de pruebas: modificar categoria, responsable, medidor, propietario y ruta desde ambos modulos y consultar la pestana. Una solicitud fallida no debe agregar filas.
- No se ha desplegado ni aplicado la migracion a la base del servidor 192.168.0.88 desde esta tarea.

Resultado local: compilacion Maven correcta; 22 pruebas correctas, sin omitidas (4 integracion de historial, 6 fotos, 3 tramite/cuenta y 9 gestion de rutas). TypeScript y plantillas Angular verificados con `ngc -p tsconfig.app.json --noEmit`. Pruebas PostgreSQL ejecutadas en un cluster temporal aislado del servidor de la empresa.
