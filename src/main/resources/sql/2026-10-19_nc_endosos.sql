-- Aplicar en ventana de mantenimiento, con respaldo y UAT previo. NO se ejecuta automaticamente.
BEGIN;
DO $$ BEGIN
 IF EXISTS (SELECT 1 FROM ntacredito WHERE valor::numeric <> round(valor::numeric,2)
 OR coalesce(devengado,0)::numeric <> round(coalesce(devengado,0)::numeric,2)) THEN
 RAISE EXCEPTION 'Existen importes con mas de dos decimales: conciliar antes de migrar'; END IF;
END $$;
ALTER TABLE ntacredito ALTER COLUMN valor TYPE numeric(14,2) USING valor::numeric,
 ALTER COLUMN devengado TYPE numeric(14,2) USING devengado::numeric;
CREATE TABLE nc_endoso (
 id bigserial PRIMARY KEY, nota_origen_id bigint NOT NULL REFERENCES ntacredito,
 cuenta_origen bigint NOT NULL REFERENCES abonados, monto_total numeric(14,2) NOT NULL CHECK(monto_total>0),
 estado varchar(20) NOT NULL DEFAULT 'BORRADOR' CHECK(estado IN ('BORRADOR','PENDIENTE','AUTORIZADO','EJECUTADO','RECHAZADO','ANULADO','REVERTIDO')),
 documento_respaldo varchar(120) NOT NULL, referencia varchar(80) NOT NULL, motivo text NOT NULL,
 solicitante_id smallint NOT NULL REFERENCES usuarios, autorizador_id smallint REFERENCES usuarios,
 ejecutor_id smallint REFERENCES usuarios, reversion_autorizador_id smallint REFERENCES usuarios,
 fecha_solicitud timestamptz, fecha_autorizacion timestamptz, fecha_ejecucion timestamptz, fecha_reversion timestamptz,
 motivo_autorizacion text, motivo_reversion text, huella varchar(64),
 saldo_antes numeric(14,2), saldo_despues numeric(14,2),
 version bigint NOT NULL DEFAULT 0, idempotency_key uuid NOT NULL UNIQUE, operacion_key uuid UNIQUE,
 creado_en timestamptz NOT NULL DEFAULT now(), actualizado_en timestamptz NOT NULL DEFAULT now(),
 CHECK(autorizador_id IS NULL OR autorizador_id<>solicitante_id));
CREATE INDEX idx_nc_endoso_origen_estado ON nc_endoso(nota_origen_id,estado);
CREATE TABLE nc_endoso_detalle (
 id bigserial PRIMARY KEY, endoso_id bigint NOT NULL REFERENCES nc_endoso,
 cuenta_destino bigint NOT NULL REFERENCES abonados, monto numeric(14,2) NOT NULL CHECK(monto>0),
 nota_derivada_id bigint UNIQUE REFERENCES ntacredito, UNIQUE(endoso_id,cuenta_destino));
CREATE TABLE nc_endoso_respaldo (
 endoso_id bigint PRIMARY KEY REFERENCES nc_endoso, nombre varchar(120) NOT NULL,
 contenido bytea NOT NULL CHECK(octet_length(contenido)<=5242880), sha256 varchar(64) NOT NULL,
 actor_id smallint NOT NULL REFERENCES usuarios, fecha timestamptz NOT NULL DEFAULT now());
ALTER TABLE ntacredito ADD COLUMN tipo_origen varchar(20) NOT NULL DEFAULT 'EMISION' CHECK(tipo_origen IN ('EMISION','ENDOSO')),
 ADD COLUMN nota_padre_id bigint REFERENCES ntacredito,
 ADD COLUMN endoso_detalle_id bigint UNIQUE REFERENCES nc_endoso_detalle;
ALTER TABLE ntacredito ADD CONSTRAINT nc_origen_endoso CHECK
 ((tipo_origen='ENDOSO' AND nota_padre_id IS NOT NULL AND endoso_detalle_id IS NOT NULL AND nrofactura IS NULL)
 OR (tipo_origen='EMISION' AND nota_padre_id IS NULL AND endoso_detalle_id IS NULL));
CREATE INDEX idx_nc_nota_padre ON ntacredito(nota_padre_id);
CREATE TABLE nc_endoso_auditoria (
 id bigserial PRIMARY KEY, endoso_id bigint NOT NULL REFERENCES nc_endoso, accion varchar(40) NOT NULL,
 estado_anterior varchar(20), estado_nuevo varchar(20), actor_id smallint NOT NULL REFERENCES usuarios,
 motivo text NOT NULL, detalle jsonb NOT NULL DEFAULT '{}'::jsonb, fecha timestamptz NOT NULL DEFAULT now());
