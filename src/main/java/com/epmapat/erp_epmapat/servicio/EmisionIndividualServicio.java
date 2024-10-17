package com.epmapat.erp_epmapat.servicio;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.interfaces.EmisionIndividualRI;
import com.epmapat.erp_epmapat.interfaces.EmisionIndividualRia;
import com.epmapat.erp_epmapat.interfaces.EmisionIndividualRin;
import com.epmapat.erp_epmapat.interfaces.IemiIndividual;
import com.epmapat.erp_epmapat.interfaces.R_refacturacion_int;
import com.epmapat.erp_epmapat.modelo.EmisionIndividual;
import com.epmapat.erp_epmapat.repositorio.EmisionIndividualR;

@Service
public class EmisionIndividualServicio {
    @Autowired
    private EmisionIndividualR dao;

    public <S extends EmisionIndividual> S save(S entity) {
        return dao.save(entity);
    }

    public List<EmisionIndividual> findByIdEmision(Long idemision) {
        return dao.findByIdEmision(idemision);
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

}
