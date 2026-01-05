package com.epmapat.erp_epmapat.repositorio.administracion;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.epmapat.erp_epmapat.modelo.administracion.Erpmodulos;

public interface ErpmodulosR extends JpaRepository<Erpmodulos, Long> {

    List<Erpmodulos> findByPlatform(String platform);
}
