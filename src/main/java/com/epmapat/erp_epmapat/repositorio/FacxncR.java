package com.epmapat.erp_epmapat.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.interfaces.NtaCreditoCompPago;
import com.epmapat.erp_epmapat.modelo.Facxnc;

public interface FacxncR extends JpaRepository<Facxnc, Long>{
    @Query(value = "SELECT * FROM facxnc fnc WHERE fnc.idvaloresnc_valoresnc ",nativeQuery = true)
    List<Facxnc> findByIdvalnc(Long idvalnc);
@Query(value = """
select
	vnc.fechaaplicado,
	vnc.valor,
	vnc.saldo,
	f.idfactura, 
	vnc.idntacredito_ntacredito
	from
	facxnc fnc
join valoresnc vnc on
	fnc.idvaloresnc_valoresnc = vnc.idvaloresnc
join facturas f on
	f.idfactura = fnc.idfactura_facturas
where fnc.idfactura_facturas  = ?1""", nativeQuery = true)
List <NtaCreditoCompPago> findByIdfactura(Long idfactura);
}
