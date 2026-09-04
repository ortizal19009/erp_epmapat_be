package com.epmapat.erp_epmapat.servicio.contabilidad;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
import com.epmapat.erp_epmapat.modelo.contabilidad.Presupue;
import com.epmapat.erp_epmapat.repositorio.contabilidad.EjecucioR;
import com.epmapat.erp_epmapat.repositorio.contabilidad.PartixcertiR;
import com.epmapat.erp_epmapat.repositorio.contabilidad.PresupueR;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PresupueServicio {

   private final PresupueR dao;
   private final PartixcertiR partixcertiDao;
   private final EjecucioR ejecucioDao;

   // Busca Partidas de ingresos o Gastos (Para cálculos con todas las partidas)
   public List<Presupue> buscaPartidas(Integer tippar) {
      return dao.buscaPartidas(tippar);
   }

   // Partidas de Ingresos por Codigo Y Nombre
   public List<Presupue> findAllIng(String codpar, String nompar) {
      int tippar = 1; // Partidas de Ingresos
      String codparPattern = codpar + "%";
      String nomparPattern = "%" + nompar.toLowerCase() + "%";
      return dao.findAllByTippar(tippar, codparPattern, nomparPattern);
   }

   // Busca por Código o Nombre
   public List<Presupue> findCodigoNombre(String codigoNombre) {
      return dao.findCodigoNombre(codigoNombre);
   }

   // Busca por codpar para datalist (OJO: Ya había, pero no se sabe si era por
   // codigo completo )
   public List<Presupue> findByCodpar(Long tippar, String codpar) {
      return dao.findByCodpar(tippar, codpar);
   }

   // Partidas por naturaleza (para cobrado/pagado)
   public List<Presupue> findByTipparAndNaturaleza(int tippar, int inicio, String naturaleza) {
      return dao.findByTipparAndNaturaleza(tippar, inicio, naturaleza);
   }

   public List<Presupue> findByNompar(Long tippar, String nompar) {
      return dao.findByNompar(tippar, nompar);
   }

   public List<Presupue> findByTippar(Long tippar) {
      return dao.findByTippar(tippar);
   }

   public List<Presupue> buscaByCodigoI(String codpar) {
      return dao.buscaByCodigoI(codpar);
   }

   // Busca por Tipo, Código y Nombre
   public List<Presupue> findByTipoCodigoyNombre(Integer tippar, String codpar, String nompar) {
      return dao.findByTipoCodigoyNombre(tippar, codpar, nompar);
   }

   // Validar Código
   public List<Presupue> buscaByCodigo(String codpar) {
      return dao.buscaByCodpar(codpar);
   }

   public List<Presupue> findByActividad(Long intest) {
      return dao.findByActividad(intest);
   }

   // Partidas de un partida del clasificador
   public List<Presupue> buscaClasificador(String codigo) {
      return dao.buscaClasificador(codigo);
   }

   // Busca una partida
   public Optional<Presupue> getPresupueByCodpar(String codpar) {
      return dao.findByCodpar(codpar);
   }

   // Suma partixcerti.valor y lo guarda en presupue.totcerti
   public void recalculaValorTotcerti(Long intpre) {
      BigDecimal total = partixcertiDao.sumaValoresPorPartida(intpre);
      Presupue partida = dao.findById(intpre)
            .orElseThrow(() -> new RuntimeException("Partida no encontrada: " + intpre));
      partida.setTotcerti(total);
      dao.save(partida);
   }

   // Usada en Ejecución Presupuestaria
   public Double totalCodpar(Long tippar, String codpar) {
      Double total = dao.totalCodpar(tippar, codpar);
      return total;
   }

   public <S extends Presupue> S save(S entity) {
      return dao.save(entity);
   }

   // Actualiza presupue.totmisos
   // @Transactional ya se usa en la llamada
   public void actualizaTotmisos(Long intpre) {
      BigDecimal totmisos = ejecucioDao.sumaPrmisoPorIntpre(intpre);
      Presupue presu = dao.findById(intpre)
            .orElseThrow(() -> new IllegalArgumentException("No existe Partida con id " + intpre));
      presu.setTotmisos(totmisos);
      dao.save(presu);
   }

   public List<Presupue> findAll() {
      return dao.findAll();
   }

   public Optional<Presupue> findById(Long id) {
      return dao.findById(id);
   }

   // Actualiza solo los modificados con Patch
   public Presupue updatePresupue(Long intpre, Presupue data) {
      Presupue partida = findById(intpre)
            .orElseThrow(() -> new ResourceNotFoundExcepciones(
                  "No se encuentra este Id " + intpre));
      // Coloca campos (solo los modificados)
      if (data.getCodpar() != null)
         partida.setCodpar(data.getCodpar());
      if (data.getCodigo() != null)
         partida.setCodigo(data.getCodigo());
      if (data.getNompar() != null)
         partida.setNompar(data.getNompar());
      partida.setUsumodi(data.getUsumodi());
      partida.setFecmodi(data.getFecmodi());
      return save(partida);
   }

   public Boolean deleteById(Long id) {
      if (dao.existsById(id)) {
         dao.deleteById(id);
         return !dao.existsById(id);
      }
      return false;
   }

   public void delete(Presupue entity) {
      dao.delete(entity);
   }

}
