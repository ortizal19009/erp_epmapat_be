package com.epmapat.erp_epmapat.emails.component;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import com.epmapat.erp_epmapat.seguridad.JwtService;
import com.epmapat.erp_epmapat.repositorio.administracion.UsuariosR;
import com.epmapat.erp_epmapat.repositorio.administracion.VentanasR;
@Service
public class EmailAdminAccessService {
    private final JwtService jwt;private final UsuariosR usuarios;private final VentanasR ventanas;
    public EmailAdminAccessService(JwtService j,UsuariosR u,VentanasR v){jwt=j;usuarios=u;ventanas=v;}
    public Long require(HttpServletRequest req,boolean write){
        String header=req.getHeader("Authorization");Long id;
        try{
            if(header==null || !header.startsWith("Bearer "))throw new IllegalArgumentException();
            id=Long.valueOf(String.valueOf(jwt.validateWebToken(header.substring(7).trim()).get("sub")));
        }catch(IllegalArgumentException invalid){throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Sesion WEB requerida");}
        if(!usuarios.findById(id).map(u->Boolean.TRUE.equals(u.getEstado())).orElse(false))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Usuario inactivo");
        if(id==1L)return id;
        long minimum=write?2:1;
        boolean allowed=ventanas.findByIdusuarioOrderByNombreAsc(id).stream().anyMatch(v->
                v.getNombre()!=null && "admin-correos".equalsIgnoreCase(v.getNombre().trim().replaceFirst("^/",""))
                && v.getPermissions()!=null && v.getPermissions()>=minimum);
        if(!allowed)throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Permiso de administracion de correos insuficiente");
        return id;
    }
}
