package com.epmapat.erp_epmapat.servicio.contabilidad;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;

import com.epmapat.erp_epmapat.DTO.contabilidad.SaldoCuentasDTO;
import com.epmapat.erp_epmapat.DTO.contabilidad.SaldoDTO;
import com.epmapat.erp_epmapat.modelo.contabilidad.Cuentas;
import com.epmapat.erp_epmapat.repositorio.contabilidad.CuentasR;
import com.epmapat.erp_epmapat.repositorio.contabilidad.TransaciR;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CuentaServicio {

   private final CuentasR dao;
   private final TransaciR transaciDao;

   public List<Cuentas> findAll() {
      return dao.findAll();
   }

   // Busca lista de Cuentas por Código y/o Nombre
   public List<Cuentas> findByCodigoyNombre(String codcue, String nomcue) {
      return dao.findByCodigoyNombre(codcue, nomcue);
   }

   public List<Cuentas> findByNomcue(String nomcue) {
      return dao.findByNomcue(nomcue);
   }

   public List<Cuentas> findByCodcue(String codcue) {
      return dao.findByCodcue(codcue);
   }

   // Busca el nombre de una cuenta por codcue
   public Object[] getNomCueByCodcue(String codcue) {
      return dao.findNomCueByCodcue(codcue);
   }

   // Busca una cuenta por codcue
   public Cuentas findCuentasByCodcue(String codcue) {
      return dao.findCuentaByCodcue(codcue);
   }

   // Valida codcue
   public boolean valCodcue(String codcue) {
      return dao.valCodcue(codcue);
   }

   // Valida el Nombre de la Cuenta
   public boolean valNomcue(String nomcue) {
      return dao.valNomcue(nomcue);
   }

   // Verifica si tiene Desagregación
   public boolean valDesagrega(String codcue, Integer nivcue) {
      return dao.valDesagrega(codcue, nivcue);
      // return dao.existsByCodcueLikeAndNivcueGreaterThan(codcue, nivcue);
   }

   // Bancos
   public List<Cuentas> findBancos() {
      return dao.findBancos();
   }

   public List<Cuentas> findByGrucue(String grucue) {
      return dao.findByGrucue(grucue);
   }

   public List<Cuentas> findByAsohaber(String asohaber) {
      return dao.findByAsohaber(asohaber);
   }

   public List<Cuentas> findByAsodebe(String asodebe) {
      return dao.findByAsodebeOrderByCodcue(asodebe);
   }

   // Cuentas por Tiptran para los DataList
   public List<Cuentas> findByTiptran(Integer tiptran, String codcue) {
      return dao.findByTiptran(tiptran, codcue);
   }

   public <S extends Cuentas> S save(S entity) {
      return dao.save(entity);
   }

   public List<Cuentas> findAll(Sort sort) {
      return dao.findAll(sort);
   }

   public Optional<Cuentas> findById(Long id) {
      return dao.findById(id);
   }

   public Boolean deleteById(Long id) {
      if (dao.existsById(id)) {
         dao.deleteById(id);
         return !dao.existsById(id);
      }
      return false;
   }

   public void delete(Cuentas entity) {
      dao.delete(entity);
   }

   // Cuentas de costos
   public List<Cuentas> findCuecostos() {
      return dao.findCuecostos();
   }

   // === Saldo de las Cuentas ===
   private boolean esCuentaDeudora(String codcue) {
      return codcue.startsWith("1") || codcue.startsWith("63") || codcue.startsWith("911");
   }

   private SaldoDTO calcularSaldoAnterior(String codcue, LocalDate desde) {
      List<Object[]> lista = transaciDao.obtenerTotalesAnteriores(codcue, desde);
      Object[] totales = lista.get(0);
      BigDecimal debitos = (BigDecimal) totales[0];
      BigDecimal creditos = (BigDecimal) totales[1];
      if (debitos == null)
         debitos = BigDecimal.ZERO;
      if (creditos == null)
         creditos = BigDecimal.ZERO;
      BigDecimal saldo = esCuentaDeudora(codcue)
            ? debitos.subtract(creditos)
            : creditos.subtract(debitos);
      return new SaldoDTO(debitos, creditos, saldo);
   }

   private SaldoDTO calcularSaldoPeriodo(String codcue, LocalDate desde, LocalDate hasta) {
      List<Object[]> lista = transaciDao.obtenerTotalesPeriodo(codcue, desde, hasta);
      Object[] totales = lista.get(0);
      BigDecimal debitos = (BigDecimal) totales[0];
      BigDecimal creditos = (BigDecimal) totales[1];
      if (debitos == null)
         debitos = BigDecimal.ZERO;
      if (creditos == null)
         creditos = BigDecimal.ZERO;
      BigDecimal saldo = esCuentaDeudora(codcue)
            ? debitos.subtract(creditos)
            : creditos.subtract(debitos);
      return new SaldoDTO(debitos, creditos, saldo);
   }

   private SaldoCuentasDTO calcularSaldosDeCuenta(Cuentas cuenta, LocalDate desde, LocalDate hasta) {

      // Si es cuenta de grupo (movcue = 1), sumar saldos de todas sus hijas
      if (cuenta.getMovcue() == 1) {
         List<Cuentas> hijas = dao.findByCodcueStartingWithOrderByCodcue(cuenta.getCodcue());
         BigDecimal saldoAnteriorGrupo = BigDecimal.ZERO;
         BigDecimal debitosGrupo = BigDecimal.ZERO;
         BigDecimal creditosGrupo = BigDecimal.ZERO;
         BigDecimal saldoPeriodoGrupo = BigDecimal.ZERO;
         for (Cuentas hija : hijas) {
            // Saldo anterior de la hija
            SaldoDTO anteriorHija = calcularSaldoAnterior(hija.getCodcue(), desde);
            // Saldo del período de la hija (incluye débitos y créditos)
            SaldoDTO periodoHija = calcularSaldoPeriodo(hija.getCodcue(), desde, hasta);
            // Acumular saldos
            saldoAnteriorGrupo = saldoAnteriorGrupo.add(anteriorHija.getSaldo());
            debitosGrupo = debitosGrupo.add(
                  periodoHija.getDebitos() != null ? periodoHija.getDebitos() : BigDecimal.ZERO);
            creditosGrupo = creditosGrupo.add(
                  periodoHija.getCreditos() != null ? periodoHija.getCreditos() : BigDecimal.ZERO);
            saldoPeriodoGrupo = saldoPeriodoGrupo.add(periodoHija.getSaldo());
         }
         BigDecimal saldoFinalGrupo = saldoAnteriorGrupo.add(saldoPeriodoGrupo);
         return new SaldoCuentasDTO(
               cuenta.getCodcue(),
               cuenta.getNomcue(),
               saldoAnteriorGrupo, // saldo anterior acumulado
               debitosGrupo, // débitos del período acumulados
               creditosGrupo, // créditos del período acumulados
               saldoPeriodoGrupo, // saldo del período acumulado
               saldoFinalGrupo // saldo final acumulado
         );
      }

      // Si es cuenta de movimiento (movcue = 2), usar la lógica normal
      SaldoDTO anterior = calcularSaldoAnterior(cuenta.getCodcue(), desde);
      SaldoDTO periodo = calcularSaldoPeriodo(cuenta.getCodcue(), desde, hasta);
      BigDecimal saldoFinal = anterior.getSaldo().add(periodo.getSaldo());
      return new SaldoCuentasDTO(
            cuenta.getCodcue(),
            cuenta.getNomcue(),
            anterior.getSaldo(),
            periodo.getDebitos(),
            periodo.getCreditos(),
            periodo.getSaldo(),
            saldoFinal);
   }

   public List<SaldoCuentasDTO> calcularSaldosPorCodcue(String codcue, LocalDate desde, LocalDate hasta) {
      // System.out.println("codcue en servicio: " + codcue);
      List<Cuentas> cuentas = dao.findByCodcueStartingWithOrderByCodcue(codcue);
      return cuentas.stream()
            .map(c -> calcularSaldosDeCuenta(c, desde, hasta))
            .filter(dto -> dto.getSaldoAnterior().compareTo(BigDecimal.ZERO) != 0 ||
                  dto.getDebitosPeriodo().compareTo(BigDecimal.ZERO) != 0 ||
                  dto.getCreditosPeriodo().compareTo(BigDecimal.ZERO) != 0 ||
                  dto.getSaldoPeriodo().compareTo(BigDecimal.ZERO) != 0 ||
                  dto.getSaldoFinal().compareTo(BigDecimal.ZERO) != 0)
            .toList();
   }

}
