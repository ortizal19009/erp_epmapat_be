CREATE TABLE IF NOT EXISTS erpmodulosxventanas (
   iderpmoduloxventana SERIAL PRIMARY KEY,
   iderpmodulo INTEGER NOT NULL REFERENCES erpmodulos(iderpmodulo),
   nombreventana VARCHAR(100) NOT NULL UNIQUE
);

CREATE INDEX IF NOT EXISTS idx_erpmodulosxventanas_modulo
   ON erpmodulosxventanas(iderpmodulo);
