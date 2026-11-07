# Reportes Jasper: archivos y opciones

## Despliegue

1. Ejecutar `src/main/resources/sql/2026-11-05_reportesjr_archivos_opciones.sql` en PostgreSQL antes de arrancar esta version. El proyecto tiene `ddl-auto=none`; el esquema no se actualiza automaticamente.
2. Definir `REPORTESJR_DIRECTORY` con la ruta absoluta de la carpeta Jasper existente del servidor y permisos de lectura/escritura para el backend. El perfil local usa `./src/main/resources/reports`, la carpeta Jasper existente. En otros perfiles el valor por defecto es `./reports`, relativo al proceso. Un WAR empaquetado requiere una carpeta externa persistente, no una ruta dentro del archivo WAR.
3. Desplegar backend y frontend juntos. Respaldar tanto la carpeta como la base de datos. Si hay varias instancias del backend, deben compartir la carpeta.

## Comportamiento

- Las cargas conservan JRXML y su compilado en archivos con nombres UUID. El backend compila el JRXML para mantener el ejecutable y sus parametros sincronizados. Se mantiene el formulario que recibe ambos archivos.
- La base de datos guarda `jrxml_path`, `jasper_path`, parametros, metadatos y opciones. Las rutas son relativas a la carpeta configurada. Las respuestas JSON ya no incluyen binarios Base64.
- SQL, Beans y Dataset leen desde disco. Los registros antiguos siguen leyendo sus binarios hasta migrarlos.
- Al editar un reporte antiguo, el boton **Mover archivos a la carpeta del backend** ejecuta `POST /reportesjr/{id}/migrar-archivos`. Se conservan los parametros y se vacian los binarios de ese registro solamente cuando la transaccion confirma. La operacion puede repetirse.
- Al reemplazar archivos se conserva la version previa hasta confirmar la transaccion. Si hay rollback se elimina la version nueva. Al eliminar el reporte se eliminan sus archivos despues del commit.

## Asignacion

Se usa el catalogo existente `repoxopcion`, que ya consultan los modulos mediante `/reportesjr/codigo/{codigo}`. La opcion principal conserva acceso y se pueden seleccionar opciones adicionales al crear o editar. Busquedas y conteos incluyen ambas. El filtro historico de metodos SQL/Beans en `/codigo` se conserva.

Los POST/PUT existentes aceptan el parametro opcional `opciones` con IDs separados por comas. Omitirlo conserva las asignaciones actuales; enviarlo reemplaza las adicionales. Tambien existe `PUT /reportesjr/{id}/opciones` con un array JSON de IDs.

Esta asignacion controla la disponibilidad en los listados por opcion. La autorizacion por usuario sigue siendo la del ERP; no agrega una comprobacion de permisos por usuario a los endpoints de ejecucion por nombre.

## Validacion

Compilacion Maven, build Angular de desarrollo y pruebas de almacenamiento (lectura antigua, rutas fuera de carpeta, commit y rollback). El SQL y el flujo completo con PostgreSQL deben verificarse en el entorno de despliegue.

## Listado y visualizacion

Las consultas cargan la opcion principal y las adicionales antes de cerrar la sesion Hibernate, ya que `open-in-view=false` y Jackson no fuerza la carga de relaciones. Se eliminan duplicados en Hibernate sin aplicar DISTINCT sobre las columnas JSON en PostgreSQL.

El listado permite abrir el detalle pulsando la fila o el icono de ojo. El modal muestra metadatos, rutas, opciones y parametros; para metodo SQL incluye un formulario de parametros y PDF integrado. Los parametros de objetos internos se muestran como informacion y no se envian como texto. Las plantillas Beans/Dataset requieren los datos del modulo consumidor. El acceso a Imprimir / Exportar se mantiene en el modal.

Validacion adicional: prueba JPA sobre PostgreSQL temporal que serializa con la sesion cerrada y varias opciones por reporte, build Angular y cuatro pruebas en ChromeHeadless del filtro, parametros y ciclo de vida del PDF. La prueba JPA requiere `REPORTESJR_TEST_JDBC_URL` apuntando exclusivamente a una base temporal (crea/elimina las tablas del catalogo).
