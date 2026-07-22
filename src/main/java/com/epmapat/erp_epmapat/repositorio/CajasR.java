package com.epmapat.erp_epmapat.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.Cajas;

public interface CajasR extends JpaRepository<Cajas, Long> {

   @Query("""
         select c
         from Cajas c
         left join fetch c.idptoemision_ptoemision p
         left join fetch c.idusuario_usuarios u
         order by p.establecimiento, c.codigo
         """)
	public List<Cajas> findAll();

   @Query("""
         select c
         from Cajas c
         left join fetch c.idptoemision_ptoemision p
         left join fetch c.idusuario_usuarios u
         where p.idptoemision = ?1 and c.codigo = ?2
         """)
	public List<Cajas> findByCodigos(Long idptoemision, String codigo);

   @Query("""
         select c
         from Cajas c
         left join fetch c.idptoemision_ptoemision p
         left join fetch c.idusuario_usuarios u
         where c.descripcion = ?1
         """)
	List<Cajas> findByDescri(String descripcion);

   @Query("""
         select c
         from Cajas c
         left join fetch c.idptoemision_ptoemision p
         left join fetch c.idusuario_usuarios u
         where p.idptoemision = ?1
         order by c.codigo
         """)
	public List<Cajas> findByIdptoemision(Long idptoemision);

   @Query("""
         select c
         from Cajas c
         left join fetch c.idptoemision_ptoemision p
         left join fetch c.idusuario_usuarios u
         where u.idusuario = :idusuario
         """)
   public Cajas findCajaByIdUsuario(@org.springframework.data.repository.query.Param("idusuario") Long idusuario);

   @Query("""
         select c
         from Cajas c
         left join fetch c.idptoemision_ptoemision p
         left join fetch c.idusuario_usuarios u
         where c.idusuario_usuarios is not null
           and c.estado = 1
         order by p.establecimiento, c.codigo
         """)
   public List<Cajas> findCajasActivas();

}
