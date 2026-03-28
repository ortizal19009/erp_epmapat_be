-- =========================================================
-- EPMAPAT ERP - Gestion Documental
-- Esquema base requerido por el backend Java actual
-- Fecha: 2026-03-27
-- Motor objetivo: PostgreSQL
-- =========================================================

CREATE EXTENSION IF NOT EXISTS pgcrypto;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'doc_flujo') THEN
        CREATE TYPE public.doc_flujo AS ENUM ('INGRESO', 'SALIDA');
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'doc_origen') THEN
        CREATE TYPE public.doc_origen AS ENUM ('INTERNO', 'EXTERNO');
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'doc_estado') THEN
        CREATE TYPE public.doc_estado AS ENUM ('BORRADOR', 'EMITIDO', 'RECIBIDO', 'DERIVADO', 'EN_REVISION', 'ARCHIVADO', 'ANULADO');
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'doc_prioridad') THEN
        CREATE TYPE public.doc_prioridad AS ENUM ('BAJA', 'MEDIA', 'ALTA', 'URGENTE');
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'confidencialidad') THEN
        CREATE TYPE public.confidencialidad AS ENUM ('PUBLICA', 'INTERNA', 'RESERVADA', 'CONFIDENCIAL');
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'estado_respuesta') THEN
        CREATE TYPE public.estado_respuesta AS ENUM ('NO_REQUIERE', 'PENDIENTE', 'RESPONDIDO');
    END IF;
END $$;

CREATE TABLE IF NOT EXISTS public.entidades (
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    codigo varchar(50) NOT NULL,
    nombre varchar(200) NOT NULL,
    activo boolean NOT NULL DEFAULT true,
    creado_en timestamptz NOT NULL DEFAULT now(),
    actualizado_en timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT entidades_pk PRIMARY KEY (id),
    CONSTRAINT entidades_codigo_uk UNIQUE (codigo)
);

CREATE TABLE IF NOT EXISTS public.dependencias (
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    entidad_id uuid NOT NULL,
    codigo varchar(50) NOT NULL,
    nombre varchar(200) NOT NULL,
    padre_id uuid NULL,
    activo boolean NOT NULL DEFAULT true,
    creado_en timestamptz NOT NULL DEFAULT now(),
    actualizado_en timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT dependencias_pk PRIMARY KEY (id),
    CONSTRAINT dependencias_entidad_codigo_uk UNIQUE (entidad_id, codigo),
    CONSTRAINT dependencias_entidad_fk FOREIGN KEY (entidad_id) REFERENCES public.entidades (id),
    CONSTRAINT dependencias_padre_fk FOREIGN KEY (padre_id) REFERENCES public.dependencias (id)
);

CREATE TABLE IF NOT EXISTS public.tipos_documento (
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    entidad_id uuid NOT NULL,
    codigo varchar(50) NOT NULL,
    nombre varchar(200) NOT NULL,
    activo boolean NOT NULL DEFAULT true,
    flujo varchar(30) NULL,
    CONSTRAINT tipos_documento_pk PRIMARY KEY (id),
    CONSTRAINT tipos_documento_entidad_codigo_uk UNIQUE (entidad_id, codigo),
    CONSTRAINT tipos_documento_entidad_fk FOREIGN KEY (entidad_id) REFERENCES public.entidades (id)
);

CREATE TABLE IF NOT EXISTS public.system_settings (
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    entity_code varchar(50) NOT NULL,
    setting_key varchar(100) NOT NULL,
    setting_value text NULL,
    updated_by varchar(100) NULL,
    updated_at timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT system_settings_pk PRIMARY KEY (id),
    CONSTRAINT system_settings_entity_key_uk UNIQUE (entity_code, setting_key)
);

CREATE TABLE IF NOT EXISTS public.document_series (
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    entity_code varchar(50) NOT NULL,
    code varchar(50) NOT NULL,
    name varchar(200) NOT NULL,
    active boolean NOT NULL DEFAULT true,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT document_series_pk PRIMARY KEY (id),
    CONSTRAINT document_series_entity_code_uk UNIQUE (entity_code, code)
);

