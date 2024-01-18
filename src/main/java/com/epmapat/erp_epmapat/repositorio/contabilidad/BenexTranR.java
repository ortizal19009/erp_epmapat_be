package com.epmapat.erp_epmapat.repositorio.contabilidad;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.contabilidad.BenexTran;

public interface BenexTranR extends JpaRepository<BenexTran, Long> {

	@Query(value = "SELECT * FROM benextran b join transaci t on b.idtransa = t.idtransa WHERE t.codcue LIKE ?1% AND t.debcre = 2", nativeQuery = true)
	public List<BenexTran> getEgresos(String codcue);

	@Query (value = "select * from benextran b join transaci t on b.idtransa = t.idtransa join asientos a on t.idasiento = a.idasiento where t.codcue like '213%' and t.idbene = ?1", nativeQuery = true)
	public List<BenexTran> getByIdBene(Long idbene);
   
	@Query(value = "select * from benextran b join transaci t on b.idtransa = t.idtransa  where t.codcue like '213%' and t.debcre  = 2 and b.valor > b.totpagcob ", nativeQuery = true)
	public List<BenexTran> getCxP();

	//ACFP sin liquidar
	@Query(value = "select * from beneficiarios b JOIN benextran x on b.idbene=x.idbene JOIN transaci t ON x.idtransa=t.idtransa JOIN asientos a ON a.idasiento=t.idasiento where a.fecha<=?1 and b.nomben like %?2% and t.tiptran=?3 and t.codcue=?4 order by b.nomben", nativeQuery = true)
	public List<BenexTran> getACFP(Date hasta, String nomben, Integer tiptran, String codcue);

}
