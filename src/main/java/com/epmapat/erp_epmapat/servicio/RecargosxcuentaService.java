package com.epmapat.erp_epmapat.servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.Recargosxcuenta;
import com.epmapat.erp_epmapat.repositorio.RecargosxcuentaR;

@Service
public class RecargosxcuentaService {
    @Autowired
    private RecargosxcuentaR recargosxcuentaR;

    // Add methods here
    // Save method
    public Recargosxcuenta save(Recargosxcuenta recargosxcuenta) {
        return recargosxcuentaR.save(recargosxcuenta);
    }
    // deleteById method
    public void deleteById(Long id) {
        recargosxcuentaR.deleteById(id);
    }
    // findAll by emision
    public List<Recargosxcuenta> findAllByEmision(Long emision) {
        return recargosxcuentaR.findByIdEmision(emision);
    }
}
