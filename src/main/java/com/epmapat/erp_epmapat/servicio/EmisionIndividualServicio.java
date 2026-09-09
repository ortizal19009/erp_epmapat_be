package com.epmapat.erp_epmapat.servicio;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.epmapat.erp_epmapat.DTO.RefacturacionIndividualRequest;
import com.epmapat.erp_epmapat.DTO.RefacturacionIndividualResponse;
import com.epmapat.erp_epmapat.interfaces.EmisionIndividualRI;
import com.epmapat.erp_epmapat.interfaces.EmisionIndividualListado;
import com.epmapat.erp_epmapat.interfaces.EmisionIndividualRia;
import com.epmapat.erp_epmapat.interfaces.EmisionIndividualRin;
import com.epmapat.erp_epmapat.interfaces.FacEliminadas;
import com.epmapat.erp_epmapat.interfaces.IemiIndividual;
import com.epmapat.erp_epmapat.interfaces.R_refacturacion_int;
import com.epmapat.erp_epmapat.interfaces.RubroxfacI;
import com.epmapat.erp_epmapat.modelo.EmisionIndividual;
import com.epmapat.erp_epmapat.modelo.Abonados;
import com.epmapat.erp_epmapat.modelo.Emisiones;
import com.epmapat.erp_epmapat.modelo.Facturas;
import com.epmapat.erp_epmapat.modelo.Lecturas;
import com.epmapat.erp_epmapat.modelo.Modulos;
import com.epmapat.erp_epmapat.modelo.Novedad;
import com.epmapat.erp_epmapat.modelo.Rutasxemision;
import com.epmapat.erp_epmapat.repositorio.AbonadosR;
import com.epmapat.erp_epmapat.repositorio.EmisionIndividualR;
import com.epmapat.erp_epmapat.repositorio.EmisionesR;
import com.epmapat.erp_epmapat.repositorio.LecturasR;
import com.epmapat.erp_epmapat.repositorio.NovedadR;
import com.epmapat.erp_epmapat.repositorio.RutasxemisionR;

@Service
public class EmisionIndividualServicio {
    @Autowired
    private EmisionIndividualR dao;
    @Autowired private AbonadosR abonadosR;
    @Autowired private EmisionesR emisionesR;
    @Autowired private LecturasR lecturasR;
    @Autowired private NovedadR novedadR;
    @Autowired private RutasxemisionR rutasxemisionR;
    @Autowired private FacturaServicio facturaServicio;
    @Autowired private LecturaServicio lecturaServicio;
    @Autowired private EmisionServicioOptimizado emisionServicioOptimizado;
    @Autowired private EmisionServicioOptimizadoV2 emisionServicioOptimizadoV2;
    @Autowired private EmisionServicioOptimizado_anterior emisionServicioOptimizadoAnterior;

    public <S extends EmisionIndividual> S save(S entity) {
        return dao.save(entity);
    }

    @Transactional
    public RefacturacionIndividualResponse crearRefacturacion(RefacturacionIndividualRequest request) {
        validarSolicitudRefacturacion(request);
        Emisiones emision = emisionesR.findById(request.getIdemision())
                .orElseThrow(() -> new IllegalArgumentException("No existe la emisión seleccionada."));
        Abonados abonado = abonadosR.findById(request.getIdabonado())
                .orElseThrow(() -> new IllegalArgumentException("No existe el abonado seleccionado."));
        Rutasxemision ruta = rutasxemisionR.findById(request.getIdrutaxemision())
                .orElseThrow(() -> new IllegalArgumentException("No existe la ruta de la emisión."));
        if (ruta.getIdemision_emisiones() == null
                || !request.getIdemision().equals(ruta.getIdemision_emisiones().getIdemision())) {
            throw new IllegalArgumentException("La ruta no corresponde a la emisión seleccionada.");
        }
        Lecturas lecturaAnterior = lecturasR.findById(request.getIdlecturaanterior())
                .orElseThrow(() -> new IllegalArgumentException("No existe la lectura anterior a refacturar."));

        Facturas factura = new Facturas();
        Modulos modulo = new Modulos();
        modulo.setIdmodulo(4L);
        factura.setIdmodulo(modulo);
        factura.setIdcliente(abonado.getIdresponsable());
        factura.setIdabonado(abonado.getIdabonado());
        factura.setPorcexoneracion(0L);
        factura.setTotaltarifa(BigDecimal.ZERO);
        factura.setPagado(0);
        factura.setConveniopago(0L);
        factura.setEstadoconvenio(0L);
        factura.setFormapago(1L);
        factura.setValorbase(BigDecimal.ZERO);
        factura.setUsucrea(request.getIdusuario());
        factura.setEstado(1L);
        factura.setFeccrea(LocalDate.now());
        factura = facturaServicio.saveForNewEmision(factura);

        Lecturas lecturaNueva = new Lecturas();
        lecturaNueva.setEstado(0);
        lecturaNueva.setFechaemision(new Date());
        lecturaNueva.setLecturaanterior(request.getLecturaanterior());
        lecturaNueva.setLecturaactual(request.getLecturaactual());
        lecturaNueva.setLecturadigitada(request.getLecturaactual());
        lecturaNueva.setMesesmulta(0);
        lecturaNueva.setIdemision(emision.getIdemision());
        lecturaNueva.setIdabonado_abonados(abonado);
        lecturaNueva.setIdresponsable(abonado.getIdresponsable().getIdcliente());
        lecturaNueva.setIdcategoria(abonado.getIdcategoria_categorias().getIdcategoria());
        lecturaNueva.setIdrutaxemision_rutasxemision(ruta);
        lecturaNueva.setTotal1(BigDecimal.ZERO);
        lecturaNueva.setIdfactura(factura.getIdfactura());
        lecturaNueva.setFotoPath("");
        if (request.getIdnovedad() != null) {
            Novedad novedad = novedadR.findById(request.getIdnovedad())
                    .orElseThrow(() -> new IllegalArgumentException("No existe la novedad seleccionada."));
            lecturaNueva.setIdnovedad_novedades(novedad);
        }
        lecturaNueva = lecturaServicio.saveLectura(lecturaNueva);

        int consumo = Math.round(request.getLecturaactual() - request.getLecturaanterior());
        int categoria = abonado.getIdcategoria_categorias().getIdcategoria().intValue();
        boolean municipio = Boolean.TRUE.equals(abonado.getMunicipio());
        boolean adultoMayor = Boolean.TRUE.equals(abonado.getAdultomayor());
        boolean aguaPotable = Boolean.TRUE.equals(abonado.getSwalcantarillado());
        BigDecimal total = calcularValoresRefacturacion(
                emision.getIdemision(), abonado.getIdabonado(), factura.getIdfactura(), consumo, categoria,
                municipio, adultoMayor, aguaPotable);
        if (Boolean.TRUE.equals(request.getSwmulta())) {
            facturaServicio.setMulta(factura.getIdfactura());
        }

        EmisionIndividual emisionIndividual = new EmisionIndividual(null, emision, lecturaNueva, lecturaAnterior);
        emisionIndividual = dao.save(emisionIndividual);
        return new RefacturacionIndividualResponse(
                factura.getIdfactura(), lecturaNueva.getIdlectura(), emisionIndividual.getIdemisionindividual(), total);
    }

