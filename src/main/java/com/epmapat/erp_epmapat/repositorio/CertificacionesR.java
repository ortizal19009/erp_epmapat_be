package com.epmapat.erp_epmapat.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.Certificaciones;

//@Repository
public interface CertificacionesR extends JpaRepository<Certificaciones, Long> {

   //Desde / Hasta número
   @Query(value = "SELECT * FROM certificaciones AS c WHERE c.numero >= ?1 and c.numero <= ?2 ORDER BY numero", nativeQuery = true)
   public List<Certificaciones> findDesdeHasta(Long desde, Long hasta);

   //Por Cliente
   @Query(value = "SELECT * FROM certificaciones AS c JOIN facturas as f ON c.idfactura_facturas=f.idfactura JOIN clientes as cl ON f.idcliente=cl.idcliente WHERE (LOWER(cl.nombre) like %?1%) ORDER by cl.nombre", nativeQuery = true)
   public List<Certificaciones> findByCliente(String cliente);

   // Ultima Certificación
   Certificaciones findFirstByOrderByIdcertificacionDesc();

}
