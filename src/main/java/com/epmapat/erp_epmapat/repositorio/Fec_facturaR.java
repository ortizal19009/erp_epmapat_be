package com.epmapat.erp_epmapat.repositorio;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.epmapat.erp_epmapat.interfaces.FecFacturaGestionProjection;
import com.epmapat.erp_epmapat.modelo.Fec_factura;
import com.epmapat.erp_epmapat.sri.interfaces.fecFacturaDatos;

public interface Fec_facturaR extends JpaRepository<Fec_factura, Long> {
    @Query(value = "SELECT * FROM fec_factura where estado like ?1 order by idfactura asc limit ?2 ", nativeQuery = true)
    public List<Fec_factura> findByEstado(String estado, Long limit);

    @Query(value = "SELECT * FROM fec_factura where referencia = ?1 order by idfactura asc ", nativeQuery = true)
    public List<Fec_factura> findByCuenta(String referencia);

    @Query(value = "SELECT * FROM fec_factura where LOWER(razonsocialcomprador) like %?1% order by idfactura asc ", nativeQuery = true)
    public List<Fec_factura> findByNombreCliente(String cliente);

    @Query(value = "SELECT * FROM fec_factura where idfactura = ?1 ", nativeQuery = true)
    public List<Fec_factura> findByNroFactura(Long idfactura);

    @Query(value = "select xmlautorizado, fechaemision from fec_factura where idfactura = ?1", nativeQuery = true)
    public fecFacturaDatos getNroFactura(Long idfactura);

    @Query(value = "SELECT * FROM fec_factura WHERE estado IN ('A', 'O') AND fechaemision >= ?1 AND fechaemision < ?2 ORDER BY fechaemision ASC, idfactura ASC", nativeQuery = true)
    public List<Fec_factura> findByFechaEmisionAndEstados(LocalDateTime desde, LocalDateTime hastaExclusive);

    @Query(value = """
            SELECT *
            FROM fec_factura
            WHERE estado = 'P'
              AND COALESCE(intentos_autorizacion, 0) < ?1
            ORDER BY COALESCE(fecha_ultimo_intento, fechaemision) ASC, idfactura ASC
            LIMIT ?2
            """, nativeQuery = true)
    List<Fec_factura> findPendientesAutorizacion(Integer maxIntentos, Integer limit);

    @Query(value = """
            SELECT *
            FROM fec_factura
            WHERE estado = 'X'
              AND COALESCE(mail_enviado, FALSE) = FALSE
            ORDER BY COALESCE(fecha_autorizacion, fechaemision) ASC, idfactura ASC
            LIMIT ?1
            """, nativeQuery = true)
    List<Fec_factura> findListasParaCorreo(Integer limit);

