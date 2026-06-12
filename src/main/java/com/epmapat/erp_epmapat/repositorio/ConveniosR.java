package com.epmapat.erp_epmapat.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epmapat.erp_epmapat.interfaces.ConvenioOneData;
import com.epmapat.erp_epmapat.interfaces.ConvenioDetalle;
import com.epmapat.erp_epmapat.interfaces.EstadoConvenios;
import com.epmapat.erp_epmapat.modelo.Convenios;

public interface ConveniosR extends JpaRepository<Convenios, Long> {

  @EntityGraph(attributePaths = {
      "idabonado",
      "idabonado.idresponsable",
      "idabonado.idcliente_clientes",
      "idabonado.idcategoria_categorias",
      "idabonado.idruta_rutas"
  })
  @Override
  List<Convenios> findAll();

  @EntityGraph(attributePaths = {
      "idabonado",
      "idabonado.idresponsable",
      "idabonado.idcliente_clientes",
      "idabonado.idcategoria_categorias",
      "idabonado.idruta_rutas"
  })
  @Override
  Optional<Convenios> findById(Long id);

  List<Convenios> findByNroconvenioBetweenOrderByNroconvenioAsc(Integer desde, Integer hasta);

  // Busca por número de convenio (para validar)
  @Query(value = "SELECT * FROM convenios AS c WHERE c.nroconvenio=?1", nativeQuery = true)
  public List<Convenios> findNroconvenio(Long nroconvenio);

  // Ultimo Número de convenio
  Convenios findFirstByOrderByNroconvenioDesc();

  // Siguiente Número de convenio
  Convenios findTopByOrderByNroconvenioDesc();

  // Valida Nroconvenio
  @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Convenios c WHERE c.nroconvenio = :nroconvenio")
  boolean valNroconvenio(@Param("nroconvenio") Integer nroconvenio);

  @Query(value = "select * from convenios c where idabonado = ?1 order by idconvenio desc ;", nativeQuery = true)
  public List<Convenios> findByReferencia(Long referencia);

  @Query(value = """
      SELECT
      	cv.idconvenio,
      	cv.nroconvenio,
      	cv.idabonado,
      	cv.feccrea,
      	cv.estado,
      	COUNT(DISTINCT fc.idfactura_facturas) AS facantiguas,
      	COUNT(DISTINCT ct.idcuota) AS facnuevas,
      	COUNT(DISTINCT CASE
      					WHEN f.pagado = 1 OR f.fechacobro IS NOT NULL
      					THEN ct.idcuota
      					END) AS facpagadas,
      	(COUNT(DISTINCT ct.idcuota) - COUNT(DISTINCT CASE
      						WHEN f.pagado = 1 OR f.fechacobro IS NOT NULL
      						THEN ct.idcuota
      					END)) AS facpendientes
      	FROM convenios cv
      	LEFT JOIN facxconvenio fc ON cv.idconvenio = fc.idconvenio_convenios
      	LEFT JOIN cuotas ct ON cv.idconvenio = ct.idconvenio_convenios
      	LEFT JOIN facturas f ON ct.idfactura = f.idfactura
      	GROUP BY cv.idconvenio, cv.nroconvenio, cv.idabonado,cv.feccrea, cv.estado order by facpendientes desc
      """, nativeQuery = true)
  List<EstadoConvenios> getEstadoByConvenios();

