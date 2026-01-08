package com.epmapat.erp_epmapat.servicio;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

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

    public List<Usrxrutas> findByUsuarioAndEmision(Long idusuario, Long idemision) {
        return usrxrutaRepository.findByUsuarioAndEmision(idusuario, idemision);
    }
}
