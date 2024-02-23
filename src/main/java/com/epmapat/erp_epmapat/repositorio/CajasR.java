package com.epmapat.erp_epmapat.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.Cajas;

public interface CajasR extends JpaRepository<Cajas, Long> {

   @Query(value = "SELECT * FROM cajas AS c JOIN ptoemision AS p ON c.idptoemision_ptoemision = p.idptoemision ORDER BY p.establecimiento, c.codigo ASC", nativeQuery = true)
	public List<Cajas> findAll();
   //Validación de Códigos (Establecimiento + Pto de emision)
   @Query(value = "SELECT * FROM cajas AS c JOIN ptoemision AS p ON c.idptoemision_ptoemision = p.idptoemision WHERE c.idptoemision_ptoemision=?1 AND c.codigo=?2", nativeQuery = true)
	public List<Cajas> findByCodigos( Long idptoemision, String codigo);
   //Validación de Descripcion
   @Query(value = "SELECT * FROM cajas AS c WHERE c.descripcion=?1", nativeQuery = true)
	List<Cajas> findByDescri(String descripcion);
   //Puntos de emision por Establecimiento
   @Query(value = "SELECT * FROM cajas WHERE idptoemision_ptoemision=?1 ORDER BY codigo", nativeQuery=true)
	public List<Cajas> findByIdptoemision(Long idptoemision);
   @Query(value = "SELECT * FROM cajas WHERE idusuario_usuarios = ?1", nativeQuery = true)
   public Cajas findCajaByIdUsuario(Long idusuario); 

}