  @Query(value = """
      SELECT * FROM (
          SELECT
              cv.idconvenio,
              cv.nroconvenio,
              c.nombre,
              cv.idabonado,
              cv.feccrea,
              cv.estado,
              COUNT(DISTINCT fc.idfactura_facturas) AS facAntiguas,
              COUNT(DISTINCT ct.idcuota) AS facNuevas,
              COUNT(DISTINCT CASE
                               WHEN f.pagado = 1 OR f.fechacobro IS NOT NULL
                               THEN ct.idcuota
                             END) AS facPagadas,
              (COUNT(DISTINCT ct.idcuota) - COUNT(DISTINCT CASE
                                 WHEN f.pagado = 1 OR f.fechacobro IS NOT NULL
                                 THEN ct.idcuota
                               END)) AS facPendientes
          FROM convenios cv
          LEFT JOIN facxconvenio fc ON cv.idconvenio = fc.idconvenio_convenios
          LEFT JOIN cuotas ct ON cv.idconvenio = ct.idconvenio_convenios
          LEFT JOIN facturas f ON ct.idfactura = f.idfactura
          LEFT JOIN abonados a ON cv.idabonado = a.idabonado
          LEFT JOIN clientes c ON a.idresponsable = c.idcliente
          GROUP BY cv.idconvenio, cv.nroconvenio, cv.idabonado, cv.feccrea, cv.estado, c.nombre
          HAVING (COUNT(DISTINCT ct.idcuota) - COUNT(DISTINCT CASE
                   WHEN f.pagado = 1 OR f.fechacobro IS NOT NULL THEN ct.idcuota
                 END)) BETWEEN :desde AND :hasta
      ) AS sub
      ORDER BY facPendientes DESC
      """, countQuery = """
      SELECT COUNT(*) FROM (
          SELECT 1
          FROM convenios cv
          LEFT JOIN facxconvenio fc ON cv.idconvenio = fc.idconvenio_convenios
          LEFT JOIN cuotas ct ON cv.idconvenio = ct.idconvenio_convenios
          LEFT JOIN facturas f ON ct.idfactura = f.idfactura
          LEFT JOIN abonados a ON cv.idabonado = a.idabonado
          LEFT JOIN clientes c ON a.idresponsable = c.idcliente
          GROUP BY cv.idconvenio, cv.nroconvenio, cv.idabonado, cv.feccrea, cv.estado, c.nombre
          HAVING (COUNT(DISTINCT ct.idcuota) - COUNT(DISTINCT CASE
                   WHEN f.pagado = 1 OR f.fechacobro IS NOT NULL THEN ct.idcuota
                 END)) > 0
      ) AS sub_count
      """, nativeQuery = true)
  Page<EstadoConvenios> getByFacPendientes(@Param("desde") Long desde,
      @Param("hasta") Long hasta,
      Pageable pageable);

  @Query(value = """
      SELECT * FROM (
          SELECT
              cv.idconvenio,
              cv.nroconvenio,
              c.nombre,
              cv.idabonado,
              cv.feccrea,
              cv.estado,
              COUNT(DISTINCT fc.idfactura_facturas) AS facAntiguas,
              COUNT(DISTINCT ct.idcuota) AS facNuevas,
              COUNT(DISTINCT CASE
                               WHEN f.pagado = 1 OR f.fechacobro IS NOT NULL
                               THEN ct.idcuota
                             END) AS facPagadas,
              (COUNT(DISTINCT ct.idcuota) - COUNT(DISTINCT CASE
                                 WHEN f.pagado = 1 OR f.fechacobro IS NOT NULL
                                 THEN ct.idcuota
                               END)) AS facPendientes
          FROM convenios cv
          LEFT JOIN facxconvenio fc ON cv.idconvenio = fc.idconvenio_convenios
          LEFT JOIN cuotas ct ON cv.idconvenio = ct.idconvenio_convenios
          LEFT JOIN facturas f ON ct.idfactura = f.idfactura
          LEFT JOIN abonados a ON cv.idabonado = a.idabonado
          LEFT JOIN clientes c ON a.idresponsable = c.idcliente
          WHERE cv.idconvenio = ?1
          GROUP BY cv.idconvenio, cv.nroconvenio, cv.idabonado, cv.feccrea, cv.estado, c.nombre
          HAVING (COUNT(DISTINCT ct.idcuota) - COUNT(DISTINCT CASE
                   WHEN f.pagado = 1 OR f.fechacobro IS NOT NULL THEN ct.idcuota
                 END)) > 0
      ) AS sub
      ORDER BY facPendientes DESC
      """, countQuery = """
      SELECT COUNT(*) FROM (
          SELECT 1
          FROM convenios cv
          LEFT JOIN facxconvenio fc ON cv.idconvenio = fc.idconvenio_convenios
          LEFT JOIN cuotas ct ON cv.idconvenio = ct.idconvenio_convenios
          LEFT JOIN facturas f ON ct.idfactura = f.idfactura
          LEFT JOIN abonados a ON cv.idabonado = a.idabonado
          LEFT JOIN clientes c ON a.idresponsable = c.idcliente
          WHERE cv.idconvenio = ?1
          GROUP BY cv.idconvenio, cv.nroconvenio, cv.idabonado, cv.feccrea, cv.estado, c.nombre
          HAVING (COUNT(DISTINCT ct.idcuota) - COUNT(DISTINCT CASE
                   WHEN f.pagado = 1 OR f.fechacobro IS NOT NULL THEN ct.idcuota
                 END)) > 0
      ) AS sub_count
      """, nativeQuery = true)
  List<EstadoConvenios> gePendienteByConvenio(Long idconvenio);

