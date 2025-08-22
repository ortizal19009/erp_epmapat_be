package com.epmapat.erp_epmapat.repositorio;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epmapat.erp_epmapat.interfaces.ConvenioOneData;
import com.epmapat.erp_epmapat.interfaces.EstadoConvenios;
import com.epmapat.erp_epmapat.modelo.Convenios;

public interface ConveniosR extends JpaRepository<Convenios, Serializable> {

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

}
