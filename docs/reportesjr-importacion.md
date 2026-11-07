# Importar los reportes existentes al modulo

Se encontraron **31 reportes distintos** en la raiz de `src/main/resources/reports`: los pares JRXML/Jasper se registran una sola vez, con prioridad al JRXML actual. Las imagenes no son reportes. `nacionalidades` solo tiene un compilado y queda con `jrxml_path = NULL`.

## Pasos

1. Aplicar `src/main/resources/sql/2026-11-05_reportesjr_archivos_opciones.sql` si aun no se aplico.
2. Si una ejecucion anterior dejo la sesion con error `25P02`, ejecutar `ROLLBACK;` por separado.
3. Ejecutar **completo** `src/main/resources/sql/2026-11-05_insert_catalogo_reportesjr.sql`, desde `BEGIN;` hasta `COMMIT;`. Ya no requiere editar `v_idrepoxopcion`. Si existe, reutiliza la opcion **Reportes JR / Catalogo Jasper importado**; si falta, la crea con un codigo numerico libre entre 99001 y 99999. Este codigo identifica un catalogo de revision, no un modulo del ERP. El usuario creador se toma del primer registro existente de `repoxopcion` con `usucrea` informado.
4. La carpeta `src/main/resources/reports/catalogo-importado` ya contiene los JRXML y los compilados preparados. Con el perfil local no hay que moverla. Para otro servidor copiarla dentro de `REPORTESJR_DIRECTORY`, manteniendo el subdirectorio `catalogo-importado`.
5. El script es transaccional y conserva los registros que ya existen por nombre (tambien si cambia solo mayusculas/minusculas). No reemplaza binarios, parametros ni asignaciones de reportes existentes. Estos se pueden migrar con el boton del modulo.
6. Abrir Reportes JR y buscar por el codigo del catalogo creado. Desde la edicion se pueden asignar las opciones adicionales que correspondan. Las opciones contables existentes se conservan.

Para consultar el destino creado:

```sql
SELECT idrepoxopcion, codigo, opcion, nombre
FROM repoxopcion
WHERE opcion = 'Reportes JR' AND nombre = 'Catalogo Jasper importado';
```

La creacion de este catalogo registra los reportes para administrarlos. No conecta por si sola los modulos de recaudacion, emisiones o tramites al selector dinamico de reportes.

## Metodos y parametros

Los parametros se obtuvieron del Jasper compilado con el mismo formato que usa el modulo: `clase|anchoCampo|orden`. Se conservan las propiedades de cada plantilla.

Se asigna metodo **1** cuando hay consulta SQL principal y **3** a las plantillas sin SQL. Esta ultima es una clasificacion inicial por su estructura: necesitan que el consumidor envie datos, o cambiar a metodo 2 si se integran mediante Beans. Una plantilla SRI no se convierte en un informe SQL por registrarla en el catalogo.

`EmisionInicial` y `EmisionInicialConEliminadas` declaran `ORACLE_REF_CURSOR` como `interface java.sql.ResultSet`; se conserva el metadato original, pero no es un parametro escalar que el usuario pueda rellenar desde un formulario normal.

## Correcciones y limites de la revision

Se corrigieron dos errores que impedian compilar:

- `report_template.jrxml`: mover `subDataset` antes de los parametros, como requiere el esquema Jasper. Sigue siendo una plantilla de ejemplo con bandas sin contenido.
- `RetencionElectronica.jrxml`: declarar los campos String `Codigo` y `Descripcion`, que ya estaban referenciados en las expresiones. No se cambio el calculo ni el diseno. El consumidor debe proporcionar esos campos; esta plantilla conserva estructura y parametros heredados de una factura y requiere validar su contenido funcional antes de usarla para comprobantes reales.

La revision compila los JRXML y vuelve a abrir cada compilado generado con JasperReports del proyecto. No ejecuta las consultas contra PostgreSQL ni genera comprobantes reales, y no ejecuta el INSERT. Las imagenes, fuentes, datos y parametros de cada reporte deben estar disponibles en su entorno de ejecucion.

Los originales se mantienen fuera de la carpeta de importacion. Las rutas del INSERT apuntan a copias independientes para que reemplazar/eliminar un reporte desde el modulo no borre la plantilla original utilizada por otros servicios.

## Regenerar

Desde la raiz del backend, en PowerShell:

```powershell
./mvnw.cmd -q '-Dtest=GenerarCatalogoReportesTest' '-Dgenerar.catalogo.reportes=true' test
```

Regenera el SQL, `docs/reportesjr-inventario.tsv` y las copias compiladas. El SQL regenerado tambien crea o reutiliza automaticamente la opcion del catalogo de revision.

## IDs automaticos desfasados

El INSERT sincroniza las secuencias de `reportesjr.idreporte` y `repoxopcion.idrepoxopcion` antes de insertar. Usa el mayor valor entre los IDs existentes y el ultimo valor de cada secuencia; no retrocede contadores ni cambia registros. Esto corrige el error `23505` sobre la clave primaria cuando existen IDs cargados previamente. Las tablas se bloquean para escritura durante la importacion. Los avances de secuencias en PostgreSQL no se revierten con un rollback, por lo que pueden quedar huecos entre IDs.