  @Query(value = """
          SELECT
          ct.idconvenio_convenios as idconvenio,
          c.nroconvenio,
          COUNT(*) AS cuotas,
          COUNT(*) FILTER (WHERE f.pagado = 1) AS pagado,
          COUNT(*) FILTER (WHERE f.pagado = 0) AS nopagado
      FROM cuotas ct
      JOIN facturas f ON ct.idfactura = f.idfactura
      JOIN convenios c ON c.idconvenio = ct.idconvenio_convenios
      WHERE ct.idconvenio_convenios = ?1
      GROUP BY ct.idconvenio_convenios, c.nroconvenio;
          """, nativeQuery = true)
  List<ConvenioOneData> findDatosConvenio(Long idconvenio);

  @Query(value = """
      SELECT * FROM (
          SELECT
              cv.idconvenio,
              cv.nroconvenio,
              c.nombre,
              cv.idabonado,
              cv.feccrea,
              cv.estado,
              cv.nroautorizacion,
              cv.referencia,
              cv.totalconvenio,
              cv.cuotas,
              cv.cuotainicial,
              cv.pagomensual,
              cv.cuotafinal,
              COUNT(DISTINCT fc.idfactura_facturas) AS facAntiguas,
              COUNT(DISTINCT ct.idcuota) AS facNuevas,
              COUNT(DISTINCT CASE
                               WHEN f.pagado = 1 OR f.fechacobro IS NOT NULL
                               THEN ct.idcuota
                             END) AS facPagadas,
              (COUNT(DISTINCT ct.idcuota) - COUNT(DISTINCT CASE
                                 WHEN f.pagado = 1 OR f.fechacobro IS NOT NULL
                                 THEN ct.idcuota
                               END)) AS facPendientes
          FROM convenios cv
          LEFT JOIN facxconvenio fc ON cv.idconvenio = fc.idconvenio_convenios
          LEFT JOIN cuotas ct ON cv.idconvenio = ct.idconvenio_convenios
          LEFT JOIN facturas f ON ct.idfactura = f.idfactura
          LEFT JOIN abonados a ON cv.idabonado = a.idabonado
          LEFT JOIN clientes c ON a.idresponsable = c.idcliente
          WHERE (:nroDesde IS NULL OR cv.nroconvenio >= :nroDesde)
            AND (:nroHasta IS NULL OR cv.nroconvenio <= :nroHasta)
            AND (:estado IS NULL OR cv.estado = :estado)
            AND (:idabonado IS NULL OR cv.idabonado = :idabonado)
            AND (:nombre IS NULL OR LOWER(c.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')))
          GROUP BY cv.idconvenio, cv.nroconvenio, cv.idabonado, cv.feccrea, cv.estado, c.nombre,
                   cv.nroautorizacion, cv.referencia, cv.totalconvenio, cv.cuotas, cv.cuotainicial,
                   cv.pagomensual, cv.cuotafinal
          HAVING (:minPendientes IS NULL OR
                    (COUNT(DISTINCT ct.idcuota) - COUNT(DISTINCT CASE
                        WHEN f.pagado = 1 OR f.fechacobro IS NOT NULL THEN ct.idcuota
                     END)) >= :minPendientes)
             AND (:maxPendientes IS NULL OR
                    (COUNT(DISTINCT ct.idcuota) - COUNT(DISTINCT CASE
                        WHEN f.pagado = 1 OR f.fechacobro IS NOT NULL THEN ct.idcuota
                     END)) <= :maxPendientes)
      ) AS sub
      ORDER BY sub.nroconvenio DESC
      """, countQuery = """
      SELECT COUNT(*) FROM (
          SELECT cv.idconvenio
          FROM convenios cv
          LEFT JOIN facxconvenio fc ON cv.idconvenio = fc.idconvenio_convenios
          LEFT JOIN cuotas ct ON cv.idconvenio = ct.idconvenio_convenios
          LEFT JOIN facturas f ON ct.idfactura = f.idfactura
          LEFT JOIN abonados a ON cv.idabonado = a.idabonado
          LEFT JOIN clientes c ON a.idresponsable = c.idcliente
          WHERE (:nroDesde IS NULL OR cv.nroconvenio >= :nroDesde)
            AND (:nroHasta IS NULL OR cv.nroconvenio <= :nroHasta)
            AND (:estado IS NULL OR cv.estado = :estado)
            AND (:idabonado IS NULL OR cv.idabonado = :idabonado)
            AND (:nombre IS NULL OR LOWER(c.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')))
          GROUP BY cv.idconvenio, cv.nroconvenio, cv.idabonado, cv.feccrea, cv.estado, c.nombre,
                   cv.nroautorizacion, cv.referencia, cv.totalconvenio, cv.cuotas, cv.cuotainicial,
                   cv.pagomensual, cv.cuotafinal
          HAVING (:minPendientes IS NULL OR
                    (COUNT(DISTINCT ct.idcuota) - COUNT(DISTINCT CASE
                        WHEN f.pagado = 1 OR f.fechacobro IS NOT NULL THEN ct.idcuota
                     END)) >= :minPendientes)
             AND (:maxPendientes IS NULL OR
                    (COUNT(DISTINCT ct.idcuota) - COUNT(DISTINCT CASE
                        WHEN f.pagado = 1 OR f.fechacobro IS NOT NULL THEN ct.idcuota
                     END)) <= :maxPendientes)
      ) AS sub_count
      """, nativeQuery = true)
  Page<ConvenioDetalle> buscarConvenios(
      @Param("nroDesde") Integer nroDesde,
      @Param("nroHasta") Integer nroHasta,
      @Param("nombre") String nombre,
      @Param("estado") Integer estado,
      @Param("minPendientes") Long minPendientes,
      @Param("maxPendientes") Long maxPendientes,
      @Param("idabonado") Long idabonado,
      Pageable pageable);

