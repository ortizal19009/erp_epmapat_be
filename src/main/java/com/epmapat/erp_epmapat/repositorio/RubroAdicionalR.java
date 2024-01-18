package com.epmapat.erp_epmapat.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.RubroAdicionalM;

public interface RubroAdicionalR extends JpaRepository<RubroAdicionalM, Long>{
	
	@Query(value = "SELECT * FROM rubroadicional r2 INNER JOIN rubros r3 ON r2.idrubro_rubros = r3.idrubro WHERE r2.idtptramite_tptramite=?1", nativeQuery = true)
	public List<RubroAdicionalM> findByIdTpTramtie(Long idtptramite);
		
}
