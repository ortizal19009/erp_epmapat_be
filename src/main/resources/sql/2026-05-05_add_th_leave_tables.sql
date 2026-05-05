CREATE TABLE IF NOT EXISTS th_leave_balances (
    idbalance BIGSERIAL PRIMARY KEY,
    idpersonal_personal BIGINT NOT NULL,
    anio INTEGER NOT NULL,
    dias_asignados NUMERIC(10,2),
    dias_usados NUMERIC(10,2),
    dias_disponibles NUMERIC(10,2),
    feccrea DATE,
    usucrea BIGINT,
    fecmodi DATE,
    usumodi BIGINT,
    estado BOOLEAN,
    CONSTRAINT fk_th_leave_balances_personal
        FOREIGN KEY (idpersonal_personal) REFERENCES personal(idpersonal),
    CONSTRAINT uq_th_leave_balances_personal_anio
        UNIQUE (idpersonal_personal, anio)
);

CREATE INDEX IF NOT EXISTS idx_th_leave_balances_personal
    ON th_leave_balances(idpersonal_personal);

CREATE INDEX IF NOT EXISTS idx_th_leave_balances_anio
    ON th_leave_balances(anio);

CREATE TABLE IF NOT EXISTS th_leave_requests (
    idrequest BIGSERIAL PRIMARY KEY,
    idpersonal_personal BIGINT NOT NULL,
    tipolicencia VARCHAR(100),
    fechainicio DATE,
    fechafin DATE,
    dias_solicitados NUMERIC(10,2),
    motivo TEXT,
    estado VARCHAR(30),
    aprobador_id BIGINT,
    fecha_aprobacion DATE,
    observacion_aprobacion TEXT,
    feccrea DATE,
    usucrea BIGINT,
    fecmodi DATE,
    usumodi BIGINT,
    activo BOOLEAN,
    CONSTRAINT fk_th_leave_requests_personal
        FOREIGN KEY (idpersonal_personal) REFERENCES personal(idpersonal),
    CONSTRAINT chk_th_leave_requests_fechas
        CHECK (fechafin IS NULL OR fechainicio IS NULL OR fechafin >= fechainicio)
);

CREATE INDEX IF NOT EXISTS idx_th_leave_requests_personal
    ON th_leave_requests(idpersonal_personal);

CREATE INDEX IF NOT EXISTS idx_th_leave_requests_estado
    ON th_leave_requests(estado);

CREATE INDEX IF NOT EXISTS idx_th_leave_requests_fechas
    ON th_leave_requests(fechainicio, fechafin);

CREATE TABLE IF NOT EXISTS th_actions (
    idaction BIGSERIAL PRIMARY KEY,
    idpersonal_personal BIGINT NOT NULL,
    tipoaccion VARCHAR(100),
    motivo TEXT,
    observacion TEXT,
    fecvigencia DATE,
    feccrea DATE,
    usucrea BIGINT,
    fecmodi DATE,
    usumodi BIGINT,
    estado BOOLEAN,
    CONSTRAINT fk_th_actions_personal
        FOREIGN KEY (idpersonal_personal) REFERENCES personal(idpersonal)
);

CREATE INDEX IF NOT EXISTS idx_th_actions_personal
    ON th_actions(idpersonal_personal);

CREATE INDEX IF NOT EXISTS idx_th_actions_feccrea
    ON th_actions(feccrea);

CREATE TABLE IF NOT EXISTS th_employee_files (
    idfile BIGSERIAL PRIMARY KEY,
    idpersonal_personal BIGINT NOT NULL,
    tipo_doc VARCHAR(100),
    nombre_archivo VARCHAR(255),
    ruta_archivo TEXT,
    hash_archivo VARCHAR(255),
    version_doc INTEGER,
    estado VARCHAR(30),
    feccrea DATE,
    usucrea BIGINT,
    fecmodi DATE,
    usumodi BIGINT,
    CONSTRAINT fk_th_employee_files_personal
        FOREIGN KEY (idpersonal_personal) REFERENCES personal(idpersonal)
);

CREATE INDEX IF NOT EXISTS idx_th_employee_files_personal
    ON th_employee_files(idpersonal_personal);

CREATE INDEX IF NOT EXISTS idx_th_employee_files_version
    ON th_employee_files(version_doc);

CREATE TABLE IF NOT EXISTS th_audit_log (
    idaudit BIGSERIAL PRIMARY KEY,
    entidad VARCHAR(120),
    idregistro BIGINT,
    accion VARCHAR(50),
    detalle TEXT,
    usuario BIGINT,
    fecha TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_th_audit_log_entidad_registro
    ON th_audit_log(entidad, idregistro);

CREATE INDEX IF NOT EXISTS idx_th_audit_log_fecha
    ON th_audit_log(fecha);
