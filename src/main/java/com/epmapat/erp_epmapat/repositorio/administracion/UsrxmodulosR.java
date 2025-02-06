package com.epmapat.erp_epmapat.repositorio.administracion;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.interfaces.ErpModulosI;
import com.epmapat.erp_epmapat.modelo.administracion.Usrxmodulos;

public interface UsrxmodulosR extends JpaRepository<Usrxmodulos, Long> {
    @Query(value = "SELECT em.descripcion, um.enabled FROM usrxmodulos um join erpmodulos em on um.iderpmodulo_erpmodulos = em.iderpmodulo where um.idusuario_usuarios = ?1 order by um.iderpmodulo_erpmodulos asc", nativeQuery = true)
    public List<ErpModulosI> findModulosEnabledByUser(Long iduser);
    @Query(value = "SELECT * from usrxmodulos um where um.idusuario_usuarios = ?1 order by um.iderpmodulo_erpmodulos asc", nativeQuery = true)
    public List<Usrxmodulos> FindByUser(Long iduser);
    @Query(value = "SELECT * FROM usrxmodulos um where um.idusuario_usuarios = ?1 and um.iderpmodulo_erpmodulos = ?2", nativeQuery = true)
    public Usrxmodulos findModulos(Long iduser, Long iderpmodulo);
}
