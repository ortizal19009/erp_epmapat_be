-- Ejecutar fuera de una transaccion. CONCURRENTLY permite continuar operando.
-- Complementa la consulta de /facturas/sincobrar/cuenta sin cambiar sus importes.
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_facturas_pendientes_cuenta_fecha
    ON public.facturas (idabonado, feccrea, idfactura)
    WHERE fechaeliminacion IS NULL
      AND fechaconvenio IS NULL
      AND (((estado = 1 OR estado = 2) AND fechacobro IS NULL) OR estado = 3);

-- Incluye todos los rubros activos; el indice de refacturaciones excluye 5 y 165.
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_notificacion_rubros_factura_activos
    ON public.rubroxfac (idfactura_facturas)
    WHERE estado IS NULL OR estado <> 0;
