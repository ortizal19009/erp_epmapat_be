package com.epmapat.erp_epmapat.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.Lecturas;

public interface LecturasR extends JpaRepository<Lecturas, Long> {

	// Lectura por Planilla (Es una a una)
	@Query(value = "SELECT * FROM lecturas WHERE idfactura=?1 ", nativeQuery = true)
	public Lecturas findOnefactura(Long idfactura);

	// Lecturas por rutasxemision
	@Query(value = "SELECT * FROM lecturas WHERE idrutaxemision_rutasxemision=?1 order by idabonado_abonados", nativeQuery = true)
	public List<Lecturas> findByIdrutaxemision(Long idrutasxemision);

	// Lecturas por Abonado (Historial de consumo)
	@Query(value = "SELECT * FROM lecturas WHERE idabonado_abonados=?1 ORDER BY idlectura DESC LIMIT 12", nativeQuery = true)
	public List<Lecturas> findByIdabonado(Long idabonado);

	@Query(value = "SELECT * FROM lecturas WHERE mesesmulta>=4 and estado=1 LIMIT 20", nativeQuery = true)
	public List<Lecturas> findByMonth();

	@Query(value = "SELECT * FROM lecturas WHERE idabonado_abonados=?1 ", nativeQuery = true)
	public List<Lecturas> findLecturasByIdAbonados(Long idabonado);

	@Query(value = "SELECT * FROM lecturas WHERE idrutaxemision_rutasxemision = ?1", nativeQuery = true)
	public List<Lecturas> findByIdRutasxEmision(Long idrutaxemision);

	@Query(value = "SELECT * FROM lecturas l INNER JOIN rutasxemision r ON l.idrutaxemision_rutasxemision = r.idrutaxemision INNER JOIN rutas r2 ON r.idruta_rutas = ?1", nativeQuery = true)
	public List<Lecturas> findByRutas(Long idrutas);

	@Query(value = "SELECT * FROM lecturas l INNER JOIN abonados a ON l.idabonado_abonados = a.idabonado  WHERE l.idabonado_abonados = ?1 AND mesesmulta >=4", nativeQuery = true)
	public List<Lecturas> findByIdAbonado(Long idabonado);

	@Query(value = "SELECT * FROM lecturas l INNER JOIN abonados a ON l.idabonado_abonados = a.idabonado INNER JOIN clientes c ON a.idcliente_clientes = c.idcliente WHERE LOWER(c.nombre) LIKE %?1% AND mesesmulta >=4", nativeQuery = true)
	public List<Lecturas> findByNCliente(String nombre);

	@Query(value = "SELECT * FROM lecturas l INNER JOIN abonados a ON l.idabonado_abonados = a.idabonado INNER JOIN clientes c ON a.idcliente_clientes = c.idcliente WHERE c.cedula LIKE %?1% AND mesesmulta >=4", nativeQuery = true)
	public List<Lecturas> findByICliente(String identificacion);

	// Lectura por Planilla (Es una a una)
	@Query(value = "SELECT * FROM lecturas WHERE idfactura=?1 ", nativeQuery = true)
	public List<Lecturas> findByIdfactura(Long idfactura);

	// Lecturas de una Emisión
	@Query(value = "SELECT * FROM lecturas WHERE idemision=?1 ", nativeQuery = true)
	public List<Lecturas> findByIdemision(Long idemision);

	// Ultima lectura de un Abonado: debe ser lecturaactual tempoaralmente
	// lecturaanterior porque no están cerradas las rutas de la emisión anterior
	@Query(value = "SELECT lecturaactual FROM lecturas WHERE idabonado_abonados=?1 ORDER BY idlectura DESC LIMIT 1", nativeQuery = true)
	public Long ultimaLectura(Long idabonado);
}
