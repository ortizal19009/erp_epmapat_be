package com.epmapat.erp_epmapat.interfaces;

import java.util.List;
import java.util.Optional;

import com.epmapat.erp_epmapat.modelo.Usrxrutas;


public interface UsrxrutasService {

    Usrxrutas crear(Usrxrutas entity);
    Usrxrutas actualizar(Long id, Usrxrutas entity);
    Usrxrutas obtenerPorId(Long id);

    List<Usrxrutas> listar();
    void eliminar(Long id);

    List<Usrxrutas> listarPorUsuario(Long idusuario);
    List<Usrxrutas> listarPorEmision(Long idemision);

    Optional<Usrxrutas> findByUsuarioAndEmision(Long idusuario, Long idemision);
    List<Usrxrutas> findByEmision(Long idemision);
    List<Long> rutasOcupadasEnEmision(Long idemision);
}
