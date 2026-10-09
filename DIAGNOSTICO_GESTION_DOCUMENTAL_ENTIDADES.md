# Error de catalogo documental: entidades no existe

Registro: 2026-10-08 13:53:39, GET /api/dependencies, SQLState 42P01.

`CatalogoDocumentalService.listDependencies` busca primero la entidad por `codigo` mediante `EntidadRepository.findByCodigo`. El mapeo JPA usa `entidades` sin esquema. PostgreSQL no puede resolver esa tabla. La consulta de dependencias aun no se ejecuta.

## Diagnostico en el entorno afectado

Ejecutar `src/main/resources/sql/2026-10-08_gestion_documental_diagnostico.sql` con la misma base y el mismo usuario de conexion que utiliza el backend. El script solo consulta metadatos.

- Si la base no es la esperada, corregir el datasource efectivo del despliegue antes de crear tablas.
- Si `public.entidades` existe pero la tabla sin esquema no se resuelve, revisar el `search_path` y los permisos del usuario de la aplicacion. El modulo tambien utiliza SQL con `public.entidades`, por lo que debe poder acceder a ese esquema.
- Si las tablas estan en otro esquema, confirmar el esquema contractual antes de duplicarlas: las consultas JDBC actuales esperan `public`.
- Si las tablas faltan en la base correcta, aplicar la migracion documental existente.

## Migracion pendiente

El esquema esta definido en `src/main/resources/sql/2026-03-27_gestion_documental_schema.sql`. `application.properties` configura `spring.jpa.hibernate.ddl-auto=none`; el backend no ejecuta automaticamente ese archivo por estar en la carpeta `sql`.

Desde la raiz del repositorio, con una conexion PostgreSQL configurada para la base correcta:

```powershell
psql -X -v ON_ERROR_STOP=1 -f src/main/resources/sql/2026-10-08_gestion_documental_diagnostico.sql
psql -X -v ON_ERROR_STOP=1 --single-transaction -f src/main/resources/sql/2026-03-27_gestion_documental_schema.sql
psql -X -v ON_ERROR_STOP=1 -f src/main/resources/sql/2026-10-08_gestion_documental_diagnostico.sql
```

La migracion requiere permisos para crear tablas, tipos y la extension `pgcrypto`. Usa `CREATE TABLE IF NOT EXISTS`, pero no adapta columnas de tablas preexistentes; revisar compatibilidad si la instalacion es parcial. No es necesario cambiar `ddl-auto`.

## Validacion funcional

La migracion crea estructura, sin insertar entidades ni dependencias. Consultar `GET /api/entities` y verificar que exista una entidad cuyo `codigo` coincida exactamente con el `entity_code` enviado por el cliente. Restaurar los catalogos oficiales o crearlos mediante los endpoints del modulo; no inventar codigos ni dependencias.

Repetir `GET /api/dependencies?entity_code=<codigo_existente>`: debe devolver el listado, que puede estar vacio si no hay dependencias. Si el codigo no existe, el servicio actual usa `orElseThrow()` y puede producir otro error, distinto del 42P01.

Los endpoints de busqueda de usuarios/personas requieren ademas columnas documentales en `public.usuarios` y `public.personas`. La migracion base no crea esas dos tablas; este diagnostico permite revisar sus columnas antes de probar esos endpoints.

No se ha ejecutado SQL contra produccion ni se ha confirmado el estado real de esa base. La causa confirmada por el registro es que `entidades` no resulta visible para la consulta; la migracion pendiente es una hipotesis que debe comprobarse con el diagnostico.
