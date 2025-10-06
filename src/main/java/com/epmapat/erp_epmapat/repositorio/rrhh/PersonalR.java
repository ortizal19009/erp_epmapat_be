package com.epmapat.erp_epmapat.repositorio.rrhh;

import org.springframework.data.jpa.repository.JpaRepository;

import com.epmapat.erp_epmapat.modelo.rrhh.Personal;

public interface PersonalR extends JpaRepository<Personal, Long> {
    
}
