-- Generado desde resources/reports. No contiene binarios.
-- Primero aplicar 2026-11-05_reportesjr_archivos_opciones.sql.
-- Crea una opcion independiente para revisar y asignar los reportes importados.
-- No asigna los reportes a las opciones contables existentes.
-- Copiar reports/catalogo-importado a REPORTESJR_DIRECTORY/catalogo-importado.
-- Los nombres existentes se conservan sin modificaciones.
BEGIN;
DO $catalogo$
DECLARE
  v_idrepoxopcion smallint;
  v_codigo text;
  v_usucrea smallint;
  v_tabla text;
  v_columna text;
  v_secuencia text;
  v_maximo bigint;
  v_ultimo bigint;
BEGIN
  -- Sincronizar IDs automaticos sin retroceder las secuencias.
  LOCK TABLE repoxopcion, reportesjr IN SHARE ROW EXCLUSIVE MODE;
  FOREACH v_tabla IN ARRAY ARRAY['repoxopcion', 'reportesjr'] LOOP
    v_columna := CASE WHEN v_tabla = 'repoxopcion' THEN 'idrepoxopcion' ELSE 'idreporte' END;
    v_secuencia := pg_get_serial_sequence(v_tabla, v_columna);
    IF v_secuencia IS NULL THEN
      RAISE EXCEPTION 'La columna %.% no tiene secuencia asociada', v_tabla, v_columna;
    END IF;
    EXECUTE format('SELECT COALESCE(MAX(%I), 0) FROM %I', v_columna, v_tabla) INTO v_maximo;
    EXECUTE format('SELECT last_value FROM %s', v_secuencia) INTO v_ultimo;
    PERFORM setval(v_secuencia::regclass, GREATEST(v_maximo, v_ultimo), true);
  END LOOP;
  SELECT min(idrepoxopcion) INTO v_idrepoxopcion FROM repoxopcion
  WHERE opcion = 'Reportes JR' AND nombre = 'Catalogo Jasper importado';
  IF v_idrepoxopcion IS NULL THEN
    SELECT usucrea INTO v_usucrea FROM repoxopcion WHERE usucrea IS NOT NULL ORDER BY idrepoxopcion LIMIT 1;
    IF v_usucrea IS NULL THEN
      RAISE EXCEPTION 'No hay usuario creador de referencia en repoxopcion';
    END IF;
    SELECT n::text INTO v_codigo FROM generate_series(99001, 99999) AS candidatos(n)
    WHERE NOT EXISTS (SELECT 1 FROM repoxopcion r WHERE r.codigo = n::text) ORDER BY n LIMIT 1;
    IF v_codigo IS NULL THEN
      RAISE EXCEPTION 'No hay codigo libre para el catalogo de importacion';
    END IF;
    INSERT INTO repoxopcion (codigo, opcion, nombre, usucrea, feccrea)
    VALUES (v_codigo, 'Reportes JR', 'Catalogo Jasper importado', v_usucrea, CURRENT_TIMESTAMP)
    RETURNING idrepoxopcion INTO v_idrepoxopcion;
  END IF;
  INSERT INTO reportesjr (nomrep, desrep, metodo, jrxml_path, jasper_path, parametros, idrepoxopcion, feccrea)
  SELECT c.nomrep, c.desrep, c.metodo, c.jrxml_path, c.jasper_path, c.parametros, v_idrepoxopcion, CURRENT_TIMESTAMP
  FROM (VALUES
  ('CompPagoConsumoAgua', 'CompPagoConsumoAgua', 1, 'catalogo-importado/CompPagoConsumoAgua.jrxml', 'catalogo-importado/CompPagoConsumoAgua.jasper', '{"idfactura":"java.lang.Long|12|999"}'::json),
  ('CompPagoConsumoAgua_con_subtotales', 'CompPagoConsumoAgua_con_subtotales', 1, 'catalogo-importado/CompPagoConsumoAgua_con_subtotales.jrxml', 'catalogo-importado/CompPagoConsumoAgua_con_subtotales.jasper', '{"idfactura":"java.lang.Long|12|999"}'::json),
  ('CompPagoConvenios', 'CompPagoConvenios', 1, 'catalogo-importado/CompPagoConvenios.jrxml', 'catalogo-importado/CompPagoConvenios.jasper', '{"idfactura":"java.lang.Long|12|999"}'::json),
  ('CompPagoServicios', 'CompPagoServicios', 1, 'catalogo-importado/CompPagoServicios.jrxml', 'catalogo-importado/CompPagoServicios.jasper', '{"idfactura":"java.lang.Long|12|999"}'::json),
  ('EmisionFinal', 'EmisionFinal', 1, 'catalogo-importado/EmisionFinal.jrxml', 'catalogo-importado/EmisionFinal.jasper', '{"idemision":"java.lang.Long|12|999","idusuario":"java.lang.Long|12|999"}'::json),
  ('EmisionInicial', 'EmisionInicial', 1, 'catalogo-importado/EmisionInicial.jrxml', 'catalogo-importado/EmisionInicial.jasper', '{"idusuario":"java.lang.Integer|12|999","idemision":"java.lang.Integer|12|999","ORACLE_REF_CURSOR":"interface java.sql.ResultSet|12|999"}'::json),
  ('EmisionInicialConEliminadas', 'EmisionInicialConEliminadas', 1, 'catalogo-importado/EmisionInicialConEliminadas.jrxml', 'catalogo-importado/EmisionInicialConEliminadas.jasper', '{"idusuario":"java.lang.Integer|12|999","idemision":"java.lang.Integer|12|999","ORACLE_REF_CURSOR":"interface java.sql.ResultSet|12|999"}'::json),
  ('factura_template', 'factura_template', 3, 'catalogo-importado/factura_template.jrxml', 'catalogo-importado/factura_template.jasper', '{"RazonSocial":"java.lang.String|12|999","Ruc":"java.lang.String|12|999","ClaveAcceso":"java.lang.String|12|999","NumeroAutorizacion":"java.lang.String|12|999","FechaAutorizacion":"java.lang.String|12|999","FechaEmision":"java.lang.String|12|999","TotalSinImpuestos":"java.lang.String|12|999","DireccionMatriz":"java.lang.String|12|999","DireccionEstablecimiento":"java.lang.String|12|999","NroFactura":"java.lang.String|12|999","Ambiente":"java.lang.String|12|999","TipoEmision":"java.lang.String|12|999","AgenteRetencion":"java.lang.String|12|999","ObligadoContabilidad":"java.lang.String|12|999","RazonSocialComprador":"java.lang.String|12|999","IdentificacionComprador":"java.lang.String|12|999","DireccionComprador":"java.lang.String|12|999","GuiaRemision":"java.lang.String|12|999","Email":"java.lang.String|12|999","Concepto":"java.lang.String|12|999","Recaudador":"java.lang.String|12|999","Referencia":"java.lang.String|12|999","Cuenta":"java.lang.String|12|999","FormaPago":"java.lang.String|12|999","Total":"java.lang.String|12|999","ImporteTotal":"java.math.BigDecimal|12|999","TotalDescuento":"java.math.BigDecimal|12|999","SubTotalIVA0":"java.math.BigDecimal|12|999","SubTotalIVA12":"java.math.BigDecimal|12|999","SubTotalIVA15":"java.math.BigDecimal|12|999","SubTotalNoObjetoIVA":"java.math.BigDecimal|12|999","SubTotalExentoIVA":"java.math.BigDecimal|12|999","TotalICE":"java.math.BigDecimal|12|999","TotalIRBPNR":"java.math.BigDecimal|12|999","TotalIVA12":"java.math.BigDecimal|12|999","TotalIVA15":"java.math.BigDecimal|12|999","Propina":"java.math.BigDecimal|12|999"}'::json),
  ('FacturaElectronica', 'FacturaElectronica', 3, 'catalogo-importado/FacturaElectronica.jrxml', 'catalogo-importado/FacturaElectronica.jasper', '{"RazonSocial":"java.lang.String|12|999","Ruc":"java.lang.String|12|999","NumeroAutorizacion":"java.lang.String|12|999","FechaAutorizacion":"java.lang.String|12|999","FechaEmision":"java.lang.String|12|999","TotalSinImpuestos":"java.lang.String|12|999","DireccionMatriz":"java.lang.String|12|999","DireccionEstablecimiento":"java.lang.String|12|999","NroFactura":"java.lang.String|12|999","Ambiente":"java.lang.String|12|999","AgenteRetencion":"java.lang.String|12|999","ObligadoContabilidad":"java.lang.String|12|999","RazonSocialComprador":"java.lang.String|12|999","IdentificacionComprador":"java.lang.String|12|999","DireccionComprador":"java.lang.String|12|999","GuiaRemision":"java.lang.String|12|999","Email":"java.lang.String|12|999","Concepto":"java.lang.String|12|999","Recaudador":"java.lang.String|12|999","Referencia":"java.lang.String|12|999","FormaPago":"java.lang.String|12|999","Total":"java.lang.String|12|999","ImporteTotal":"java.math.BigDecimal|12|999","TotalDescuento":"java.math.BigDecimal|12|999","SubTotalIVA0":"java.math.BigDecimal|12|999","SubTotalIVA12":"java.math.BigDecimal|12|999","SubTotalIVA15":"java.math.BigDecimal|12|999","SubTotalNoObjetoIVA":"java.math.BigDecimal|12|999","SubTotalExentoIVA":"java.math.BigDecimal|12|999","TotalICE":"java.math.BigDecimal|12|999","TotalIRBPNR":"java.math.BigDecimal|12|999","TotalIVA12":"java.math.BigDecimal|12|999","TotalIVA15":"java.math.BigDecimal|12|999","Propina":"java.math.BigDecimal|12|999"}'::json),
  ('FacturasCobradas', 'FacturasCobradas', 1, 'catalogo-importado/FacturasCobradas.jrxml', 'catalogo-importado/FacturasCobradas.jasper', '{"desde":"java.util.Date|12|999","hasta":"java.util.Date|12|999","idusuario":"java.lang.Integer|12|999"}'::json),
  ('facturasCobradasCaja', 'facturasCobradasCaja', 1, 'catalogo-importado/facturasCobradasCaja.jrxml', 'catalogo-importado/facturasCobradasCaja.jasper', '{"v_dfecha":"java.util.Date|12|999","v_hfecha":"java.util.Date|12|999","usuariocobro":"java.lang.Long|12|999"}'::json),
  ('FacturasCobradasFacilito', 'FacturasCobradasFacilito', 1, 'catalogo-importado/FacturasCobradasFacilito.jrxml', 'catalogo-importado/FacturasCobradasFacilito.jasper', '{"desde":"java.util.Date|12|999","hasta":"java.util.Date|12|999","idusuario":"java.lang.Integer|12|999","usuariocobro":"java.lang.Integer|12|999"}'::json),
  ('FacturasCobradasFacilitoHoras', 'FacturasCobradasFacilitoHoras', 1, 'catalogo-importado/FacturasCobradasFacilitoHoras.jrxml', 'catalogo-importado/FacturasCobradasFacilitoHoras.jasper', '{"desde":"java.sql.Timestamp|12|999","hasta":"java.sql.Timestamp|12|999","idusuario":"java.lang.Integer|12|999","usuariocobro":"java.lang.Integer|12|999"}'::json),
  ('FacturasCobradasRec', 'FacturasCobradasRec', 1, 'catalogo-importado/FacturasCobradasRec.jrxml', 'catalogo-importado/FacturasCobradasRec.jasper', '{"desde":"java.util.Date|12|999","hasta":"java.util.Date|12|999","idusuario":"java.lang.Integer|12|999","usuariocobro":"java.lang.Integer|12|999"}'::json),
  ('FacturasCobradasXEmision', 'FacturasCobradasXEmision', 1, 'catalogo-importado/FacturasCobradasXEmision.jrxml', 'catalogo-importado/FacturasCobradasXEmision.jasper', '{"idusuario":"java.lang.Integer|12|999","emision":"java.lang.Integer|12|999"}'::json),
  ('nacionalidades', 'nacionalidades', 1, NULL, 'catalogo-importado/nacionalidades.jasper', '{}'::json),
  ('NotasCobradas', 'NotasCobradas', 1, 'catalogo-importado/NotasCobradas.jrxml', 'catalogo-importado/NotasCobradas.jasper', '{"desde":"java.util.Date|12|999","hasta":"java.util.Date|12|999","idusuario":"java.lang.Integer|12|999"}'::json),
  ('NtaCreditoIndividual', 'NtaCreditoIndividual', 1, 'catalogo-importado/NtaCreditoIndividual.jrxml', 'catalogo-importado/NtaCreditoIndividual.jasper', '{"idntacredito":"java.lang.Integer|12|999","idusuario":"java.lang.Integer|12|999"}'::json),
  ('Ref_Facturas_Fecha', 'Ref_Facturas_Fecha', 1, 'catalogo-importado/Ref_Facturas_Fecha.jrxml', 'catalogo-importado/Ref_Facturas_Fecha.jasper', '{"desde":"java.util.Date|12|999","hasta":"java.util.Date|12|999"}'::json),
  ('Refacturaciones', 'Refacturaciones', 1, 'catalogo-importado/Refacturaciones.jrxml', 'catalogo-importado/Refacturaciones.jasper', '{"desde":"java.util.Date|12|999","hasta":"java.util.Date|12|999","idusuario":"java.lang.Integer|12|999"}'::json),
  ('RefacturacionesRubros', 'RefacturacionesRubros', 1, 'catalogo-importado/RefacturacionesRubros.jrxml', 'catalogo-importado/RefacturacionesRubros.jasper', '{"idusuario":"java.lang.Integer|12|999","idemision":"java.lang.Integer|12|999"}'::json),
  ('RepFacturas', 'RepFacturas', 1, 'catalogo-importado/RepFacturas.jrxml', 'catalogo-importado/RepFacturas.jasper', '{"desde":"java.util.Date|12|999","hasta":"java.util.Date|12|999","idusuario":"java.lang.Integer|12|999"}'::json),
  ('report_template', 'report_template', 3, 'catalogo-importado/report_template.jrxml', 'catalogo-importado/report_template.jasper', '{"param1":"java.lang.String|12|999","param2":"java.lang.Integer|12|999"}'::json),
  ('ResumenEmision', 'ResumenEmision', 1, 'catalogo-importado/ResumenEmision.jrxml', 'catalogo-importado/ResumenEmision.jasper', '{"idemision":"java.lang.Integer|12|999"}'::json),
  ('ResumenFacturacion', 'ResumenFacturacion', 1, 'catalogo-importado/ResumenFacturacion.jrxml', 'catalogo-importado/ResumenFacturacion.jasper', '{"estado":"java.lang.String|12|999","numeroAutorizacion":"java.lang.String|12|999","fechaAutorizacion":"java.lang.String|12|999","ambienteAutorizacion":"java.lang.String|12|999","razonSocial":"java.lang.String|12|999","nombreComercial":"java.lang.String|12|999","ruc":"java.lang.String|12|999","claveAcceso":"java.lang.String|12|999","estab":"java.lang.String|12|999","ptoEmi":"java.lang.String|12|999","secuencial":"java.lang.String|12|999","dirMatriz":"java.lang.String|12|999","fechaEmision":"java.lang.String|12|999","obligadoContabilidad":"java.lang.String|12|999","tipoIdentificacionSujetoRetenido":"java.lang.String|12|999","razonSocialSujetoRetenido":"java.lang.String|12|999","identificacionSujetoRetenido":"java.lang.String|12|999","periodoFiscal":"java.lang.String|12|999","direccionSujetoRetenido":"java.lang.String|12|999","telefonoSujetoRetenido":"java.lang.String|12|999","emailSujetoRetenido":"java.lang.String|12|999","fechaInicio":"java.lang.String|12|999","fechaFin":"java.lang.String|12|999"}'::json),
  ('retencion_template', 'retencion_template', 3, 'catalogo-importado/retencion_template.jrxml', 'catalogo-importado/retencion_template.jasper', '{"estado":"java.lang.String|12|999","numeroAutorizacion":"java.lang.String|12|999","fechaAutorizacion":"java.lang.String|12|999","ambienteAutorizacion":"java.lang.String|12|999","razonSocial":"java.lang.String|12|999","nombreComercial":"java.lang.String|12|999","ruc":"java.lang.String|12|999","claveAcceso":"java.lang.String|12|999","estab":"java.lang.String|12|999","ptoEmi":"java.lang.String|12|999","secuencial":"java.lang.String|12|999","dirMatriz":"java.lang.String|12|999","fechaEmision":"java.lang.String|12|999","obligadoContabilidad":"java.lang.String|12|999","tipoIdentificacionSujetoRetenido":"java.lang.String|12|999","razonSocialSujetoRetenido":"java.lang.String|12|999","identificacionSujetoRetenido":"java.lang.String|12|999","periodoFiscal":"java.lang.String|12|999","direccionSujetoRetenido":"java.lang.String|12|999","telefonoSujetoRetenido":"java.lang.String|12|999","emailSujetoRetenido":"java.lang.String|12|999"}'::json),
  ('RetencionElectronica', 'RetencionElectronica', 3, 'catalogo-importado/RetencionElectronica.jrxml', 'catalogo-importado/RetencionElectronica.jasper', '{"RazonSocial":"java.lang.String|12|999","Ruc":"java.lang.String|12|999","NumeroAutorizacion":"java.lang.String|12|999","FechaAutorizacion":"java.lang.String|12|999","FechaEmision":"java.lang.String|12|999","TotalSinImpuestos":"java.lang.String|12|999","DireccionMatriz":"java.lang.String|12|999","DireccionEstablecimiento":"java.lang.String|12|999","NroFactura":"java.lang.String|12|999","Ambiente":"java.lang.String|12|999","AgenteRetencion":"java.lang.String|12|999","ObligadoContabilidad":"java.lang.String|12|999","RazonSocialComprador":"java.lang.String|12|999","IdentificacionComprador":"java.lang.String|12|999","DireccionComprador":"java.lang.String|12|999","GuiaRemision":"java.lang.String|12|999","Email":"java.lang.String|12|999","Concepto":"java.lang.String|12|999","Recaudador":"java.lang.String|12|999","Referencia":"java.lang.String|12|999","FormaPago":"java.lang.String|12|999","Total":"java.lang.String|12|999","ImporteTotal":"java.math.BigDecimal|12|999","TotalDescuento":"java.math.BigDecimal|12|999","SubTotalIVA0":"java.math.BigDecimal|12|999","SubTotalIVA12":"java.math.BigDecimal|12|999","SubTotalIVA15":"java.math.BigDecimal|12|999","SubTotalNoObjetoIVA":"java.math.BigDecimal|12|999","SubTotalExentoIVA":"java.math.BigDecimal|12|999","TotalICE":"java.math.BigDecimal|12|999","TotalIRBPNR":"java.math.BigDecimal|12|999","TotalIVA12":"java.math.BigDecimal|12|999","TotalIVA15":"java.math.BigDecimal|12|999","Propina":"java.math.BigDecimal|12|999"}'::json),
  ('RubrosCobrados', 'RubrosCobrados', 1, 'catalogo-importado/RubrosCobrados.jrxml', 'catalogo-importado/RubrosCobrados.jasper', '{"desde":"java.sql.Timestamp|12|999","hasta":"java.sql.Timestamp|12|999","idusuario":"java.lang.Integer|12|999","tope":"java.util.Date|12|999"}'::json),
  ('RubrosCobradosRec', 'RubrosCobradosRec', 1, 'catalogo-importado/RubrosCobradosRec.jrxml', 'catalogo-importado/RubrosCobradosRec.jasper', '{"desde":"java.sql.Timestamp|12|999","hasta":"java.sql.Timestamp|12|999","idusuario":"java.lang.Integer|12|999","tope":"java.util.Date|12|999","usuariocobro":"java.lang.Integer|12|999"}'::json),
  ('TramitesAgua', 'TramitesAgua', 1, 'catalogo-importado/TramitesAgua.jrxml', 'catalogo-importado/TramitesAgua.jasper', '{"desde":"java.util.Date|12|999","hasta":"java.util.Date|12|999","idusuario":"java.lang.Integer|12|999"}'::json),
  ('TramitesDeAgua', 'TramitesDeAgua', 1, 'catalogo-importado/TramitesDeAgua.jrxml', 'catalogo-importado/TramitesDeAgua.jasper', '{"desde":"java.util.Date|12|999","hasta":"java.util.Date|12|999","idusuario":"java.lang.Integer|12|999"}'::json)
  ) AS c(nomrep, desrep, metodo, jrxml_path, jasper_path, parametros)
  WHERE NOT EXISTS (SELECT 1 FROM reportesjr existente WHERE lower(existente.nomrep) = lower(c.nomrep))
  ON CONFLICT (nomrep) DO NOTHING;
END $catalogo$;
COMMIT;
