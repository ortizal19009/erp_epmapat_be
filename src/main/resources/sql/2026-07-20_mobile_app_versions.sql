CREATE TABLE IF NOT EXISTS mobile_app_versions (
    id BIGSERIAL PRIMARY KEY,
    package_name VARCHAR(150) NOT NULL,
    version_name VARCHAR(60) NOT NULL,
    version_code BIGINT NOT NULL,
    archivo_nombre VARCHAR(255) NOT NULL,
    archivo_ruta VARCHAR(500) NOT NULL,
    content_type VARCHAR(120),
    size_bytes BIGINT,
    checksum_sha256 VARCHAR(128),
    descripcion VARCHAR(1000),
    entorno VARCHAR(80),
    host_name VARCHAR(150),
    force_update BOOLEAN NOT NULL DEFAULT FALSE,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    usucrea BIGINT,
    feccrea TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_mobile_app_versions_package_active
    ON mobile_app_versions(package_name, activo, version_code DESC, feccrea DESC);
