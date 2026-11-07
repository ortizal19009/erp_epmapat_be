-- Separador de sentencias: ;; (ResourceDatabasePopulator y psql).
CREATE TABLE IF NOT EXISTS abonado_historial (
 idhistorial bigserial PRIMARY KEY,
 idabonado bigint NOT NULL,
 fecha timestamptz NOT NULL DEFAULT clock_timestamp(),
 usuario bigint,
 persona text,
 modulo text NOT NULL,
 observacion text,
 cambios jsonb NOT NULL
);;
CREATE INDEX IF NOT EXISTS abonado_historial_cuenta_fecha_idx
 ON abonado_historial(idabonado, fecha DESC, idhistorial DESC);;

-- Guarda etiquetas en el momento del cambio; los nombres futuros no alteran el historial.
CREATE OR REPLACE FUNCTION abonado_historial_valor(campo text, valor jsonb)
RETURNS text LANGUAGE plpgsql AS $body$
DECLARE nombre text;
BEGIN
 IF valor IS NULL OR valor = 'null'::jsonb THEN RETURN NULL; END IF;
 IF campo IN ('idcliente_clientes', 'idresponsable') THEN
  SELECT c.nombre INTO nombre FROM clientes c WHERE c.idcliente = (valor #>> '{}')::bigint;
 ELSIF campo = 'idcategoria_categorias' THEN
  SELECT c.descripcion INTO nombre FROM categorias c WHERE c.idcategoria = (valor #>> '{}')::bigint;
 ELSIF campo = 'idruta_rutas' THEN
  SELECT concat_ws(' - ', r.codigo, r.descripcion) INTO nombre FROM rutas r WHERE r.idruta = (valor #>> '{}')::bigint;
 END IF;
 RETURN CASE WHEN nombre IS NULL THEN valor #>> '{}' ELSE concat(valor #>> '{}', ' - ', nombre) END;
END
$body$;;

CREATE OR REPLACE FUNCTION registrar_abonado_historial()
RETURNS trigger LANGUAGE plpgsql AS $body$
DECLARE anterior jsonb; nuevo jsonb; diferencias jsonb; actor bigint; nombre text;
BEGIN
 anterior := to_jsonb(OLD) - ARRAY['usumodi','fecmodi','usucrea','feccrea','idabonado'];
 nuevo := to_jsonb(NEW) - ARRAY['usumodi','fecmodi','usucrea','feccrea','idabonado'];
 SELECT jsonb_agg(jsonb_build_object('campo', n.key,
   'anterior', abonado_historial_valor(n.key, anterior -> n.key),
   'nuevo', abonado_historial_valor(n.key, n.value)) ORDER BY n.key)
 INTO diferencias FROM jsonb_each(nuevo) n WHERE n.value IS DISTINCT FROM anterior -> n.key;
 IF diferencias IS NULL THEN RETURN NEW; END IF;
 actor := NULLIF(current_setting('epmapat.historial_usuario', true), '')::bigint;
 IF actor IS NULL AND (NEW.usumodi IS DISTINCT FROM OLD.usumodi OR NEW.fecmodi IS DISTINCT FROM OLD.fecmodi) THEN
  actor := NEW.usumodi;
 END IF;
 SELECT u.nomusu INTO nombre FROM usuarios u WHERE u.idusuario = actor;
 INSERT INTO abonado_historial(idabonado,usuario,persona,modulo,observacion,cambios)
 VALUES(NEW.idabonado,actor,nombre,
   COALESCE(NULLIF(current_setting('epmapat.historial_modulo', true), ''), 'OTROS'),
   NULLIF(current_setting('epmapat.historial_observacion', true), ''),diferencias);
 RETURN NEW;
END
$body$;;
DROP TRIGGER IF EXISTS abonado_historial_update ON abonados;;
CREATE TRIGGER abonado_historial_update AFTER UPDATE ON abonados
 FOR EACH ROW EXECUTE FUNCTION registrar_abonado_historial();;
