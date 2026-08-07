CREATE TABLE IF NOT EXISTS public.factura_reasignacion_historial (
    idreasignacion bigserial PRIMARY KEY,
    idfactura bigint NOT NULL,
    idusuarioaccion bigint NULL,
    idrecaudador bigint NULL,
    idcaja bigint NULL,
    idrecaudaxcaja bigint NULL,
    secuencialanterior varchar(30) NULL,
    secuencialnuevo varchar(30) NULL,
    nrofacturaanterior varchar(30) NULL,
    nrofacturanuevo varchar(30) NULL,
    claveaccesoanterior varchar(60) NULL,
    claveaccesonueva varchar(60) NULL,
    estadoanterior varchar(10) NULL,
    estadonuevo varchar(10) NULL,
    xmlanterior text NULL,
    xmlnuevo text NULL,
    observacion text NULL,
    fechareasignacion timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_factura_reasignacion_historial_idfactura
    ON public.factura_reasignacion_historial (idfactura, fechareasignacion DESC);

CREATE INDEX IF NOT EXISTS idx_factura_reasignacion_historial_recaudador
    ON public.factura_reasignacion_historial (idrecaudador, fechareasignacion DESC);
