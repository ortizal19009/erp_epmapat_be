-- Schema minimo para el flujo SRI de retenciones
-- Ejecutar una sola vez en la base de datos correspondiente

CREATE TABLE IF NOT EXISTS public.fec_retenciones (
    idretencion bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
    claveacceso varchar NULL,
    secuencial varchar NULL,
    xmlautorizado text NULL,
    errores text NULL,
    estado varchar NULL,
    establecimiento varchar NULL,
    puntoemision varchar NULL,
    direccionestablecimiento varchar NULL,
    fechaemision date NULL,
    tipoidentificacionsujetoretenid varchar NULL,
    razonsocialsujetoretenido varchar NULL,
    identificacionsujetoretenido varchar NULL,
    periodofiscal varchar NULL,
    telefonosujetoretenido varchar NULL,
    emailsujetoretenido varchar NULL,
    CONSTRAINT fec_retenciones_pk PRIMARY KEY (idretencion)
);

CREATE TABLE IF NOT EXISTS public.fec_retenciones_impuestos (
    idretencionesimpuestos bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
    idretencion bigint NOT NULL,
    codigo varchar NULL,
    codigoporcentaje varchar NULL,
    baseimponible numeric(18,2) NULL,
    codigodocumentosustento varchar NULL,
    numerodocumentosustento varchar NULL,
    fechaemisiondocumentosustento date NULL,
    CONSTRAINT fec_retenciones_impuestos_pk PRIMARY KEY (idretencionesimpuestos)
);

ALTER TABLE public.fec_retenciones_impuestos
    ADD CONSTRAINT fec_retenciones_impuestos_retencion_fk
    FOREIGN KEY (idretencion) REFERENCES public.retenciones(idrete) ON DELETE CASCADE;

CREATE TABLE IF NOT EXISTS public.correos_enviados (
    idcorreosenviado bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
    modulo varchar NULL,
    documento varchar NULL,
    documentoid bigint NULL,
    destinatarios text NULL,
    asunto varchar NULL,
    remitente varchar NULL,
    archivoadjunto varchar NULL,
    estado varchar NULL,
    detalle text NULL,
    fechaenvio timestamp NULL,
    CONSTRAINT correos_enviados_pk PRIMARY KEY (idcorreosenviado)
);
