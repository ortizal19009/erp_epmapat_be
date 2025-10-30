package com.epmapat.erp_epmapat.repositorio.contabilidad;

import org.springframework.data.jpa.repository.JpaRepository;

import com.epmapat.erp_epmapat.modelo.contabilidad.ConciliaBan;
import com.epmapat.erp_epmapat.modelo.contabilidad.Cuentas;

public interface ConciliaBanR extends JpaRepository<ConciliaBan, Long>{

   ConciliaBan findByIdcuentaAndMes(Cuentas idcuenta, Integer mes);

}