    @Query(value = """
            WITH ultima_cola AS (
                SELECT DISTINCT ON (q.idfactura)
                    q.idfactura,
                    q.estado,
                    q.intentos,
                    q.ultimo_error,
                    q.fecha_crea
                FROM fec_mail_queue q
                ORDER BY q.idfactura, q.fecha_crea DESC, q.id DESC
            )
            SELECT
                f.idfactura,
                f.claveacceso,
                f.secuencial,
                f.xmlautorizado,
                f.errores,
                f.estado,
                f.establecimiento,
                f.puntoemision,
                f.direccionestablecimiento,
                f.fechaemision,
                f.tipoidentificacioncomprador,
                f.guiaremision,
                f.razonsocialcomprador,
                f.identificacioncomprador,
                f.direccioncomprador,
                f.telefonocomprador,
                f.emailcomprador,
                f.concepto,
                f.referencia,
                f.recaudador,
                fac.usuariocobro,
                COALESCE(f.mail_enviado, FALSE) AS swmail,
                COALESCE(uc.intentos, f.mail_intentos, 0) AS mail_intentos,
                COALESCE(uc.ultimo_error, f.mail_error) AS mail_error,
                COALESCE(
                    uc.estado,
                    f.email_estado,
                    CASE
                        WHEN COALESCE(f.mail_enviado, FALSE) = TRUE THEN 'ENVIADO'
                        ELSE 'NO_ENVIADO'
                    END
                ) AS email_estado
            FROM fec_factura f
            LEFT JOIN facturas fac ON fac.idfactura = f.idfactura
            LEFT JOIN ultima_cola uc ON uc.idfactura = f.idfactura
            WHERE (:numeroFactura IS NULL OR CONCAT(COALESCE(f.establecimiento,''), '-', COALESCE(f.puntoemision,''), '-', COALESCE(f.secuencial,'')) ILIKE CONCAT('%', :numeroFactura, '%'))
              AND (:claveAcceso IS NULL OR COALESCE(f.claveacceso,'') ILIKE CONCAT('%', :claveAcceso, '%'))
              AND (:cliente IS NULL OR COALESCE(f.razonsocialcomprador,'') ILIKE CONCAT('%', :cliente, '%'))
              AND (:identificacion IS NULL OR COALESCE(f.identificacioncomprador,'') ILIKE CONCAT('%', :identificacion, '%'))
              AND (:establecimiento IS NULL OR COALESCE(f.establecimiento,'') = :establecimiento)
              AND (
                    :puntoEmision IS NULL
                    OR COALESCE(f.puntoemision,'') = :puntoEmision
                    OR CONCAT(COALESCE(f.establecimiento,''), '-', COALESCE(f.puntoemision,'')) = :puntoEmision
                  )
              AND (:idusuario IS NULL OR fac.usuariocobro = :idusuario)
              AND (:fechaDesde IS NULL OR f.fechaemision >= CAST(:fechaDesde AS timestamp))
              AND (:fechaHasta IS NULL OR f.fechaemision < CAST(:fechaHasta AS timestamp) + interval '1 day')
              AND (:secuencialDesde IS NULL OR CAST(REGEXP_REPLACE(COALESCE(f.secuencial,'0'), '\\D', '', 'g') AS bigint) >= CAST(:secuencialDesde AS bigint))
              AND (:secuencialHasta IS NULL OR CAST(REGEXP_REPLACE(COALESCE(f.secuencial,'0'), '\\D', '', 'g') AS bigint) <= CAST(:secuencialHasta AS bigint))
              AND (:estadoSri IS NULL OR COALESCE(f.estado, '') = :estadoSri)
              AND (:emailEstado IS NULL OR COALESCE(
                    uc.estado,
                    f.email_estado,
                    CASE WHEN COALESCE(f.mail_enviado, FALSE) = TRUE THEN 'ENVIADO' ELSE 'NO_ENVIADO' END
                  ) = :emailEstado)
              AND (:swmail IS NULL OR COALESCE(f.mail_enviado, FALSE) = :swmail)
              AND (:mailIntentos IS NULL OR COALESCE(uc.intentos, f.mail_intentos, 0) = :mailIntentos)
              AND (:mailError IS NULL OR COALESCE(uc.ultimo_error, f.mail_error, '') ILIKE CONCAT('%', :mailError, '%'))
              AND (:soloFallidos IS NULL OR :soloFallidos = FALSE OR COALESCE(
                    uc.estado,
                    f.email_estado,
                    CASE WHEN COALESCE(f.mail_enviado, FALSE) = TRUE THEN 'ENVIADO' ELSE 'NO_ENVIADO' END
                  ) IN ('ERROR', 'ERROR_ENVIO'))
            ORDER BY f.fechaemision DESC, f.idfactura DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<FecFacturaGestionProjection> buscarGestion(
            @Param("numeroFactura") String numeroFactura,
            @Param("claveAcceso") String claveAcceso,
            @Param("estadoSri") String estadoSri,
            @Param("cliente") String cliente,
            @Param("identificacion") String identificacion,
            @Param("establecimiento") String establecimiento,
            @Param("puntoEmision") String puntoEmision,
            @Param("idusuario") Long idusuario,
            @Param("secuencialDesde") String secuencialDesde,
            @Param("secuencialHasta") String secuencialHasta,
            @Param("fechaDesde") String fechaDesde,
            @Param("fechaHasta") String fechaHasta,
            @Param("emailEstado") String emailEstado,
            @Param("swmail") Boolean swmail,
            @Param("mailIntentos") Integer mailIntentos,
            @Param("mailError") String mailError,
            @Param("soloFallidos") Boolean soloFallidos,
            @Param("limit") Integer limit);
    @Transactional
    @Modifying
    @Query("delete from Fec_factura f where f.idfactura = :idfactura")
    void deleteByIdfactura(@Param("idfactura") Long idfactura);
}

