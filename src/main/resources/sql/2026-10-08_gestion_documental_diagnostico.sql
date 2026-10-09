-- Solo lectura. Ejecutar en la misma base y con el mismo usuario del backend.
SELECT current_database() AS base_actual,
       current_user AS usuario_actual,
       current_setting('search_path') AS search_path,
       current_schemas(true) AS esquemas_visibles;

SELECT nombre,
       to_regclass(nombre) AS tabla_public,
       to_regclass(split_part(nombre, '.', 2)) AS tabla_resuelta_por_search_path
FROM (VALUES ('public.entidades'), ('public.dependencias'),
             ('public.tipos_documento'), ('public.documentos')) AS tablas(nombre);

-- Detecta tablas homonimas en otros esquemas sin consultar sus datos.
SELECT table_schema, table_name
FROM information_schema.tables
WHERE table_name IN ('entidades', 'dependencias', 'tipos_documento', 'documentos')
ORDER BY table_name, table_schema;

SELECT table_schema, table_name, column_name, data_type, udt_name
FROM information_schema.columns
WHERE table_schema = 'public'
  AND table_name IN ('entidades', 'dependencias', 'tipos_documento', 'usuarios', 'personas')
ORDER BY table_name, ordinal_position;