    private BigDecimal calcularValoresRefacturacion(Long idemision, Long cuenta, Long idfactura, int consumo,
            int categoria, boolean municipio, boolean adultoMayor, boolean aguaPotable) {
        if (idemision.equals(243L)) {
            return emisionServicioOptimizado.calcularValores(idemision, cuenta, idfactura, consumo, categoria,
                    municipio, adultoMayor, aguaPotable, true);
        }
        if (idemision > 243L) {
            return emisionServicioOptimizadoV2.calcularValores(idemision, cuenta, idfactura, consumo, categoria,
                    municipio, adultoMayor, aguaPotable, false, true);
        }
        return emisionServicioOptimizadoAnterior.calcularValores(cuenta, idfactura, consumo, categoria,
                municipio, adultoMayor, aguaPotable, true);
    }

    private void validarSolicitudRefacturacion(RefacturacionIndividualRequest request) {
        if (request == null || request.getIdemision() == null || request.getIdabonado() == null
                || request.getIdrutaxemision() == null || request.getIdlecturaanterior() == null
                || request.getLecturaanterior() == null || request.getLecturaactual() == null
                || request.getIdusuario() == null) {
            throw new IllegalArgumentException("Faltan datos obligatorios para la refacturación.");
        }
    }

    public List<EmisionIndividual> findByIdEmision(Long idemision) {
        return dao.findByIdEmision(idemision);
    }

    public List<EmisionIndividualListado> findListadoByIdEmision(Long idemision) {
        return dao.findListadoByIdEmision(idemision);
    }

    public EmisionIndividual findDetalleById(Long id) {
        return dao.findDetalleById(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe la emisión individual solicitada."));
    }

    public List<IemiIndividual> findLecturasNuevas(Long idemision) {
        return dao.findLecturasNuevas(idemision);
    }

    public List<IemiIndividual> findLecturasAnteriores(Long idemision) {
        return dao.findLecturasAnteriores(idemision);
    }

    public List<EmisionIndividualRI> getLecReport(Integer idemision) {
        return dao.getLecReport(idemision);
    }

    public List<EmisionIndividualRia> emisionIndividualAnterior(Integer idemision) {
        return dao.emisionIndividualAnterior(idemision);
    }

    public List<EmisionIndividualRin> emisionIndividualNueva(Integer idemision) {
        return dao.emisionIndividualNueva(idemision);
    }

    public List<R_refacturacion_int> getRefacturacionxEmision(Long idemision) {
        return dao.getRefacturacionxEmision(idemision);
    }

    public List<R_refacturacion_int> getRefacturacionxFecha(Date d, Date h) {
        return dao.getRefacturacionxFecha(d, h);
    }

    public List<RubroxfacI> getRefacturacionxEmisionRubrosAnteriores(Long idemision) {
        return dao.getRefacturacionxEmisionRubrosAnteriores(idemision);
    }

    public List<RubroxfacI> getRefacturacionxEmisionRubrosNuevos(Long idemision) {
        return dao.getRefacturacionxEmisionRubrosNuevos(idemision);
    }

    public List<RubroxfacI> getRefacturacionxFechaRubrosAnteriores(Date d, Date h) {
        return dao.getRefacturacionxFechaRubrosAnteriores(d, h);
    }

    public List<RubroxfacI> getRefacturacionxFechaRubrosNuevos(Date d, Date h) {
        return dao.getRefacturacionxFechaRubrosNuevos(d, h);
    }

    public List<FacEliminadas> getFacElimByFechaElimina(LocalDate d, LocalDate h) {
        return dao.getFacElimByFechaElimina(d, h);
    }

    public List<FacEliminadas> getFacElimByEmision(Long idemision) {
        return dao.getFacElimByEmision(idemision);
    }

}