  @Query(value = """
      SELECT * FROM (
          SELECT
              cv.idconvenio,
              cv.nroconvenio,
              c.nombre,
              cv.idabonado,
              cv.feccrea,
              cv.estado,
              cv.nroautorizacion,
              cv.referencia,
              cv.totalconvenio,
              cv.cuotas,
              cv.cuotainicial,
              cv.pagomensual,
              cv.cuotafinal,
              COUNT(DISTINCT fc.idfactura_facturas) AS facAntiguas,
              COUNT(DISTINCT ct.idcuota) AS facNuevas,
              COUNT(DISTINCT CASE
                               WHEN f.pagado = 1 OR f.fechacobro IS NOT NULL
                               THEN ct.idcuota
                             END) AS facPagadas,
              (COUNT(DISTINCT ct.idcuota) - COUNT(DISTINCT CASE
                                 WHEN f.pagado = 1 OR f.fechacobro IS NOT NULL
                                 THEN ct.idcuota
                               END)) AS facPendientes
          FROM convenios cv
          LEFT JOIN facxconvenio fc ON cv.idconvenio = fc.idconvenio_convenios
          LEFT JOIN cuotas ct ON cv.idconvenio = ct.idconvenio_convenios
          LEFT JOIN facturas f ON ct.idfactura = f.idfactura
          LEFT JOIN abonados a ON cv.idabonado = a.idabonado
          LEFT JOIN clientes c ON a.idresponsable = c.idcliente
          WHERE (:estado IS NULL OR cv.estado = :estado)
          GROUP BY cv.idconvenio, cv.nroconvenio, cv.idabonado, cv.feccrea, cv.estado, c.nombre,
                   cv.nroautorizacion, cv.referencia, cv.totalconvenio, cv.cuotas, cv.cuotainicial,
                   cv.pagomensual, cv.cuotafinal
          HAVING (COUNT(DISTINCT ct.idcuota) - COUNT(DISTINCT CASE
                    WHEN f.pagado = 1 OR f.fechacobro IS NOT NULL THEN ct.idcuota
                 END)) = 0
      ) AS sub
      ORDER BY sub.nroconvenio DESC
      """, nativeQuery = true)
  List<ConvenioDetalle> findConveniosSinPendientes(@Param("estado") Integer estado);