CREATE TABLE IF NOT EXISTS public.document_subseries (
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    series_id uuid NOT NULL,
    code varchar(50) NOT NULL,
    name varchar(200) NOT NULL,
    active boolean NOT NULL DEFAULT true,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT document_subseries_pk PRIMARY KEY (id),
    CONSTRAINT document_subseries_series_code_uk UNIQUE (series_id, code),
    CONSTRAINT document_subseries_series_fk FOREIGN KEY (series_id) REFERENCES public.document_series (id)
);

CREATE TABLE IF NOT EXISTS public.retention_schedule (
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    entity_code varchar(50) NOT NULL,
    series_id uuid NOT NULL,
    subseries_id uuid NULL,
    active_years integer NOT NULL DEFAULT 1,
    semi_active_years integer NOT NULL DEFAULT 1,
    final_disposition varchar(50) NOT NULL DEFAULT 'CONSERVAR',
    legal_basis text NULL,
    active boolean NOT NULL DEFAULT true,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT retention_schedule_pk PRIMARY KEY (id),
    CONSTRAINT retention_schedule_series_fk FOREIGN KEY (series_id) REFERENCES public.document_series (id),
    CONSTRAINT retention_schedule_subseries_fk FOREIGN KEY (subseries_id) REFERENCES public.document_subseries (id),
    CONSTRAINT retention_schedule_entity_series_subseries_uk UNIQUE (entity_code, series_id, subseries_id)
);

CREATE TABLE IF NOT EXISTS public.electronic_case_file (
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    entity_code varchar(50) NOT NULL,
    code varchar(50) NOT NULL,
    title varchar(250) NOT NULL,
    owner_dependency_id uuid NULL,
    status varchar(30) NOT NULL DEFAULT 'ABIERTO',
    opened_at timestamptz NOT NULL DEFAULT now(),
    closed_at timestamptz NULL,
    closed_by uuid NULL,
    created_by uuid NULL,
    created_at timestamptz NOT NULL DEFAULT now(),
    closure_index_hash varchar(256) NULL,
    closure_sealed_at timestamptz NULL,
    CONSTRAINT electronic_case_file_pk PRIMARY KEY (id),
    CONSTRAINT electronic_case_file_entity_code_uk UNIQUE (entity_code, code),
    CONSTRAINT electronic_case_file_dependency_fk FOREIGN KEY (owner_dependency_id) REFERENCES public.dependencias (id)
);

CREATE TABLE IF NOT EXISTS public.series_numeracion (
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    entidad_id uuid NOT NULL,
    tipo_doc_id uuid NOT NULL,
    dependencia_id uuid NULL,
    anio integer NOT NULL,
    prefijo varchar(30) NOT NULL,
    longitud_seq integer NOT NULL DEFAULT 6,
    siguiente_seq integer NOT NULL DEFAULT 1,
    activo boolean NOT NULL DEFAULT true,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT series_numeracion_pk PRIMARY KEY (id),
    CONSTRAINT series_numeracion_entidad_fk FOREIGN KEY (entidad_id) REFERENCES public.entidades (id),
    CONSTRAINT series_numeracion_tipo_fk FOREIGN KEY (tipo_doc_id) REFERENCES public.tipos_documento (id),
    CONSTRAINT series_numeracion_dependencia_fk FOREIGN KEY (dependencia_id) REFERENCES public.dependencias (id)
);

