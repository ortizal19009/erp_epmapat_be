-- Catálogo Personal de la ficha individual; independiente de rrhh_employees.
BEGIN;
SET LOCAL lock_timeout = '5s';
SET LOCAL statement_timeout = '60s';
SET LOCAL search_path = public;
CREATE TABLE IF NOT EXISTS th_performance_reviews (
    idreview BIGSERIAL PRIMARY KEY,
    idpersonal BIGINT NOT NULL REFERENCES personal(idpersonal),
    periodo VARCHAR(40) NOT NULL CHECK (length(trim(periodo)) > 0),
    fecha DATE NOT NULL,
    puntaje NUMERIC(5,2) NOT NULL CHECK (puntaje BETWEEN 0 AND 100),
    evaluador VARCHAR(120) NOT NULL CHECK (length(trim(evaluador)) > 0),
    observaciones VARCHAR(2000) NOT NULL CHECK (length(trim(observaciones)) > 0),
    plan_mejora VARCHAR(2000),
    usuario BIGINT NOT NULL,
    creado TIMESTAMP NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_th_performance_personal ON th_performance_reviews(idpersonal, fecha DESC, idreview DESC);
COMMIT;