  @Query(value = """
      SELECT * FROM (
          SELECT
              cv.idconvenio,
              cv.nroconvenio,
              c.nombre,
              cv.idabonado,
              cv.feccrea,
              cv.estado,
              cv.nroautorizacion,
              cv.referencia,
              cv.totalconvenio,
              cv.cuotas,
              cv.cuotainicial,
              cv.pagomensual,
              cv.cuotafinal,
              COUNT(DISTINCT fc.idfactura_facturas) AS facAntiguas,
              COUNT(DISTINCT ct.idcuota) AS facNuevas,
              COUNT(DISTINCT CASE
                               WHEN f.pagado = 1 OR f.fechacobro IS NOT NULL
                               THEN ct.idcuota
                             END) AS facPagadas,
              (COUNT(DISTINCT ct.idcuota) - COUNT(DISTINCT CASE
                                 WHEN f.pagado = 1 OR f.fechacobro IS NOT NULL
                                 THEN ct.idcuota
                               END)) AS facPendientes
          FROM convenios cv
          LEFT JOIN facxconvenio fc ON cv.idconvenio = fc.idconvenio_convenios
          LEFT JOIN cuotas ct ON cv.idconvenio = ct.idconvenio_convenios
          LEFT JOIN facturas f ON ct.idfactura = f.idfactura
          LEFT JOIN abonados a ON cv.idabonado = a.idabonado
          LEFT JOIN clientes c ON a.idresponsable = c.idcliente
          WHERE (:estado IS NULL OR cv.estado = :estado)
          GROUP BY cv.idconvenio, cv.nroconvenio, cv.idabonado, cv.feccrea, cv.estado, c.nombre,
                   cv.nroautorizacion, cv.referencia, cv.totalconvenio, cv.cuotas, cv.cuotainicial,
                   cv.pagomensual, cv.cuotafinal
          HAVING (COUNT(DISTINCT ct.idcuota) - COUNT(DISTINCT CASE
                    WHEN f.pagado = 1 OR f.fechacobro IS NOT NULL THEN ct.idcuota
                 END)) > 0
      ) AS sub
      ORDER BY sub.facpendientes DESC, sub.nroconvenio DESC
      """, nativeQuery = true)
  List<ConvenioDetalle> findConveniosConPendientes(@Param("estado") Integer estado);

}