CREATE TABLE IF NOT EXISTS public.documentos (
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    entidad_id uuid NOT NULL,
    flujo public.doc_flujo NOT NULL,
    origen public.doc_origen NOT NULL,
    estado public.doc_estado NOT NULL DEFAULT 'BORRADOR',
    prioridad public.doc_prioridad NOT NULL DEFAULT 'MEDIA',
    confidencialidad public.confidencialidad NOT NULL DEFAULT 'INTERNA',
    requiere_respuesta boolean NOT NULL DEFAULT false,
    fecha_plazo timestamptz NULL,
    estado_respuesta public.estado_respuesta NOT NULL DEFAULT 'NO_REQUIERE',
    tipo_doc_id uuid NOT NULL,
    dependencia_emisora_id uuid NULL,
    series_id uuid NULL,
    subseries_id uuid NULL,
    retention_schedule_id uuid NULL,
    serie_id uuid NULL,
    case_file_id uuid NULL,
    numero_oficial varchar(120) NULL,
    fecha_elaboracion date NOT NULL DEFAULT CURRENT_DATE,
    fecha_emision timestamptz NULL,
    fecha_recepcion timestamptz NULL,
    remitente_persona_id uuid NULL,
    remitente_externo varchar(250) NULL,
    asunto varchar(500) NOT NULL,
    cuerpo text NULL,
    referencia varchar(250) NULL,
    observaciones text NULL,
    owner_user_id uuid NULL,
    creado_por uuid NULL,
    actualizado_por uuid NULL,
    creado_en timestamptz NOT NULL DEFAULT now(),
    actualizado_en timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT documentos_pk PRIMARY KEY (id),
    CONSTRAINT documentos_entidad_fk FOREIGN KEY (entidad_id) REFERENCES public.entidades (id),
    CONSTRAINT documentos_tipo_fk FOREIGN KEY (tipo_doc_id) REFERENCES public.tipos_documento (id),
    CONSTRAINT documentos_dependencia_fk FOREIGN KEY (dependencia_emisora_id) REFERENCES public.dependencias (id),
    CONSTRAINT documentos_series_fk FOREIGN KEY (series_id) REFERENCES public.document_series (id),
    CONSTRAINT documentos_subseries_fk FOREIGN KEY (subseries_id) REFERENCES public.document_subseries (id),
    CONSTRAINT documentos_retention_fk FOREIGN KEY (retention_schedule_id) REFERENCES public.retention_schedule (id),
    CONSTRAINT documentos_case_file_fk FOREIGN KEY (case_file_id) REFERENCES public.electronic_case_file (id),
    CONSTRAINT documentos_serie_numeracion_fk FOREIGN KEY (serie_id) REFERENCES public.series_numeracion (id)
);

CREATE TABLE IF NOT EXISTS public.case_file_items (
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    case_file_id uuid NOT NULL,
    document_id uuid NOT NULL,
    order_index integer NOT NULL,
    folio varchar(30) NOT NULL,
    incorporated_at timestamptz NOT NULL DEFAULT now(),
    incorporated_by uuid NULL,
    CONSTRAINT case_file_items_pk PRIMARY KEY (id),
    CONSTRAINT case_file_items_case_file_fk FOREIGN KEY (case_file_id) REFERENCES public.electronic_case_file (id) ON DELETE CASCADE,
    CONSTRAINT case_file_items_document_fk FOREIGN KEY (document_id) REFERENCES public.documentos (id) ON DELETE CASCADE,
    CONSTRAINT case_file_items_case_file_document_uk UNIQUE (case_file_id, document_id)
);

CREATE TABLE IF NOT EXISTS public.documento_destinatarios (
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    documento_id uuid NOT NULL,
    to_user_id uuid NULL,
    to_dependency_id uuid NULL,
    persona_id uuid GENERATED ALWAYS AS (to_user_id) STORED,
    dependencia_id uuid GENERATED ALWAYS AS (to_dependency_id) STORED,
    externo_nombre varchar(250) NULL,
    estado varchar(30) NOT NULL DEFAULT 'PENDIENTE',
    creado_en timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT documento_destinatarios_pk PRIMARY KEY (id),
    CONSTRAINT documento_destinatarios_documento_fk FOREIGN KEY (documento_id) REFERENCES public.documentos (id) ON DELETE CASCADE,
    CONSTRAINT documento_destinatarios_target_chk CHECK (
        (to_user_id IS NOT NULL)::int +
        (to_dependency_id IS NOT NULL)::int +
        (externo_nombre IS NOT NULL)::int = 1
    )
);