CREATE INDEX idx_nc_endoso_auditoria ON nc_endoso_auditoria(endoso_id,fecha);
CREATE TABLE nc_endoso_permiso (
 usuario_id smallint NOT NULL REFERENCES usuarios, permiso varchar(40) NOT NULL
 CHECK(permiso IN ('CONSULTAR','SOLICITAR','AUTORIZAR','EJECUTAR','REVERTIR','REPORTES')),
 PRIMARY KEY(usuario_id,permiso));
-- No se otorgan permisos automaticamente, tampoco al administrador.
CREATE VIEW nc_saldos AS
 SELECT n.*, coalesce(x.endosado,0)::numeric(14,2) AS endosado,
 CASE WHEN n.estado=1 AND n.fechaeliminacion IS NULL THEN
 n.valor-coalesce(n.devengado,0)-coalesce(x.endosado,0) ELSE 0 END::numeric(14,2) AS disponible
 FROM ntacredito n LEFT JOIN (
 SELECT nota_origen_id,sum(monto_total) endosado FROM nc_endoso WHERE estado='EJECUTADO' GROUP BY nota_origen_id
 ) x ON x.nota_origen_id=n.idntacredito;
-- Defensa comun ante actualizaciones de saldo, incluidas rutas antiguas.
CREATE FUNCTION nc_validar_saldo() RETURNS trigger LANGUAGE plpgsql AS $$
DECLARE usado numeric; BEGIN
 SELECT coalesce(sum(monto_total),0) INTO usado FROM nc_endoso WHERE nota_origen_id=NEW.idntacredito AND estado='EJECUTADO';
 IF coalesce(NEW.devengado,0)<0 OR NEW.valor-coalesce(NEW.devengado,0)-usado<0 THEN
 RAISE EXCEPTION 'Saldo de nota de credito insuficiente'; END IF;
 IF OLD.tipo_origen='ENDOSO' AND (NEW.valor<>OLD.valor OR NEW.idabonado_abonados IS DISTINCT FROM OLD.idabonado_abonados
 OR NEW.tipo_origen<>OLD.tipo_origen OR NEW.nota_padre_id IS DISTINCT FROM OLD.nota_padre_id OR NEW.endoso_detalle_id IS DISTINCT FROM OLD.endoso_detalle_id) THEN
 RAISE EXCEPTION 'La procedencia de un credito derivado es inmutable'; END IF;
 RETURN NEW; END $$;
CREATE TRIGGER nc_validar_saldo BEFORE UPDATE ON ntacredito FOR EACH ROW EXECUTE FUNCTION nc_validar_saldo();
CREATE FUNCTION nc_auditoria_inmutable() RETURNS trigger LANGUAGE plpgsql AS $$ BEGIN
 RAISE EXCEPTION 'La auditoria de endosos es inmutable'; END $$;
CREATE TRIGGER nc_auditoria_inmutable BEFORE UPDATE OR DELETE ON nc_endoso_auditoria FOR EACH ROW EXECUTE FUNCTION nc_auditoria_inmutable();
-- Igualdad de importes verificada al COMMIT para permitir insertar cabecera y detalles en la misma transaccion.
CREATE FUNCTION nc_endoso_total_valido() RETURNS trigger LANGUAGE plpgsql AS $$
DECLARE eid bigint; cab numeric; suma numeric; BEGIN
 IF TG_TABLE_NAME='nc_endoso' THEN eid=NEW.id;
 ELSIF TG_OP='DELETE' THEN eid=OLD.endoso_id; ELSE eid=NEW.endoso_id; END IF;
 SELECT monto_total INTO cab FROM nc_endoso WHERE id=eid;
 SELECT coalesce(sum(monto),0) INTO suma FROM nc_endoso_detalle WHERE endoso_id=eid;
 IF cab IS NOT NULL AND cab<>suma THEN RAISE EXCEPTION 'La distribucion no coincide con total del endoso'; END IF;
 RETURN NULL; END $$;
CREATE CONSTRAINT TRIGGER nc_endoso_total_cab AFTER INSERT OR UPDATE ON nc_endoso DEFERRABLE INITIALLY DEFERRED FOR EACH ROW EXECUTE FUNCTION nc_endoso_total_valido();
CREATE CONSTRAINT TRIGGER nc_endoso_total_det AFTER INSERT OR UPDATE OR DELETE ON nc_endoso_detalle DEFERRABLE INITIALLY DEFERRED FOR EACH ROW EXECUTE FUNCTION nc_endoso_total_valido();
CREATE FUNCTION nc_endoso_no_borrar() RETURNS trigger LANGUAGE plpgsql AS $$ BEGIN
 RAISE EXCEPTION 'No borrar endosos; anular o revertir con auditoria'; END $$;
CREATE TRIGGER nc_endoso_no_borrar BEFORE DELETE ON nc_endoso FOR EACH ROW EXECUTE FUNCTION nc_endoso_no_borrar();
COMMIT;

