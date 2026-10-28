package com.epmapat.erp_epmapat.repositorio;
import org.springframework.data.repository.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.epmapat.erp_epmapat.modelo.AuditoriaGenerica;
public interface RutaHistorialR extends Repository<AuditoriaGenerica,Long> {
    String FILTRO = " FROM auditoria_generica a WHERE a.entidad='abonados' AND a.tipo='CAMBIO_RUTA' "
      + " AND (CAST(:cuenta AS text) IS NULL OR CAST(a.entidad_id AS text)=CAST(:cuenta AS text)) "
      + " AND (CAST(:ruta AS text) IS NULL OR a.object_json->'rutaAnterior'->>'id'=CAST(:ruta AS text) "
      + " OR a.object_json->'rutaNueva'->>'id'=CAST(:ruta AS text)) ";
    @Query(value="SELECT a.*"+FILTRO+" ORDER BY a.fecmodi DESC,a.idauditoria DESC",countQuery="SELECT COUNT(*)"+FILTRO,nativeQuery=true)
    Page<AuditoriaGenerica> buscar(@Param("ruta") Long ruta,@Param("cuenta") Long cuenta,Pageable pageable);
}
