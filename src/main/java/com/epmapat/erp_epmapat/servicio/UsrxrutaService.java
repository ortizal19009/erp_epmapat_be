package com.epmapat.erp_epmapat.servicio;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.DTO.RutaDTO;
import com.epmapat.erp_epmapat.modelo.Rutas;
import com.epmapat.erp_epmapat.modelo.Usrxrutas;
import com.epmapat.erp_epmapat.repositorio.UsrxrutasR;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UsrxrutaService {
    private final UsrxrutasR usrxrutaRepository;

    public List<Usrxrutas> findAll() {
        return usrxrutaRepository.findAll();
    }

    public Optional<Usrxrutas> findById(Long id) {
        return usrxrutaRepository.findById(id);
    }

    public Usrxrutas save(Usrxrutas usrxrutas) {
        return usrxrutaRepository.save(usrxrutas);
    }

    public void delete(Usrxrutas usrxrutas) {
        usrxrutaRepository.delete(usrxrutas);
    }

    public Optional<Usrxrutas> findByUsuarioAndEmision(Long idusuario, Long idemision) {
        return usrxrutaRepository.findByUsuarioAndEmision(idusuario, idemision);
    }

    public List<Usrxrutas> findByEmision(Long idemision) {
        return usrxrutaRepository.findByEmision(idemision);
    }

    @Transactional
    public Usrxrutas saveOrUpdate(Usrxrutas request) {

        Optional<Usrxrutas> optional = usrxrutaRepository
                .findByUsuarioAndEmision(
                        request.getIdusuario_usuarios().getIdusuario(),
                        request.getIdemision_emisiones().getIdemision());

        if (optional.isPresent()) {
            Usrxrutas existente = optional.get();

            List<RutaDTO> actuales = existente.getRutas();
            List<RutaDTO> nuevas = request.getRutas();

            if (actuales == null)
                actuales = new ArrayList<>();
            if (nuevas != null) {
                for (RutaDTO rn : nuevas) {
                    boolean existe = actuales.stream()
                            .anyMatch(ra -> ra.getIdruta().equals(rn.getIdruta()));

                    if (!existe) {
                        actuales.add(rn);
                    }
                }
            }

            existente.setRutas(actuales);
            return usrxrutaRepository.save(existente);
        }

        return usrxrutaRepository.save(request);
    }

}
