package com.epmapat.erp_epmapat.nc;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/** Writes a credit application inside the enclosing collection transaction. */
@Service
public class NcAplicacionCreditoServicio {
    private final JdbcTemplate db;
    private final NcSchemaState schema;

    public NcAplicacionCreditoServicio(JdbcTemplate db, NcSchemaState schema) {
        this.db = db;
        this.schema = schema;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public BigDecimal aplicar(Long facturaId, Long cuenta, BigDecimal importe) {
        if (importe == null || importe.signum() == 0) return BigDecimal.ZERO;
        if (facturaId == null || cuenta == null || importe.signum() < 0 || importe.scale() > 2) {
            throw new IllegalArgumentException("Factura, cuenta e importe de credito invalidos");
        }
        BigDecimal pendiente = importe.setScale(2, RoundingMode.UNNECESSARY);

        // Same order as endoso/reversion: notes first, then the invoice being collected.
        var bloqueadas = db.queryForList("SELECT idntacredito FROM ntacredito WHERE idabonado_abonados=? "
                + "ORDER BY idntacredito FOR UPDATE", cuenta).stream()
                .map(n -> ((Number)n.get("idntacredito")).longValue()).collect(java.util.stream.Collectors.toSet());
        var facturas = db.queryForList("SELECT idabonado FROM facturas WHERE idfactura=? FOR UPDATE", facturaId);
        if (facturas.isEmpty() || facturas.get(0).get("idabonado") == null
                || ((Number) facturas.get(0).get("idabonado")).longValue() != cuenta.longValue()) {
            throw new IllegalArgumentException("La factura no pertenece a la cuenta del credito");
        }
        int aplicaciones = db.queryForObject("SELECT count(*) FROM facxnc f JOIN valoresnc v "
                + "ON v.idvaloresnc=f.idvaloresnc_valoresnc "
                + "WHERE f.idfactura_facturas=? AND v.estado=1", Integer.class, facturaId);
        if (aplicaciones > 0) {
            throw new IllegalStateException("La factura ya tiene una aplicacion de credito registrada");
        }

        boolean migrada = schema.migrated();
        String fuente = migrada ? "nc_saldos" : "ntacredito";
        String disponible = migrada ? "disponible" : "CAST(valor AS numeric(14,2))-CAST(coalesce(devengado,0) AS numeric(14,2))";
        var notas = db.queryForList("SELECT idntacredito," + disponible + " AS saldo FROM " + fuente
                + " WHERE idabonado_abonados=? AND estado=1 AND fechaeliminacion IS NULL ORDER BY idntacredito", cuenta).stream()
                .filter(n -> bloqueadas.contains(((Number)n.get("idntacredito")).longValue())).toList();
        BigDecimal total = notas.stream().map(n -> decimal(n, "saldo").max(BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (total.compareTo(pendiente) < 0) {
            throw new IllegalStateException("La nota de credito no tiene saldo suficiente para cubrir el valor aplicado");
        }
        for (var nota : notas) {
            if (pendiente.signum() == 0) break;
            BigDecimal saldo = decimal(nota, "saldo");
            if (saldo.signum() <= 0) continue;
            BigDecimal uso = saldo.min(pendiente).setScale(2, RoundingMode.UNNECESSARY);
            long notaId = ((Number) nota.get("idntacredito")).longValue();
            db.update("UPDATE ntacredito SET devengado=coalesce(devengado,0)+? WHERE idntacredito=?", uso, notaId);
            long movimiento = db.queryForObject("INSERT INTO valoresnc "
                    + "(estado,valor,fechaaplicado,saldo,idntacredito_ntacredito) "
                    + "VALUES(1,?,current_date,?,?) RETURNING idvaloresnc", Long.class, uso, saldo.subtract(uso), notaId);
            db.update("INSERT INTO facxnc(idfactura_facturas,idvaloresnc_valoresnc) VALUES(?,?)", facturaId, movimiento);
            pendiente = pendiente.subtract(uso);
        }
        // Recaudacion also assigns the same value to its managed Facturas entity.
        db.update("UPDATE facturas SET valornotacredito=? WHERE idfactura=?", importe, facturaId);
        return importe;
    }

    private BigDecimal decimal(Map<String, Object> row, String field) {
        return row.get(field) == null ? BigDecimal.ZERO : new BigDecimal(row.get(field).toString());
    }
}
