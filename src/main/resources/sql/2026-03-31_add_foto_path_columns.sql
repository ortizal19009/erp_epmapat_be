-- Agrega los campos para almacenar el path de las fotos en Abonados y Lecturas
ALTER TABLE abonados ADD COLUMN IF NOT EXISTS foto_path text;
ALTER TABLE lecturas ADD COLUMN IF NOT EXISTS foto_path text;
