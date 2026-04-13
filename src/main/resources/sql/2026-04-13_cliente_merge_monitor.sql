CREATE TABLE IF NOT EXISTS cliente_merge (
    id_merge bigserial PRIMARY KEY,
    master_id bigint NOT NULL,
    fecha_merge timestamp NOT NULL DEFAULT now(),
    usuario_merge bigint,
    observacion text
);

CREATE TABLE IF NOT EXISTS cliente_merge_clientes (
    id bigserial PRIMARY KEY,
    id_merge bigint NOT NULL,
    cliente_dup_id bigint NOT NULL,
    fecha_merge timestamp NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS cliente_merge_abonados (
    id bigserial PRIMARY KEY,
    id_merge bigint NOT NULL,
    abonado_id bigint NOT NULL,
    cliente_origen bigint NOT NULL,
    fecha_merge timestamp NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS cliente_merge_facturas (
    id bigserial PRIMARY KEY,
    id_merge bigint NOT NULL,
    factura_id bigint NOT NULL,
    cliente_origen bigint NOT NULL,
    fecha_merge timestamp NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS cliente_merge_lecturas (
    id_merge_lectura bigserial PRIMARY KEY,
    id_merge bigint NOT NULL,
    id_lectura bigint NOT NULL,
    id_cliente_origen bigint NOT NULL,
    fecha_registro timestamp DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_cliente_merge_fecha ON cliente_merge (fecha_merge DESC);
CREATE INDEX IF NOT EXISTS idx_cliente_merge_master ON cliente_merge (master_id);
CREATE INDEX IF NOT EXISTS idx_cliente_merge_usuario ON cliente_merge (usuario_merge);
CREATE INDEX IF NOT EXISTS idx_cliente_merge_clientes_merge ON cliente_merge_clientes (id_merge);
CREATE INDEX IF NOT EXISTS idx_cliente_merge_abonados_merge ON cliente_merge_abonados (id_merge);
CREATE INDEX IF NOT EXISTS idx_cliente_merge_facturas_merge ON cliente_merge_facturas (id_merge);
CREATE INDEX IF NOT EXISTS idx_cliente_merge_lecturas_merge ON cliente_merge_lecturas (id_merge);
