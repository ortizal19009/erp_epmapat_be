-- Registra la ventana en Comercializacion sin conceder permisos a usuarios.
INSERT INTO erpmodulosxventanas (iderpmodulo, nombreventana)
SELECT iderpmodulo, 'trazabilidad'
FROM erpmodulos
WHERE iderpmodulo = 1
ON CONFLICT (nombreventana) DO NOTHING;
