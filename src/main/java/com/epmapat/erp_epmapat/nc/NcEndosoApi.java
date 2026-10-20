package com.epmapat.erp_epmapat.nc;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import javax.sql.DataSource;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.*;
import java.io.ByteArrayOutputStream;

@RestController
@RequestMapping("/api/nc-endosos")
public class NcEndosoApi {
 private final NcEndosoService service; private final DataSource dataSource;
 public NcEndosoApi(NcEndosoService service,DataSource dataSource){this.service=service;this.dataSource=dataSource;}
 @GetMapping("/permisos") public List<String> permisos(HttpServletRequest r){return service.permisos(r);}
 @GetMapping("/notas/{id}/disponibilidad") public Map<String,Object> saldo(@PathVariable long id,HttpServletRequest r){service.actor(r,"CONSULTAR");return service.disponibilidad(id);}
 @GetMapping public List<Map<String,Object>> lista(@RequestParam(required=false) String estado,@RequestParam(required=false) Long cuenta,@RequestParam(required=false) String desde,@RequestParam(required=false) String hasta,HttpServletRequest r){service.actor(r,"CONSULTAR");return service.listar(estado,cuenta,desde,hasta);}
 @GetMapping("/consulta") public Map<String,Object> consulta(@RequestParam(required=false) String estado,@RequestParam(required=false) Long cuenta,@RequestParam(required=false) String desde,@RequestParam(required=false) String hasta,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size,HttpServletRequest r){service.actor(r,"CONSULTAR");return service.consultar(estado,cuenta,desde,hasta,page,size);}
 @GetMapping("/{id}") public Map<String,Object> uno(@PathVariable long id,HttpServletRequest r){service.actor(r,"CONSULTAR");return service.obtener(id);}
 @GetMapping("/{id}/historial") public List<Map<String,Object>> historial(@PathVariable long id,HttpServletRequest r){service.actor(r,"CONSULTAR");return service.historial(id);}
 @PostMapping public Map<String,Object> crear(@RequestBody NcEndosoService.Solicitud s,HttpServletRequest r){return service.crear(s,service.actor(r,"SOLICITAR"));}
 @PutMapping("/{id}") public Map<String,Object> editar(@PathVariable long id,@RequestBody NcEndosoService.Solicitud s,HttpServletRequest r){return service.editar(id,s,service.actor(r,"SOLICITAR"));}
 @PostMapping("/{id}/{accion:solicitar|autorizar|rechazar|anular|ejecutar|revertir}")
 public Map<String,Object> accion(@PathVariable long id,@PathVariable String accion,@RequestBody NcEndosoService.Accion a,HttpServletRequest r){
  String permiso=switch(accion){case "autorizar","rechazar"->"AUTORIZAR";case "ejecutar"->"EJECUTAR";case "revertir"->"REVERTIR";default->"SOLICITAR";};
  long actor=service.actor(r,permiso);
  try { return switch(accion){case "ejecutar"->service.ejecutar(id,a,actor);case "revertir"->service.revertir(id,a,actor);default->service.transicion(id,accion,a,actor);}; } catch(org.springframework.web.server.ResponseStatusException ex){if("revertir".equals(accion))service.rechazoReversion(id,actor,ex.getReason());throw ex;}
 }
 @PostMapping(value="/{id}/respaldo",consumes="multipart/form-data")
 public void subir(@PathVariable long id,@RequestParam("archivo") org.springframework.web.multipart.MultipartFile archivo,HttpServletRequest r) throws Exception {long actor=service.actor(r,"SOLICITAR");service.respaldo(id,archivo.getOriginalFilename(),archivo.getBytes(),actor);}
 @GetMapping("/{id}/respaldo") public ResponseEntity<byte[]> respaldo(@PathVariable long id,HttpServletRequest r){service.actor(r,"CONSULTAR");var file=service.respaldo(id);return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).header("Content-Disposition","attachment; filename=respaldo.pdf").header("X-Content-Type-Options","nosniff").body((byte[])file.get("contenido"));}
 @GetMapping({"/{id}/comprobante","/reporte"})
 public ResponseEntity<byte[]> reporte(@PathVariable(required=false) Long id,@RequestParam(defaultValue="pdf") String formato,
 @RequestParam(required=false) String estado,@RequestParam(required=false) Long cuenta,@RequestParam(required=false) String desde,@RequestParam(required=false) String hasta,HttpServletRequest r) throws Exception {
  service.actor(r,"REPORTES");
  if(!List.of("pdf","xlsx").contains(formato))throw new org.springframework.web.server.ResponseStatusException(HttpStatus.BAD_REQUEST,"Formato no soportado");
  Map<String,Object> params=new HashMap<>();params.put("id",id);params.put("estado",estado==null||estado.isBlank()?null:estado);params.put("cuenta",cuenta);
  params.put("desde",desde==null||desde.isBlank()?null:java.sql.Date.valueOf(desde));params.put("hasta",hasta==null||hasta.isBlank()?null:java.sql.Date.valueOf(hasta));
  try(var in=getClass().getResourceAsStream("/nc-reports/EndosoNotaCredito.jrxml");var db=dataSource.getConnection()){
   db.setReadOnly(true);var print=JasperFillManager.fillReport(JasperCompileManager.compileReport(in),params,db);
   byte[] output;
   if("pdf".equals(formato))output=JasperExportManager.exportReportToPdf(print);
   else {var out=new ByteArrayOutputStream();var exporter=new JRXlsxExporter();exporter.setExporterInput(new SimpleExporterInput(print));exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));var config=new SimpleXlsxReportConfiguration();config.setDetectCellType(true);config.setWhitePageBackground(false);exporter.setConfiguration(config);exporter.exportReport();output=out.toByteArray();}
   return ResponseEntity.ok().header("Content-Disposition","attachment; filename=endosos"+(id==null?"":"-"+id)+"."+formato).contentType(MediaType.parseMediaType("pdf".equals(formato)?"application/pdf":"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")).body(output);
  }
 }
}
