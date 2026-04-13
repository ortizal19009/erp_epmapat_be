package com.epmapat.erp_epmapat.servicio;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.DTO.ClienteMergeMonitorDetalleDTO;
import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
import com.epmapat.erp_epmapat.interfaces.ClienteMergeMonitorView;
import com.epmapat.erp_epmapat.modelo.ClienteMerge;
import com.epmapat.erp_epmapat.repositorio.ClienteMergeAbonadoR;
import com.epmapat.erp_epmapat.repositorio.ClienteMergeClienteR;
import com.epmapat.erp_epmapat.repositorio.ClienteMergeFacturaR;
import com.epmapat.erp_epmapat.repositorio.ClienteMergeLecturaR;
import com.epmapat.erp_epmapat.repositorio.ClienteMergeR;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ClienteMergeMonitorService {
    private final ClienteMergeR mergeRepo;
    private final ClienteMergeClienteR mergeClienteRepo;
    private final ClienteMergeAbonadoR mergeAbonadoRepo;
    private final ClienteMergeFacturaR mergeFacturaRepo;
    private final ClienteMergeLecturaR mergeLecturaRepo;

    public Page<ClienteMergeMonitorView> findMonitor(
            String q,
            Long masterId,
            Long usuario,
            LocalDateTime desde,
            LocalDateTime hasta,
            Pageable pageable) {
        return mergeRepo.findMonitor(q == null ? "" : q.trim(), masterId, usuario, desde, hasta, pageable);
    }

    public ClienteMergeMonitorDetalleDTO findDetalle(Long idMerge) {
        ClienteMerge merge = mergeRepo.findById(idMerge)
                .orElseThrow(() -> new ResourceNotFoundExcepciones("No existe el merge Id: " + idMerge));

        return new ClienteMergeMonitorDetalleDTO(
                merge,
                mergeClienteRepo.findByIdMerge(idMerge),
                mergeAbonadoRepo.findByIdMerge(idMerge),
                mergeFacturaRepo.findByIdMerge(idMerge),
                mergeLecturaRepo.findByIdMerge(idMerge));
    }
}
