import java.math.BigDecimal;
import java.util.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.epmapat.erp_epmapat.nc.*;
import org.springframework.mock.web.MockHttpServletRequest;

/** Local-only compatibility check. All fixture writes are rolled back. No application boot or network services. */
class VerifyNcUatLocal {
 static long number(Map<String,Object> row,String key){return ((Number)row.get(key)).longValue();}
 static void check(boolean ok,String message){if(!ok)throw new IllegalStateException(message);}
 public static void main(String[] args) throws Exception {
  String database=System.getenv("NC_UAT_DATABASE");
  check(database!=null&&database.matches("[A-Za-z0-9_]+"),"Nombre de base local invalido");
  var ds=new DriverManagerDataSource("jdbc:postgresql://127.0.0.1:5432/"+database,System.getenv("NC_UAT_USER"),System.getenv("NC_UAT_PASSWORD"));
  var db=new JdbcTemplate(ds);var tx=new TransactionTemplate(new DataSourceTransactionManager(ds));
  tx.setTimeout(90);
  var target=db.queryForMap("SELECT current_database() nombre,host(inet_server_addr()) host,inet_server_port() puerto");
  check(database.equals(target.get("nombre"))&&"127.0.0.1".equals(target.get("host"))&&number(target,"puerto")==5432,"Se requiere instancia local 127.0.0.1:5432");
  var schema=new NcSchemaState(db);check(schema.migrated(),"Primero aplicar la migracion en la copia local autorizada");
  String counts="SELECT (SELECT count(*) FROM ntacredito) notas,(SELECT count(*) FROM valoresnc) aplicaciones,(SELECT count(*) FROM facxnc) vinculos,(SELECT count(*) FROM nc_endoso) endosos,(SELECT count(*) FROM nc_endoso_permiso) permisos";
  var before=db.queryForMap(counts);
  var service=new NcEndosoService(db,new ObjectMapper(),true,new BigDecimal("1000000.00"),false);
  var credit=new NcAplicacionCreditoServicio(db,schema);
  Long[] invoice={null};
  tx.execute(status->{
   status.setRollbackOnly();
   db.execute("SET LOCAL lock_timeout='5s'; SET LOCAL statement_timeout='60s'");
   var users=db.queryForList("SELECT idusuario FROM usuarios WHERE estado=true ORDER BY idusuario LIMIT 4",Long.class);
   check(users.size()==4,"Se requieren cuatro usuarios activos en la copia");
   long owner=db.queryForObject("SELECT a.idresponsable FROM abonados a WHERE a.estado=1 AND NOT EXISTS(SELECT 1 FROM ntacredito n WHERE n.idabonado_abonados=a.idabonado) GROUP BY a.idresponsable HAVING count(*)>=4 ORDER BY count(*) DESC,a.idresponsable LIMIT 1",Long.class);
   var accounts=db.queryForList("SELECT a.idabonado FROM abonados a WHERE a.estado=1 AND a.idresponsable=? AND NOT EXISTS(SELECT 1 FROM ntacredito n WHERE n.idabonado_abonados=a.idabonado) ORDER BY a.idabonado LIMIT 4",Long.class,owner);
   check(accounts.size()==4,"Se requieren cuatro cuentas del mismo titular sin notas previas");
   String[] rights={"SOLICITAR","AUTORIZAR","EJECUTAR","REVERTIR"};
   for(int i=0;i<4;i++)db.update("INSERT INTO nc_endoso_permiso(usuario_id,permiso) VALUES(?,?) ON CONFLICT DO NOTHING",users.get(i),rights[i]);
   long note=db.queryForObject("INSERT INTO ntacredito(estado,observacion,valor,devengado,idcliente_clientes,usucrea,feccrea,idabonado_abonados,refdocumento) VALUES(1,'UAT LOCAL - ROLLBACK',500,0,?,?,current_date,?,'UAT-LOCAL') RETURNING idntacredito",Long.class,owner,users.get(0),accounts.get(0));
   var request=new NcEndosoService.Solicitud(note,"UAT-LOCAL-20261020","Validacion temporal sin persistencia","Reparto temporal para prueba local de compatibilidad",UUID.randomUUID(),null,List.of(new NcEndosoService.Destino(accounts.get(1),new BigDecimal("200.00")),new NcEndosoService.Destino(accounts.get(2),new BigDecimal("150.00")),new NcEndosoService.Destino(accounts.get(3),new BigDecimal("150.00"))));
   MockHttpServletRequest actor=new MockHttpServletRequest();actor.setAttribute("jwtUserId",users.get(0));
   var draft=service.crear(request,service.actor(actor,"SOLICITAR"));long id=number(draft,"id");
   var pending=service.transicion(id,"solicitar",action(draft),users.get(0));actor.setAttribute("jwtUserId",users.get(1));
   var approved=service.transicion(id,"autorizar",action(pending),service.actor(actor,"AUTORIZAR"));actor.setAttribute("jwtUserId",users.get(2));
   var operation=action(approved);var done=service.ejecutar(id,operation,service.actor(actor,"EJECUTAR"));service.ejecutar(id,operation,users.get(2));
   check(db.queryForObject("SELECT count(*) FROM nc_endoso_detalle WHERE endoso_id=? AND nota_derivada_id IS NOT NULL",Integer.class,id)==3,"Debe haber exactamente tres creditos derivados");
   long modulo=db.queryForObject("SELECT idmodulo FROM modulos ORDER BY idmodulo LIMIT 1",Long.class);
   invoice[0]=db.queryForObject("INSERT INTO facturas(idmodulo,idcliente,totaltarifa,usucrea,feccrea,valorbase,idabonado,pagado,estado) VALUES(?,?,25,?,current_date,25,?,0,1) RETURNING idfactura",Long.class,modulo,owner,users.get(2),accounts.get(1));
   credit.aplicar(invoice[0],accounts.get(1),new BigDecimal("25.00"));
   long derived=db.queryForObject("SELECT nota_derivada_id FROM nc_endoso_detalle WHERE endoso_id=? AND cuenta_destino=?",Long.class,id,accounts.get(1));
   check(((BigDecimal)service.disponibilidad(derived).get("disponible")).compareTo(new BigDecimal("175.00"))==0,"Saldo recibido incorrecto");
   check(((BigDecimal)service.disponibilidad(note).get("disponible")).signum()==0,"Saldo origen incorrecto");
   check(db.queryForObject("SELECT sum(disponible) FROM nc_saldos WHERE idntacredito=? OR nota_padre_id=?",BigDecimal.class,note,note).compareTo(new BigDecimal("475.00"))==0,"No conserva el saldo con consumo de 25");
   check(db.queryForObject("SELECT count(*) FROM facxnc WHERE idfactura_facturas=?",Integer.class,invoice[0])==1,"Falta vinculo de factura");
   boolean rejected=false;try{service.revertir(id,action(done),users.get(3));}catch(org.springframework.web.server.ResponseStatusException ex){rejected=ex.getRawStatusCode()==409;}
   check(rejected,"Debe rechazar reversion de un credito utilizado");
   System.out.println("APROBADO: reparto 500=200+150+150, reintento sin duplicacion, aplicacion 25, saldo destino 175, saldo conjunto 475 y reversion con uso rechazada.");
   return null;
  });
  check(before.equals(db.queryForMap(counts)),"Los conteos no se restauraron tras rollback");
  check(invoice[0]!=null&&db.queryForObject("SELECT count(*) FROM facturas WHERE idfactura=?",Integer.class,invoice[0])==0,"La factura ficticia debe quedar revertida");
  System.out.println("ROLLBACK verificado: sin notas, endosos, permisos ni aplicaciones ficticias persistidos. Las secuencias pueden avanzar.");
 }
 static NcEndosoService.Accion action(Map<String,Object> e){return new NcEndosoService.Accion(number(e,"version"),"Validacion administrativa temporal de UAT local",UUID.randomUUID());}
}
