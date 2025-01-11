package com.epmapat.erp_epmapat.repositorio.contabilidad;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.contabilidad.BenexTran;

public interface BenexTranR extends JpaRepository<BenexTran, Long> {

	@Query(value = "SELECT * FROM benextran b join transaci t on b.inttra = t.inttra WHERE t.codcue LIKE ?1% AND t.debcre = 2", nativeQuery = true)
	public List<BenexTran> getEgresos(String codcue);

	// Benextran de un Beneficiario desde/hasta
	@Query(value = "select * from benextran b join transaci t on b.inttra = t.inttra join asientos a on t.idasiento = a.idasiento where t.idbene = ?1 and a.fecha >=?2 and a.fecha <=?3 order by a.fecha", nativeQuery = true)
	public List<BenexTran> getByIdBeneDesdeHasta(Long idbene, Date desde, Date hasta);

	// Benextran de un Beneficiario de la 213 ??
	@Query(value = "select * from benextran b join transaci t on b.inttra = t.inttra join asientos a on t.idasiento = a.idasiento where t.codcue like '213%' and t.idbene = ?1", nativeQuery = true)
	public List<BenexTran> getByIdBene213(Long idbene);

	@Query(value = "select * from benextran b join transaci t on b.inttra = t.inttra  where t.codcue like '213%' and t.debcre  = 2 and b.valor > b.totpagcob ", nativeQuery = true)
	public List<BenexTran> getCxP();

	// ACFP sin liquidar
	@Query(value = "select * from beneficiarios b JOIN benextran x on b.idbene=x.idbene JOIN transaci t ON x.inttra=t.inttra JOIN asientos a ON a.idasiento=t.idasiento where a.fecha<=?1 and b.nomben like %?2% and t.tiptran=?3 and t.codcue=?4 order by b.nomben", nativeQuery = true)
	public List<BenexTran> getACFP(Date hasta, String nomben, Integer tiptran, String codcue);

	// Verifica si un Beneficiario tiene registros en benextran
	@Query(value = "SELECT EXISTS (SELECT 1 FROM benextran WHERE idbene = ?1)", nativeQuery = true)
	boolean existeByIdbene(Long idbene);

}
