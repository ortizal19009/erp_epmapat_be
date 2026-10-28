package com.epmapat.erp_epmapat.servicio;
import java.util.*;
import javax.persistence.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.jdbc.core.JdbcTemplate;
import com.epmapat.erp_epmapat.DTO.RutaGuardarRequest;
import com.epmapat.erp_epmapat.modelo.*;
import com.epmapat.erp_epmapat.repositorio.*;
@Service
public class RutaGestionServicio {
    private final RutasR rutas;private final AbonadosR abonados;private final RutaHistorialServicio historial;private final JdbcTemplate jdbc;
    @PersistenceContext private EntityManager em;
    public RutaGestionServicio(@Qualifier("rutasR") RutasR rutas,AbonadosR abonados,RutaHistorialServicio historial,JdbcTemplate jdbc) {
      this.rutas=rutas;this.abonados=abonados;this.historial=historial;this.jdbc=jdbc;
    }
    @Transactional
    public Rutas guardar(Long id,RutaGuardarRequest solicitud,Long usuario) {
      if(usuario==null||usuario<=0)throw error(HttpStatus.BAD_REQUEST,"Indique el usuario que realiza el cambio");
      var elegidas=solicitud.cuentas()==null?List.<RutaGuardarRequest.CuentaTraslado>of():solicitud.cuentas();
      if(elegidas.stream().map(RutaGuardarRequest.CuentaTraslado::cuenta).distinct().count()!=elegidas.size())throw error(HttpStatus.BAD_REQUEST,"Hay cuentas repetidas");
      if(!elegidas.isEmpty() && (solicitud.motivo()==null||solicitud.motivo().isBlank()))throw error(HttpStatus.BAD_REQUEST,"Indique el motivo del traslado");
      if(!elegidas.isEmpty()&&!Boolean.TRUE.equals(solicitud.estado()))throw error(HttpStatus.CONFLICT,"Active la ruta para recibir cuentas");
      jdbc.queryForObject("SELECT 1 FROM pg_advisory_xact_lock(7194361202)",Integer.class);
      Rutas ruta=id==null?new Rutas():em.find(Rutas.class,id,LockModeType.PESSIMISTIC_WRITE);
      if(ruta==null)throw error(HttpStatus.NOT_FOUND,"No existe la ruta");
      if(id!=null)em.refresh(ruta);
      String codigo=id==null?siguienteCodigo():ruta.getCodigo();
      Integer usados=jdbc.queryForObject("SELECT COUNT(*) FROM rutas WHERE lower(trim(codigo))=lower(?) AND (?::bigint IS NULL OR idruta<>?)",Integer.class,codigo,id,id);
      if(usados!=null&&usados>0)throw error(HttpStatus.CONFLICT,"Ya existe otra ruta con ese codigo");
      if(id==null){ruta.setUsucrea(usuario);ruta.setFeccrea(new Date());}
      else {ruta.setUsumodi(usuario);ruta.setFecmodi(new Date());}
      ruta.setCodigo(codigo);ruta.setDescripcion(solicitud.descripcion().trim());ruta.setOrden(id==null?siguienteOrden():solicitud.orden());ruta.setEstado(solicitud.estado());
      ruta=rutas.saveAndFlush(ruta);
      if(elegidas.isEmpty())return ruta;
      var ids=elegidas.stream().map(RutaGuardarRequest.CuentaTraslado::cuenta).sorted().toList();
      var cuentas=abonados.lockParaRetiro(ids);cuentas.forEach(em::refresh);
      if(cuentas.size()!=ids.size())throw error(HttpStatus.NOT_FOUND,"Una de las cuentas ya no existe");
      var origenes=new HashMap<Long,Long>();elegidas.forEach(c->origenes.put(c.cuenta(),c.rutaOrigen()));
      for(var cuenta:cuentas) {
        Long origen=cuenta.getIdruta_rutas()==null?null:cuenta.getIdruta_rutas().getIdruta();
        if(!Objects.equals(origen,origenes.get(cuenta.getIdabonado())))throw error(HttpStatus.CONFLICT,"La cuenta "+cuenta.getIdabonado()+" cambio de ruta. Actualice la seleccion.");
        if(Objects.equals(origen,ruta.getIdruta()))throw error(HttpStatus.CONFLICT,"La cuenta ya pertenece a esta ruta");
      }
      Long secuencia=jdbc.queryForObject("SELECT COALESCE(MAX(secuencia),0) FROM abonados WHERE idruta_rutas=?",Long.class,ruta.getIdruta());
      for(var cuenta:cuentas) {
        secuencia++;
        historial.registrar(cuenta,ruta,secuencia,usuario,solicitud.motivo(),"RUTAS");
        cuenta.setIdruta_rutas(ruta);cuenta.setSecuencia(secuencia);cuenta.setUsumodi(usuario);
        cuenta.setFecmodi(java.time.LocalDate.now(java.time.ZoneId.of("America/Guayaquil")));
        abonados.save(cuenta);
      }
      return ruta;
    }
    public Long siguienteOrden() {
      Long mayor=jdbc.queryForObject("SELECT COALESCE(MAX(orden),0) FROM rutas",Long.class);
      return Math.addExact(mayor==null?0L:mayor,1L);
    }
    public String siguienteCodigo() {
      return calcularSiguienteCodigo(jdbc.queryForList("SELECT codigo FROM rutas",String.class));
    }
    static String calcularSiguienteCodigo(List<String> codigos) {
      long mayor=0;
      for(String codigo:codigos) {
        if(codigo!=null && codigo.trim().matches("[0-9]{1,8}")) mayor=Math.max(mayor,Long.parseLong(codigo.trim()));
      }
      if(mayor>=99999999L)throw new ResponseStatusException(HttpStatus.CONFLICT,"Se agoto la numeracion de ocho digitos de las rutas");
      return String.format(java.util.Locale.ROOT,"%08d",mayor+1);
    }
    private ResponseStatusException error(HttpStatus estado,String mensaje){return new ResponseStatusException(estado,mensaje);}
}
