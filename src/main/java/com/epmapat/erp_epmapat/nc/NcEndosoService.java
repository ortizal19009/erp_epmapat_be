package com.epmapat.erp_epmapat.nc;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class NcEndosoService {
 public record Destino(Long cuenta, BigDecimal valor) {}
 public record Solicitud(Long notaOrigenId, String documentoRespaldo, String referencia, String motivo,
                         UUID idempotencyKey, Long version, List<Destino> destinos) {}
 public record Accion(Long version, String motivo, UUID operationKey) {}
 private final JdbcTemplate db;
 private final ObjectMapper json;
 private final boolean enabled;
 private final BigDecimal maximo;
 private final boolean entreTitulares;
 public NcEndosoService(JdbcTemplate db,ObjectMapper json,
 @Value("${erp.nc-endosos.enabled:${ERP_NC_ENDOSOS_ENABLED:true}}") boolean enabled,
 @Value("${erp.nc-endosos.maximo:1000000.00}") BigDecimal maximo,
 @Value("${erp.nc-endosos.entre-titulares:false}") boolean entreTitulares) {
  this.db=db;this.json=json;this.enabled=enabled;this.maximo=maximo;this.entreTitulares=entreTitulares;
 }
 private static final List<String> PERMISOS = List.of("CONSULTAR","SOLICITAR","AUTORIZAR","EJECUTAR","REVERTIR","REPORTES");
 public long actor(HttpServletRequest request,String permiso) {
  List<String> permisos = permisos(request);
  if(!permisos.contains(permiso)) throw error(HttpStatus.FORBIDDEN,"Sin permiso NC_ENDOSO_"+permiso);
  return Long.parseLong(request.getAttribute("jwtUserId").toString());
 }
 public List<String> permisos(HttpServletRequest request) {
  Object id=request.getAttribute("jwtUserId");
  if(id==null)throw error(HttpStatus.UNAUTHORIZED,"Sesion WEB requerida");
  if(!enabled)throw error(HttpStatus.SERVICE_UNAVAILABLE,"Endosos deshabilitados por configuracion (ERP_NC_ENDOSOS_ENABLED)");
  long actor=Long.parseLong(id.toString());
  Integer activos=db.queryForObject("SELECT count(*) FROM usuarios WHERE idusuario=? AND estado=true",Integer.class,actor);
  if(activos==null || activos==0)throw error(HttpStatus.FORBIDDEN,"Usuario inactivo o inexistente");
  if(!new NcSchemaState(db).migrated())throw error(HttpStatus.SERVICE_UNAVAILABLE,"Falta aplicar la migracion de Endosos: 2026-10-19_nc_endosos.sql");
  // The system administrator has every module permission. Business rules still apply.
  if(actor==1L)return PERMISOS;
  return db.queryForList("SELECT p.permiso FROM nc_endoso_permiso p JOIN usuarios u ON u.idusuario=p.usuario_id WHERE p.usuario_id=? AND u.estado=true ORDER BY permiso",String.class,actor);
 }
 public Map<String,Object> disponibilidad(long nota) {
  return one("SELECT s.idntacredito, s.idabonado_abonados cuenta, s.valor, coalesce(s.devengado,0) aplicado, s.endosado, s.disponible, s.tipo_origen, s.estado, s.fechaeliminacion, n.feccrea, n.nrofactura, n.refdocumento, n.observacion, c.nombre titular, c.cedula identificacion FROM nc_saldos s JOIN ntacredito n ON n.idntacredito=s.idntacredito LEFT JOIN clientes c ON c.idcliente=n.idcliente_clientes WHERE s.idntacredito=?",nota);
 }
 public List<Map<String,Object>> listar(String estado,Long cuenta,String desde,String hasta) {
  List<Object> args=new ArrayList<>();String sql="SELECT e.* FROM nc_endoso e WHERE 1=1";
  if(estado!=null&&!estado.isBlank()){sql+=" AND e.estado=?";args.add(estado);}
  if(cuenta!=null){sql+=" AND (e.cuenta_origen=? OR EXISTS(SELECT 1 FROM nc_endoso_detalle d WHERE d.endoso_id=e.id AND d.cuenta_destino=?))";args.add(cuenta);args.add(cuenta);}
  if(desde!=null&&!desde.isBlank()){sql+=" AND e.creado_en>=?::date";args.add(java.time.LocalDate.parse(desde).toString());}
  if(hasta!=null&&!hasta.isBlank()){sql+=" AND e.creado_en<?::date+interval '1 day'";args.add(java.time.LocalDate.parse(hasta).toString());}
  return db.queryForList(sql+" ORDER BY e.id DESC LIMIT 500",args.toArray());
 }
 public Map<String,Object> consultar(String estado,Long cuenta,String desde,String hasta,int pagina,int tamanio) {
  if(pagina<0||tamanio<1||tamanio>100)throw error(HttpStatus.BAD_REQUEST,"Paginacion invalida");
  if(desde!=null&&!desde.isBlank()&&hasta!=null&&!hasta.isBlank()&&java.time.LocalDate.parse(desde).isAfter(java.time.LocalDate.parse(hasta)))throw error(HttpStatus.BAD_REQUEST,"Rango de fechas invalido");
  var args=new ArrayList<Object>();String where=" WHERE 1=1";
  if(estado!=null&&!estado.isBlank()){where+=" AND e.estado=?";args.add(estado);}
  if(cuenta!=null){where+=" AND (e.cuenta_origen=? OR EXISTS(SELECT 1 FROM nc_endoso_detalle d WHERE d.endoso_id=e.id AND d.cuenta_destino=?))";args.add(cuenta);args.add(cuenta);}
  if(desde!=null&&!desde.isBlank()){where+=" AND e.creado_en>=?::date";args.add(java.time.LocalDate.parse(desde).toString());}
  if(hasta!=null&&!hasta.isBlank()){where+=" AND e.creado_en<?::date+interval '1 day'";args.add(java.time.LocalDate.parse(hasta).toString());}
  var resumen=db.queryForMap("SELECT count(*) total,coalesce(sum(e.monto_total),0) monto_total,coalesce(sum(e.monto_total) FILTER(WHERE e.estado='EJECUTADO'),0) monto_ejecutado FROM nc_endoso e"+where,args.toArray());
  var paginados=new ArrayList<Object>(args);paginados.add(tamanio);paginados.add((long)pagina*tamanio);
  var filas=db.queryForList("SELECT e.*,c.nombre titular_origen,u.nomusu solicitante,(SELECT string_agg(d.cuenta_destino::text,', ' ORDER BY d.cuenta_destino) FROM nc_endoso_detalle d WHERE d.endoso_id=e.id) cuentas_destino FROM nc_endoso e LEFT JOIN abonados a ON a.idabonado=e.cuenta_origen LEFT JOIN clientes c ON c.idcliente=a.idresponsable LEFT JOIN usuarios u ON u.idusuario=e.solicitante_id"+where+" ORDER BY e.id DESC LIMIT ? OFFSET ?",paginados.toArray());
  return Map.of("content",filas,"resumen",resumen,"page",pagina,"size",tamanio);
 }
 public Map<String,Object> obtener(long id) {
  var e=one("SELECT * FROM nc_endoso WHERE id=?",id);
  e.put("destinos",db.queryForList("SELECT d.*, c.nombre titular FROM nc_endoso_detalle d JOIN abonados a ON a.idabonado=d.cuenta_destino JOIN clientes c ON c.idcliente=a.idresponsable WHERE d.endoso_id=? ORDER BY d.cuenta_destino",id));return e;
 }
 public List<Map<String,Object>> historial(long id){return db.queryForList("SELECT * FROM nc_endoso_auditoria WHERE endoso_id=? ORDER BY id",id);}
 @Transactional
 public Map<String,Object> crear(Solicitud s,long actor) {
  validar(s);
  // Transaction-scoped advisory lock serializes retries before there is an endoso row to lock.
  db.queryForList("SELECT pg_advisory_xact_lock(hashtextextended(?::text,0))",s.idempotencyKey().toString());
  var prev=db.queryForList("SELECT * FROM nc_endoso WHERE idempotency_key=?",s.idempotencyKey());
  if(!prev.isEmpty()) {
   var e=prev.get(0);if(num(e,"solicitante_id")!=actor||!fingerprint(s).equals(fingerprint(solicitud(e))))throw error(HttpStatus.CONFLICT,"Clave idempotente ya usada con otro contenido");
   return obtener(num(e,"id"));
  }
  var n=nota(s.notaOrigenId());validarNota(n);
  validarDestinos(s,num(n,"idabonado_abonados"));
  long id=db.queryForObject("INSERT INTO nc_endoso(nota_origen_id,cuenta_origen,monto_total,documento_respaldo,referencia,motivo,solicitante_id,idempotency_key) VALUES(?,?,?,?,?,?,?,?) RETURNING id",Long.class,s.notaOrigenId(),num(n,"idabonado_abonados"),total(s),s.documentoRespaldo().trim(),s.referencia().trim(),s.motivo().trim(),actor,s.idempotencyKey());
  detalles(id,s);audit(id,"CREAR",null,"BORRADOR",actor,s.motivo());return obtener(id);
 }
 @Transactional
 public Map<String,Object> editar(long id,Solicitud s,long actor) {
  validar(s);var e=lock(id);version(e,s.version());estado(e,"BORRADOR");
  if(num(e,"solicitante_id")!=actor)throw error(HttpStatus.FORBIDDEN,"Solo el solicitante puede editar");
  if(num(e,"nota_origen_id")!=s.notaOrigenId()||!e.get("idempotency_key").equals(s.idempotencyKey()))throw error(HttpStatus.CONFLICT,"No se puede cambiar nota ni clave de solicitud");
  validarDestinos(s,num(e,"cuenta_origen"));
  db.update("DELETE FROM nc_endoso_detalle WHERE endoso_id=?",id);detalles(id,s);
  db.update("UPDATE nc_endoso SET monto_total=?, documento_respaldo=?, referencia=?,motivo=?,huella=null,version=version+1,actualizado_en=now() WHERE id=?",total(s),s.documentoRespaldo().trim(),s.referencia().trim(),s.motivo().trim(),id);
  audit(id,"EDITAR","BORRADOR","BORRADOR",actor,s.motivo());return obtener(id);
 }
 @Transactional
 public Map<String,Object> transicion(long id,String accion,Accion a,long actor) {
  texto(a.motivo(),10,2000);var e=lock(id);version(e,a.version());String anterior=e.get("estado").toString();String nuevo;
  switch(accion) {
   case "solicitar": estado(e,"BORRADOR");if(num(e,"solicitante_id")!=actor)throw error(HttpStatus.FORBIDDEN,"Solo solicitante");nuevo="PENDIENTE";db.update("UPDATE nc_endoso SET fecha_solicitud=now() WHERE id=?",id);break;
   case "autorizar": estado(e,"PENDIENTE");segregar(e,actor);nuevo="AUTORIZADO";db.update("UPDATE nc_endoso SET autorizador_id=?,fecha_autorizacion=now(),motivo_autorizacion=?,huella=? WHERE id=?",actor,a.motivo(),huellaGuardada(id),id);break;
   case "rechazar": estado(e,"PENDIENTE");segregar(e,actor);nuevo="RECHAZADO";break;
   case "anular": if(!List.of("BORRADOR","PENDIENTE","AUTORIZADO").contains(anterior))throw error(HttpStatus.CONFLICT,"No se puede anular una ejecucion");if(num(e,"solicitante_id")!=actor)throw error(HttpStatus.FORBIDDEN,"Solo solicitante");nuevo="ANULADO";break;
   default:throw error(HttpStatus.BAD_REQUEST,"Accion desconocida");
  }
  db.update("UPDATE nc_endoso SET estado=?,version=version+1,actualizado_en=now() WHERE id=?",nuevo,id);audit(id,accion.toUpperCase(Locale.ROOT),anterior,nuevo,actor,a.motivo());return obtener(id);
 }
 @Transactional
 public Map<String,Object> ejecutar(long id,Accion a,long actor) {
  if(a.operationKey()==null)throw error(HttpStatus.BAD_REQUEST,"operationKey requerida");
  var e=lock(id);
  if("EJECUTADO".equals(e.get("estado"))){if(a.operationKey().equals(e.get("operacion_key")))return obtener(id);throw error(HttpStatus.CONFLICT,"Endoso ejecutado con otra clave");}
  version(e,a.version());estado(e,"AUTORIZADO");
  if(!huellaGuardada(id).equals(e.get("huella")))throw error(HttpStatus.CONFLICT,"La distribucion aprobada cambio");
  var n=nota(num(e,"nota_origen_id"));validarNota(n);var s=solicitud(e);if(num(n,"idabonado_abonados")!=num(e,"cuenta_origen"))throw error(HttpStatus.CONFLICT,"Cambio de cuenta origen");validar(s);if(total(s).compareTo(decimal(e,"monto_total"))!=0)throw error(HttpStatus.CONFLICT,"Total de cabecera no coincide con destinos");validarDestinos(s,num(e,"cuenta_origen"));
  BigDecimal disponible=(BigDecimal)disponibilidad(num(e,"nota_origen_id")).get("disponible");
  if(disponible.compareTo(total(s))<0)throw error(HttpStatus.CONFLICT,"Saldo insuficiente al ejecutar");
  for(var d:db.queryForList("SELECT * FROM nc_endoso_detalle WHERE endoso_id=? ORDER BY cuenta_destino",id)) {
   long cuenta=num(d,"cuenta_destino");var abonado=one("SELECT * FROM abonados WHERE idabonado=?",cuenta);
   long derivada=db.queryForObject("INSERT INTO ntacredito(estado,observacion,valor,devengado,nrofactura,idcliente_clientes,usucrea,feccrea,idabonado_abonados,iddocumento_documentos,refdocumento,tipo_origen,nota_padre_id,endoso_detalle_id) VALUES(1,?,?,0,null,?,?,current_date,?,?,?,'ENDOSO',?,?) RETURNING idntacredito",Long.class,"Saldo recibido por endoso #"+id,d.get("monto"),num(abonado,"idresponsable"),actor,cuenta,n.get("iddocumento_documentos"),"ENDOSO #"+id,num(n,"idntacredito"),num(d,"id"));
   db.update("UPDATE nc_endoso_detalle SET nota_derivada_id=? WHERE id=?",derivada,num(d,"id"));
  }
  db.update("UPDATE nc_endoso SET estado='EJECUTADO',ejecutor_id=?,fecha_ejecucion=now(),saldo_antes=?,saldo_despues=?,operacion_key=?,version=version+1,actualizado_en=now() WHERE id=?",actor,disponible,disponible.subtract(total(s)),a.operationKey(),id);
  audit(id,"EJECUTAR","AUTORIZADO","EJECUTADO",actor,"Transferencia de saldo; no pago de facturas");return obtener(id);
 }
 @Transactional
 public Map<String,Object> revertir(long id,Accion a,long actor) {
  texto(a.motivo(),10,2000);var e=lock(id);
  if("REVERTIDO".equals(e.get("estado")))return obtener(id);
  version(e,a.version());estado(e,"EJECUTADO");
  if(actor==num(e,"solicitante_id")||actor==num(e,"ejecutor_id"))throw error(HttpStatus.FORBIDDEN,"Reversion requiere actor independiente del solicitante y ejecutor");
  // Derived credits cannot be endorsed in v1. Lock all notes in ascending ID order.
  var notas=db.queryForList("SELECT n.* FROM ntacredito n WHERE n.idntacredito=? OR n.endoso_detalle_id IN(SELECT id FROM nc_endoso_detalle WHERE endoso_id=?) ORDER BY n.idntacredito FOR UPDATE",num(e,"nota_origen_id"),id);
  for(var n:notas)if(num(n,"idntacredito")!=num(e,"nota_origen_id")) {
   long nid=num(n,"idntacredito");
   int movimientos=db.queryForObject("SELECT count(*) FROM valoresnc WHERE idntacredito_ntacredito=?",Integer.class,nid);
   int posteriores=db.queryForObject("SELECT count(*) FROM nc_endoso WHERE nota_origen_id=? AND estado='EJECUTADO'",Integer.class,nid);
   if(decimal(n,"devengado").signum()!=0||movimientos>0||posteriores>0||num(n,"estado")!=1||n.get("fechaeliminacion")!=null)throw error(HttpStatus.CONFLICT,"Credito derivado utilizado o alterado: requiere compensacion administrativa");
  }
  db.update("UPDATE ntacredito SET estado=0,fechaeliminacion=current_date,usuarioeliminacion=?,razoneliminacion=? WHERE endoso_detalle_id IN(SELECT id FROM nc_endoso_detalle WHERE endoso_id=?)",actor,a.motivo().substring(0,Math.min(254,a.motivo().length())),id);
  db.update("UPDATE nc_endoso SET estado='REVERTIDO',fecha_reversion=now(),reversion_autorizador_id=?,motivo_reversion=?,version=version+1,actualizado_en=now() WHERE id=?",actor,a.motivo(),id);
  audit(id,"REVERTIR","EJECUTADO","REVERTIDO",actor,a.motivo());return obtener(id);
 }
 @Transactional
 public void respaldo(long id,String nombre,byte[] contenido,long actor) {
  var e=lock(id);estado(e,"BORRADOR");if(num(e,"solicitante_id")!=actor)throw error(HttpStatus.FORBIDDEN,"Solo solicitante");
  if(contenido==null||contenido.length<5||contenido.length>5242880||!new String(contenido,0,5,StandardCharsets.US_ASCII).equals("%PDF-"))throw error(HttpStatus.BAD_REQUEST,"Respaldo PDF requerido, maximo 5 MB");
  String limpio=nombre==null?"respaldo.pdf":nombre.replaceAll("[^a-zA-Z0-9._-]","_");if(limpio.length()>120)limpio=limpio.substring(limpio.length()-120);
  try{String hash=HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(contenido));db.update("INSERT INTO nc_endoso_respaldo(endoso_id,nombre,contenido,sha256,actor_id) VALUES(?,?,?,?,?) ON CONFLICT(endoso_id) DO UPDATE SET nombre=excluded.nombre,contenido=excluded.contenido,sha256=excluded.sha256,actor_id=excluded.actor_id,fecha=now()",id,limpio,contenido,hash,actor);db.update("UPDATE nc_endoso SET version=version+1,actualizado_en=now() WHERE id=?",id);audit(id,"RESPALDO","BORRADOR","BORRADOR",actor,"Respaldo PDF actualizado");}catch(java.security.NoSuchAlgorithmException ex){throw new IllegalStateException(ex);}
 }
 public Map<String,Object> respaldo(long id){return one("SELECT nombre,contenido FROM nc_endoso_respaldo WHERE endoso_id=?",id);}
 @Transactional(propagation=org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
 public void rechazoReversion(long id,long actor,String motivo){db.update("INSERT INTO nc_endoso_auditoria(endoso_id,accion,estado_anterior,estado_nuevo,actor_id,motivo) SELECT id,'REVERSION_RECHAZADA',estado,estado,?,? FROM nc_endoso WHERE id=?",actor,motivo,id);}
 private Solicitud solicitud(Map<String,Object> e){long id=num(e,"id");var ds=db.queryForList("SELECT cuenta_destino,monto FROM nc_endoso_detalle WHERE endoso_id=? ORDER BY cuenta_destino",id).stream().map(d->new Destino(num(d,"cuenta_destino"),(BigDecimal)d.get("monto"))).toList();return new Solicitud(num(e,"nota_origen_id"),e.get("documento_respaldo").toString(),e.get("referencia").toString(),e.get("motivo").toString(),(UUID)e.get("idempotency_key"),num(e,"version"),ds);}
 private String huellaGuardada(long id){
  String base=fingerprint(solicitud(one("SELECT * FROM nc_endoso WHERE id=?",id)));
  var files=db.queryForList("SELECT sha256 FROM nc_endoso_respaldo WHERE endoso_id=?",String.class,id);
  try{return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest((base+String.join("",files)).getBytes(StandardCharsets.UTF_8)));}catch(Exception ex){throw new IllegalStateException(ex);}
 }
 private String fingerprint(Solicitud s){try{var ds=s.destinos().stream().sorted(Comparator.comparing(Destino::cuenta)).map(d->List.of(d.cuenta().toString(),d.valor().setScale(2).toPlainString())).toList();return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(json.writeValueAsBytes(List.of(s.notaOrigenId(),s.documentoRespaldo().trim(),s.referencia().trim(),s.motivo().trim(),ds))));}catch(Exception ex){throw error(HttpStatus.BAD_REQUEST,"Distribucion invalida");}}
 private void validar(Solicitud s){if(s==null||s.notaOrigenId()==null||s.idempotencyKey()==null||s.destinos()==null||s.destinos().isEmpty()||s.destinos().size()>20)throw error(HttpStatus.BAD_REQUEST,"Nota, clave y entre 1 y 20 destinos requeridos");texto(s.documentoRespaldo(),1,120);texto(s.referencia(),1,80);texto(s.motivo(),10,2000);Set<Long> cuentas=new HashSet<>();for(var d:s.destinos()){if(d==null||d.cuenta()==null||!cuentas.add(d.cuenta())||d.valor()==null||d.valor().scale()>2||d.valor().signum()<=0)throw error(HttpStatus.BAD_REQUEST,"Destinos duplicados o importe invalido");}if(total(s).compareTo(maximo)>0)throw error(HttpStatus.BAD_REQUEST,"Supera maximo permitido");}
 private void validarDestinos(Solicitud s,long origen){for(var d:s.destinos()){if(d.cuenta()==origen)throw error(HttpStatus.BAD_REQUEST,"Destino igual a origen");var a=one("SELECT estado,idresponsable FROM abonados WHERE idabonado=? FOR SHARE",d.cuenta());if(!entreTitulares){var o=one("SELECT idresponsable FROM abonados WHERE idabonado=?",origen);if(num(o,"idresponsable")!=num(a,"idresponsable"))throw error(HttpStatus.FORBIDDEN,"Endosos entre titulares no habilitados por politica institucional");}if(num(a,"estado")!=1)throw error(HttpStatus.BAD_REQUEST,"Cuenta destino no activa");}}
 private void validarNota(Map<String,Object> n){var cuenta=one("SELECT estado FROM abonados WHERE idabonado=? FOR SHARE",num(n,"idabonado_abonados"));if(num(cuenta,"estado")!=1)throw error(HttpStatus.CONFLICT,"Cuenta origen inactiva");if(num(n,"estado")!=1||n.get("fechaeliminacion")!=null||"ENDOSO".equals(n.get("tipo_origen")))throw error(HttpStatus.CONFLICT,"Nota inactiva o derivada; no elegible para endoso");}
 private BigDecimal total(Solicitud s){return s.destinos().stream().map(Destino::valor).reduce(BigDecimal.ZERO,BigDecimal::add).setScale(2,RoundingMode.UNNECESSARY);}
 private void detalles(long id,Solicitud s){for(var d:s.destinos())db.update("INSERT INTO nc_endoso_detalle(endoso_id,cuenta_destino,monto) VALUES(?,?,?)",id,d.cuenta(),d.valor());}
 private Map<String,Object> nota(long id){return one("SELECT * FROM ntacredito WHERE idntacredito=? FOR UPDATE",id);}
 private Map<String,Object> lock(long id){return one("SELECT * FROM nc_endoso WHERE id=? FOR UPDATE",id);}
 private Map<String,Object> one(String sql,Object...args){var rows=db.queryForList(sql,args);if(rows.isEmpty())throw error(HttpStatus.NOT_FOUND,"Registro no encontrado");return rows.get(0);}
 private void version(Map<String,Object> e,Long v){if(v==null||num(e,"version")!=v)throw error(HttpStatus.CONFLICT,"Version desactualizada; recargue");}
 private void estado(Map<String,Object> e,String state){if(!state.equals(e.get("estado")))throw error(HttpStatus.CONFLICT,"Estado incompatible con la accion");}
 private void segregar(Map<String,Object> e,long actor){if(num(e,"solicitante_id")==actor)throw error(HttpStatus.FORBIDDEN,"No puede aprobar o rechazar su propia solicitud");}
 private void texto(String s,int min,int max){if(s==null||s.trim().length()<min||s.trim().length()>max)throw error(HttpStatus.BAD_REQUEST,"Texto obligatorio fuera de longitud permitida");}
 private void audit(long id,String accion,String antes,String despues,long actor,String motivo){
  try {var snapshot=solicitud(one("SELECT * FROM nc_endoso WHERE id=?",id));
   String detalle=json.writeValueAsString(Map.of("distribucionSha256",huellaGuardada(id),"solicitud",snapshot));
   db.update("INSERT INTO nc_endoso_auditoria(endoso_id,accion,estado_anterior,estado_nuevo,actor_id,motivo,detalle) VALUES(?,?,?,?,?,?,?::jsonb)",id,accion,antes,despues,actor,motivo,detalle);
  }catch(com.fasterxml.jackson.core.JsonProcessingException ex){throw new IllegalStateException("No se pudo registrar auditoria",ex);}
 }

 private long num(Map<String,Object> m,String k){return ((Number)m.get(k)).longValue();}
 private BigDecimal decimal(Map<String,Object> m,String k){return m.get(k)==null?BigDecimal.ZERO:new BigDecimal(m.get(k).toString());}
 private ResponseStatusException error(HttpStatus status,String message){return new ResponseStatusException(status,message);}
}
