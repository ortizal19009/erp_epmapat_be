package com.epmapat.erp_epmapat.repositorio.administracion;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epmapat.erp_epmapat.interfaces.UsuarioI;
import com.epmapat.erp_epmapat.modelo.administracion.Usuarios;

public interface UsuariosR extends JpaRepository<Usuarios, Long> {

   // Todos (Excepto el Administrador)
   @Query("SELECT u FROM Usuarios u LEFT JOIN FETCH u.personal ORDER BY u.identificausu")
   List<Usuarios> findAll();

   // Busca un usuario por Identificación
   @Query(value = "SELECT * FROM usuarios where identificausu=?1", nativeQuery = true)
   Usuarios findByIdentificausu(String identificausu);

   @Query(value = "SELECT * FROM usuarios where identificausu=?1 AND codusu=?2", nativeQuery = true)
   Usuarios findUsuario(String a, String b);

   @Query(value = "select u.idusuario as idusuario, u.identificausu as identificacion, u.nomusu as nombre, u.alias as alias, u.estado as estado from usuarios u where idusuario = ?1", nativeQuery = true)
   UsuarioI findDatosById(Long idusuario);

   @Query(value = """
         select
            u.nomusu,
            u.codusu,
            u.idusuario,
            u.alias,
            u.estado,
            u.plataform_access
         from usuarios u
         where upper(trim(u.nomusu)) = upper(trim(?1))
            or upper(trim(coalesce(u.alias, ''))) = upper(trim(?1))
         order by
            case when upper(trim(u.nomusu)) = upper(trim(?1)) then 0 else 1 end,
            u.idusuario
         limit 1
         """, nativeQuery = true)
   UsuarioI chargeLogin(String nomusu);

   @Query(value = "select u.idusuario,u.nomusu, u.estado as estado from usuarios u join personal p on u.personal_idpersonal = p.idpersonal where p.idcargo_cargos = ?1", nativeQuery = true)
   List<UsuarioI> findByCargoUsuario(Long idcargo);

   @Query(value = """
           SELECT
             u.idusuario,
             u.nomusu,
             u.estado AS estado,
             c.descripcion as cargo
           FROM usuarios u
           JOIN personal p ON u.personal_idpersonal = p.idpersonal
           JOIN cargos c ON p.idcargo_cargos = c.idcargo
           WHERE p.idcargo_cargos IN (:idsCargo)
         """, nativeQuery = true)
   List<UsuarioI> findByCargoIn(@Param("idsCargo") List<Long> idsCargo);

}
