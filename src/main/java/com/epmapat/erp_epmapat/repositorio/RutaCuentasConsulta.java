package com.epmapat.erp_epmapat.repositorio;
import java.util.*;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.data.domain.*;
@Repository
public class RutaCuentasConsulta {
    private final NamedParameterJdbcTemplate jdbc;
    public RutaCuentasConsulta(NamedParameterJdbcTemplate jdbc){this.jdbc=jdbc;}
    public Page<Map<String,Object>> buscar(Long origen,Long destino,String filtro,int pagina,int tamano) {
      String from=" FROM abonados a LEFT JOIN clientes c ON c.idcliente=a.idcliente_clientes LEFT JOIN clientes p ON p.idcliente=a.idresponsable LEFT JOIN rutas r ON r.idruta=a.idruta_rutas ";
      String where=" WHERE (CAST(:origen AS bigint) IS NULL OR a.idruta_rutas=CAST(:origen AS bigint)) AND (CAST(:destino AS bigint) IS NULL OR a.idruta_rutas IS DISTINCT FROM CAST(:destino AS bigint)) AND (:filtro='' OR CAST(a.idabonado AS text) ILIKE :patron OR c.nombre ILIKE :patron OR c.cedula ILIKE :patron OR p.nombre ILIKE :patron OR p.cedula ILIKE :patron) ";
      Map<String,Object> params=new HashMap<>();params.put("origen",origen);params.put("destino",destino);params.put("filtro",filtro);params.put("patron","%"+filtro.replace("!","!!").replace("%","!%").replace("_","!_")+"%");
      where=where.replace(" ILIKE :patron"," ILIKE :patron ESCAPE '!'");
      Long total=jdbc.queryForObject("SELECT COUNT(*)"+from+where,params,Long.class);
      params.put("limite",tamano);params.put("offset",(long)pagina*tamano);
      var datos=jdbc.queryForList("SELECT a.idabonado,a.nromedidor,a.estado,a.secuencia,a.idruta_rutas AS rutaorigen,r.codigo AS codigoruta,r.descripcion AS nombreruta,c.nombre AS cliente,p.nombre AS responsable"+from+where+" ORDER BY a.idruta_rutas,a.secuencia,a.idabonado LIMIT :limite OFFSET :offset",params);
      return new PageImpl<>(datos,PageRequest.of(pagina,tamano),total==null?0:total);
    }
}