CREATE TABLE IF NOT EXISTS public.documento_recepciones (
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    documento_id uuid NOT NULL,
    receptor_id uuid NULL,
    dependencia_id uuid NULL,
    recibido_en timestamptz NULL,
    confirmado_por uuid NULL,
    estado varchar(30) NOT NULL DEFAULT 'PENDIENTE',
    comentario text NULL,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT documento_recepciones_pk PRIMARY KEY (id),
    CONSTRAINT documento_recepciones_documento_fk FOREIGN KEY (documento_id) REFERENCES public.documentos (id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX IF NOT EXISTS documento_recepciones_doc_receptor_uk
    ON public.documento_recepciones (documento_id, receptor_id)
    WHERE receptor_id IS NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS documento_recepciones_doc_dependencia_uk
    ON public.documento_recepciones (documento_id, dependencia_id)
    WHERE receptor_id IS NULL AND dependencia_id IS NOT NULL;

CREATE TABLE IF NOT EXISTS public.documento_eventos (
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    documento_id uuid NOT NULL,
    actor_user_id uuid NULL,
    actor_rol varchar(60) NULL,
    evento varchar(80) NULL,
    detalle jsonb NULL,
    created_at timestamptz NULL,
    event_type varchar(80) NULL,
    description text NULL,
    occurred_at timestamptz NULL,
    CONSTRAINT documento_eventos_pk PRIMARY KEY (id),
    CONSTRAINT documento_eventos_documento_fk FOREIGN KEY (documento_id) REFERENCES public.documentos (id) ON DELETE CASCADE
);

CREATE OR REPLACE FUNCTION public.trg_documento_eventos_sync()
RETURNS trigger
LANGUAGE plpgsql
AS $$
BEGIN
    IF NEW.evento IS NULL THEN
        NEW.evento := NEW.event_type;
    END IF;

    IF NEW.event_type IS NULL THEN
        NEW.event_type := NEW.evento;
    END IF;

    IF NEW.created_at IS NULL THEN
        NEW.created_at := COALESCE(NEW.occurred_at, now());
    END IF;

    IF NEW.occurred_at IS NULL THEN
        NEW.occurred_at := COALESCE(NEW.created_at, now());
    END IF;

    IF NEW.detalle IS NULL AND NEW.description IS NOT NULL THEN
        NEW.detalle := jsonb_build_object('detail', NEW.description);
    END IF;

    IF NEW.description IS NULL AND NEW.detalle IS NOT NULL THEN
        NEW.description := COALESCE(NEW.detalle ->> 'detail', NEW.detalle::text);
    END IF;

    RETURN NEW;
END $$;

DROP TRIGGER IF EXISTS documento_eventos_sync_biu ON public.documento_eventos;
CREATE TRIGGER documento_eventos_sync_biu
BEFORE INSERT OR UPDATE ON public.documento_eventos
FOR EACH ROW
EXECUTE FUNCTION public.trg_documento_eventos_sync();

CREATE TABLE IF NOT EXISTS public.documento_asignaciones (
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    documento_id uuid NOT NULL,
    asignado_a_user_id uuid NOT NULL,
    asignado_por_user_id uuid NULL,
    dependencia_id uuid NULL,
    rol_responsable varchar(50) NULL,
    principal boolean NOT NULL DEFAULT true,
    estado varchar(30) NOT NULL DEFAULT 'ACTIVA',
    observacion text NULL,
    creado_en timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT documento_asignaciones_pk PRIMARY KEY (id),
    CONSTRAINT documento_asignaciones_documento_fk FOREIGN KEY (documento_id) REFERENCES public.documentos (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS public.documento_derivaciones (
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    documento_id uuid NOT NULL,
    de_user_id uuid NULL,
    de_dependencia_id uuid NULL,
    para_user_id uuid NULL,
    para_dependencia_id uuid NULL,
    sumilla text NULL,
    requiere_respuesta boolean NOT NULL DEFAULT false,
    fecha_plazo timestamptz NULL,
    estado varchar(30) NOT NULL DEFAULT 'PENDIENTE',
    leido_en timestamptz NULL,
    respondido_en timestamptz NULL,
    cerrado_en timestamptz NULL,
    creado_en timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT documento_derivaciones_pk PRIMARY KEY (id),
    CONSTRAINT documento_derivaciones_documento_fk FOREIGN KEY (documento_id) REFERENCES public.documentos (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS public.documento_respuestas (
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    documento_id uuid NOT NULL,
    derivacion_id uuid NULL,
    respondido_por_user_id uuid NULL,
    asunto varchar(500) NOT NULL,
    cuerpo text NULL,
    creado_en timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT documento_respuestas_pk PRIMARY KEY (id),
    CONSTRAINT documento_respuestas_documento_fk FOREIGN KEY (documento_id) REFERENCES public.documentos (id) ON DELETE CASCADE,
    CONSTRAINT documento_respuestas_derivacion_fk FOREIGN KEY (derivacion_id) REFERENCES public.documento_derivaciones (id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS public.documento_relaciones (
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    documento_padre_id uuid NOT NULL,
    documento_hijo_id uuid NOT NULL,
    tipo_relacion varchar(50) NOT NULL,
    creado_por_user_id uuid NULL,
    detalle jsonb NULL,
    creado_en timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT documento_relaciones_pk PRIMARY KEY (id),
    CONSTRAINT documento_relaciones_padre_fk FOREIGN KEY (documento_padre_id) REFERENCES public.documentos (id) ON DELETE CASCADE,
    CONSTRAINT documento_relaciones_hijo_fk FOREIGN KEY (documento_hijo_id) REFERENCES public.documentos (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS public.documento_archivos (
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    documento_id uuid NOT NULL,
    version integer NOT NULL,
    tipo varchar(40) NOT NULL DEFAULT 'ANEXO',
    nombre_original varchar(255) NOT NULL,
    nombre_storage varchar(255) NOT NULL,
    extension varchar(30) NULL,
    mime_type varchar(150) NOT NULL,
    size_bytes bigint NOT NULL DEFAULT 0,
    sha256 varchar(64) NULL,
    storage_path text NOT NULL,
    subido_por_user_id uuid NULL,
    subido_en timestamptz NOT NULL DEFAULT now(),
    activo boolean NOT NULL DEFAULT true,
    CONSTRAINT documento_archivos_pk PRIMARY KEY (id),
    CONSTRAINT documento_archivos_documento_fk FOREIGN KEY (documento_id) REFERENCES public.documentos (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS public.documento_alertas (
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    documento_id uuid NOT NULL,
    user_id uuid NULL,
    tipo varchar(30) NOT NULL,
    scheduled_at timestamptz NOT NULL,
    sent_at timestamptz NULL,
    estado varchar(30) NOT NULL DEFAULT 'PENDIENTE',
    payload jsonb NULL,
    created_at timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT documento_alertas_pk PRIMARY KEY (id),
    CONSTRAINT documento_alertas_documento_fk FOREIGN KEY (documento_id) REFERENCES public.documentos (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS entidades_codigo_idx ON public.entidades (codigo);
CREATE INDEX IF NOT EXISTS dependencias_entidad_idx ON public.dependencias (entidad_id);
CREATE INDEX IF NOT EXISTS tipos_documento_entidad_idx ON public.tipos_documento (entidad_id);
CREATE INDEX IF NOT EXISTS documentos_entidad_idx ON public.documentos (entidad_id);
CREATE INDEX IF NOT EXISTS documentos_estado_idx ON public.documentos (estado);
CREATE INDEX IF NOT EXISTS documentos_flujo_idx ON public.documentos (flujo);
CREATE INDEX IF NOT EXISTS documentos_tipo_idx ON public.documentos (tipo_doc_id);
CREATE INDEX IF NOT EXISTS documentos_dependencia_idx ON public.documentos (dependencia_emisora_id);
CREATE INDEX IF NOT EXISTS documentos_due_idx ON public.documentos (fecha_plazo);
CREATE INDEX IF NOT EXISTS documento_destinatarios_documento_idx ON public.documento_destinatarios (documento_id);
CREATE INDEX IF NOT EXISTS documento_derivaciones_documento_idx ON public.documento_derivaciones (documento_id);
CREATE INDEX IF NOT EXISTS documento_derivaciones_para_user_idx ON public.documento_derivaciones (para_user_id);
CREATE INDEX IF NOT EXISTS documento_derivaciones_para_dep_idx ON public.documento_derivaciones (para_dependencia_id);
CREATE INDEX IF NOT EXISTS documento_recepciones_documento_idx ON public.documento_recepciones (documento_id);
CREATE INDEX IF NOT EXISTS documento_eventos_documento_idx ON public.documento_eventos (documento_id, created_at);
CREATE INDEX IF NOT EXISTS documento_archivos_documento_idx ON public.documento_archivos (documento_id, activo);
CREATE INDEX IF NOT EXISTS documento_alertas_documento_idx ON public.documento_alertas (documento_id);
CREATE INDEX IF NOT EXISTS documento_alertas_estado_idx ON public.documento_alertas (estado, scheduled_at);
CREATE INDEX IF NOT EXISTS series_numeracion_lookup_idx
    ON public.series_numeracion (entidad_id, tipo_doc_id, anio, dependencia_id, activo);
