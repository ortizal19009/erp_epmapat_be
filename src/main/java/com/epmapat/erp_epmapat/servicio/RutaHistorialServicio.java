package com.epmapat.erp_epmapat.servicio;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import com.epmapat.erp_epmapat.modelo.*;
@Service
public class RutaHistorialServicio {
    private final AuditoriaGenericaService auditoria;
    public RutaHistorialServicio(AuditoriaGenericaService auditoria) {this.auditoria=auditoria;}
    public record RutaSnapshot(Long id,String codigo,String descripcion) {}
    public record Cambio(Long cuenta,RutaSnapshot rutaAnterior,RutaSnapshot rutaNueva,Long secuenciaAnterior,Long secuenciaNueva,String origen) {}
    private RutaSnapshot snapshot(Rutas r) {return r==null?null:new RutaSnapshot(r.getIdruta(),r.getCodigo(),r.getDescripcion());}
    public void registrar(Abonados cuenta,Rutas destino,Long secuencia,Long usuario,String motivo,String origen) {
        Long anterior=cuenta.getIdruta_rutas()==null?null:cuenta.getIdruta_rutas().getIdruta();
        Long nueva=destino==null?null:destino.getIdruta();
        if(Objects.equals(anterior,nueva))return;
        if(usuario==null || usuario<=0)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Indique el usuario del traslado");
        if(destino==null)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Seleccione la ruta de destino");
        auditoria.saveAuditAtomic("abonados",cuenta.getIdabonado(),
          new Cambio(cuenta.getIdabonado(),snapshot(cuenta.getIdruta_rutas()),snapshot(destino),cuenta.getSecuencia(),secuencia,origen),
          usuario,motivo==null||motivo.isBlank()?"Cambio de ruta":motivo,"CAMBIO_RUTA");
    }
}
