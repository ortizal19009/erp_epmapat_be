package com.epmapat.erp_epmapat.servicio;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.Abonados;
import com.epmapat.erp_epmapat.modelo.Clientes;
import com.epmapat.erp_epmapat.modelo.Facturas;
import com.epmapat.erp_epmapat.modelo.Lecturas;
import com.epmapat.erp_epmapat.modelo.ClienteMerge;
import com.epmapat.erp_epmapat.modelo.ClienteMergeAbonado;
import com.epmapat.erp_epmapat.modelo.ClienteMergeCliente;
import com.epmapat.erp_epmapat.modelo.ClienteMergeFactura;
import com.epmapat.erp_epmapat.modelo.ClienteMergeLectura;
import com.epmapat.erp_epmapat.repositorio.*;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ClienteMergeService {

    private final ClientesR clienteRepo;
    private final AbonadosR abonadoRepo;
    private final FacturasR facturaRepo;
    private final LecturasR lecturasRepo;

    private final ClienteMergeR mergeRepo;
    private final ClienteMergeClienteR mergeClienteRepo;
    private final ClienteMergeAbonadoR mergeAbonadoRepo;
    private final ClienteMergeFacturaR mergeFacturaRepo;
    private final ClienteMergeLecturaR mergeLecturaRepo;

    @Transactional
    public void merge(Long masterId, List<Long> duplicateIds, Long usuario) {

        if (duplicateIds == null || duplicateIds.isEmpty()) {
            throw new IllegalArgumentException("No existen clientes duplicados para merge");
        }

        if (duplicateIds.contains(masterId)) {
            throw new IllegalArgumentException("El cliente master no puede estar en duplicados");
        }

        Clientes master = clienteRepo.findById(masterId)
                .orElseThrow(() -> new RuntimeException("Cliente master no existe"));

        ClienteMerge merge = new ClienteMerge();
        merge.setMasterId(masterId);
        merge.setUsuarioMerge(usuario);
        merge.setFechaMerge(LocalDateTime.now());
        mergeRepo.save(merge);

        for (Long dupId : duplicateIds) {

            Clientes duplicado = clienteRepo.findById(dupId)
                    .orElseThrow(() -> new RuntimeException("Cliente duplicado no existe"));

            mergeClienteRepo.save(
                    new ClienteMergeCliente(merge.getIdMerge(), dupId));

            // ABONADOS
            List<Abonados> abonados = abonadoRepo.findByIdcliente(dupId);
            for (Abonados a : abonados) {
                mergeAbonadoRepo.save(
                        new ClienteMergeAbonado(
                                merge.getIdMerge(),
                                a.getIdabonado(),
                                dupId));
                a.setIdcliente_clientes(master);
                abonadoRepo.save(a);
            }

            // FACTURAS PENDIENTES
            List<Facturas> facturas = facturaRepo.findSincobroToMerge(dupId);
            for (Facturas f : facturas) {
                mergeFacturaRepo.save(
                        new ClienteMergeFactura(
                                merge.getIdMerge(),
                                f.getIdfactura(),
                                dupId));
                f.setIdcliente(master);
                facturaRepo.save(f);
            }
            // LECTURAS
            List<Lecturas> lecturas = lecturasRepo.findPendientesByCliente(dupId);
            for (Lecturas l : lecturas) {

                ClienteMergeLectura ml = new ClienteMergeLectura();
                ml.setIdMerge(merge.getIdMerge());
                ml.setIdLectura(l.getIdlectura()); // <- usa el getter real
                ml.setIdClienteOrigen(dupId);
                mergeLecturaRepo.save(ml);
                // Reasignar responsable al master (según cómo esté mapeado en tu entity
                // Lecturas)
                // l.setIdresponsable(master);
                lecturasRepo.save(l);
            }

            // UPDATE real
            lecturasRepo.reasignarCliente(dupId, masterId);
            LocalDate now = LocalDate.now();
            // Desactivar cliente duplicado
            duplicado.setUsumodi(usuario);
            duplicado.setFecmodi(now);
            duplicado.setEstado(0L);
            duplicado.setActivo(false);
            clienteRepo.save(duplicado);
        }
    }

}
