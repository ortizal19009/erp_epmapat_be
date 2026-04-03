ALTER TABLE lecturas ADD COLUMN IF NOT EXISTS fechalectura timestamp;
ALTER TABLE lecturas ADD COLUMN IF NOT EXISTS usuariolectura bigint;
ALTER TABLE lecturas ADD COLUMN IF NOT EXISTS usumodi bigint;
ALTER TABLE lecturas ADD COLUMN IF NOT EXISTS fecmodi timestamp;
