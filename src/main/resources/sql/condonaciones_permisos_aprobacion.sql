-- Habilita el permiso de aprobacion/rechazo de exoneraciones I/M/R.
-- Regla aplicada por frontend y backend:
--   permissions >= 3 en la ventana 'condonaciones-pendientes'
--
-- Uso:
-- 1. Reemplazar los idusuario del bloque usuarios_autorizados.
-- 2. Ejecutar el script en la base ErpEpmapaT.
-- 3. Verificar el resultado con la consulta final.

BEGIN;

WITH usuarios_autorizados(idusuario) AS (
    VALUES
        (3),
        (4),
        (6)
)
UPDATE ventanas v
SET permissions = 3
WHERE v.nombre = 'condonaciones-pendientes'
  AND v.idusuario IN (SELECT idusuario FROM usuarios_autorizados);

INSERT INTO ventanas (nombre, color1, color2, idusuario, permissions)
SELECT
    'condonaciones-pendientes',
    COALESCE(base.color1, 'rgb(80, 4, 80)'),
    COALESCE(base.color2, 'rgb(250, 200, 250)'),
    ua.idusuario,
    3
FROM usuarios_autorizados ua
LEFT JOIN ventanas base
    ON base.idusuario = ua.idusuario
   AND base.nombre = 'condonaciones'
WHERE NOT EXISTS (
    SELECT 1
    FROM ventanas v
    WHERE v.idusuario = ua.idusuario
      AND v.nombre = 'condonaciones-pendientes'
);

COMMIT;

SELECT
    v.idusuario,
    u.nomusu,
    u.estado,
    v.nombre,
    v.permissions
FROM ventanas v
LEFT JOIN usuarios u ON u.idusuario = v.idusuario
WHERE v.nombre = 'condonaciones-pendientes'
ORDER BY v.idusuario;
