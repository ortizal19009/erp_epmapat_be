package com.epmapat.erp_epmapat.servicio;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import com.epmapat.erp_epmapat.modelo.Facturas;

class FacturaServicioTransactionalTest {

    @Test
    void saveAndCascadeDeleteShouldUseSpringTransactions() throws NoSuchMethodException {
        Method saveMethod = FacturaServicio.class.getDeclaredMethod("save", Facturas.class);
        Method deleteMethod = FacturaServicio.class.getDeclaredMethod("eliminarFacturaElectronicaEnCascada", Long.class);

        assertTrue(saveMethod.isAnnotationPresent(Transactional.class),
                "save() must be annotated with Spring @Transactional to keep the cascade-delete query in an active transaction");
        assertTrue(deleteMethod.isAnnotationPresent(Transactional.class),
                "eliminarFacturaElectronicaEnCascada() must be annotated with Spring @Transactional for modifying queries");
    }
}
