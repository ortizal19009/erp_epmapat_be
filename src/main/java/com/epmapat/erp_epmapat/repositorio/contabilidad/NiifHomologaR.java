package com.epmapat.erp_epmapat.repositorio.contabilidad;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.contabilidad.NiifHomologa;

public interface NiifHomologaR extends JpaRepository<NiifHomologa, Long> {

   @Query(value = "SELECT * FROM niifhomologa nh JOIN niifcuentas nc ON nh.idniifcue = nc.idniifcue WHERE nc.idniifcue = ?1 ", nativeQuery = true)
   public List<NiifHomologa> findByIdNiifCue(Long idniifcue);

   @Query(value = "SELECT * FROM niifhomologa ng WHERE idcuenta=?1", nativeQuery = true)
   public List<NiifHomologa> findByIdCuenta(Long idcue);

}