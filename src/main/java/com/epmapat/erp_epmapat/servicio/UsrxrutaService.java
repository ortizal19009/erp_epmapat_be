package com.epmapat.erp_epmapat.servicio;

import java.util.List;
import java.util.Optional;
// import java.util.stream.Collectors; // si usas Java 11

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.epmapat.erp_epmapat.excepciones.RutasOcupadasException;
import com.epmapat.erp_epmapat.interfaces.UsrxrutasService;
import com.epmapat.erp_epmapat.modelo.Emisiones;
import com.epmapat.erp_epmapat.modelo.Rutas;
import com.epmapat.erp_epmapat.modelo.Usrxrutas;
import com.epmapat.erp_epmapat.modelo.administracion.Usuarios;
import com.epmapat.erp_epmapat.repositorio.EmisionesR;
import com.epmapat.erp_epmapat.repositorio.UsrxrutasR;
import com.epmapat.erp_epmapat.repositorio.administracion.UsuariosR;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UsrxrutaService implements UsrxrutasService {

    private final UsrxrutasR usrxrutasRepository;
    private final UsuariosR usuariosRepository;
    private final EmisionesR emisionesRepository;

    @Override
    public Usrxrutas crear(Usrxrutas entity) {

        Long idusuario = entity.getIdusuario_usuarios() != null ? entity.getIdusuario_usuarios().getIdusuario() : null;
        Long idemision = entity.getIdemision_emisiones() != null ? entity.getIdemision_emisiones().getIdemision() : null;

        if (idusuario == null || idemision == null) {
            throw new IllegalArgumentException("Debe enviar idusuario_usuarios e idemision_emisiones.");
        }

        Usuarios usuario = usuariosRepository.findById(idusuario)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no existe: " + idusuario));

        Emisiones emision = emisionesRepository.findById(idemision)
                .orElseThrow(() -> new EntityNotFoundException("Emisión no existe: " + idemision));

        // =========================
        // ✅ extraer idrutas como List<Long> (NO array)
        // =========================
        List<Rutas> rutas = entity.getRutas() != null ? entity.getRutas() : List.of();

        // Java 16+:
        List<Long> idrutas = rutas.stream()
                .map(Rutas::getIdruta)
                .toList();

        // Si estás en Java 11, usa:
        // List<Long> idrutas = rutas.stream().map(Rutas::getIdruta).collect(Collectors.toList());

        // validar ocupadas por otros
        if (!idrutas.isEmpty()) {
            List<Long> ocupadas = usrxrutasRepository
                    .findRutasOcupadasEnEmisionPorOtros(idemision, idusuario, idrutas);

            if (!ocupadas.isEmpty()) {
                throw new RutasOcupadasException("Existen rutas ya asignadas a otro lector.", ocupadas);
            }
        }

        // upsert por usuario+emision
        Optional<Usrxrutas> existente = usrxrutasRepository.findByUsuarioAndEmision(idusuario, idemision);
        if (existente.isPresent()) {
            Usrxrutas reg = existente.get();
            reg.setRutas(entity.getRutas());
            return usrxrutasRepository.save(reg);
        }

        entity.setIdusuario_usuarios(usuario);
        entity.setIdemision_emisiones(emision);
        return usrxrutasRepository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> rutasOcupadasEnEmision(Long idemision) {
        return usrxrutasRepository.findRutasOcupadasEnEmision(idemision);
    }

    @Override
    public Usrxrutas actualizar(Long id, Usrxrutas entity) {

        Usrxrutas actual = usrxrutasRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Registro no existe: " + id));

        // actualizar usuario/emision si vienen
        if (entity.getIdusuario_usuarios() != null && entity.getIdusuario_usuarios().getIdusuario() != null) {
            Long idusuario = entity.getIdusuario_usuarios().getIdusuario();
            Usuarios usuario = usuariosRepository.findById(idusuario)
                    .orElseThrow(() -> new EntityNotFoundException("Usuario no existe: " + idusuario));
            actual.setIdusuario_usuarios(usuario);
        }

        if (entity.getIdemision_emisiones() != null && entity.getIdemision_emisiones().getIdemision() != null) {
            Long idemision = entity.getIdemision_emisiones().getIdemision();
            Emisiones emision = emisionesRepository.findById(idemision)
                    .orElseThrow(() -> new EntityNotFoundException("Emisión no existe: " + idemision));
            actual.setIdemision_emisiones(emision);
        }

        // si cambian rutas: validar ocupadas por otros
        if (entity.getRutas() != null) {
            Long idusuarioActual = actual.getIdusuario_usuarios().getIdusuario();
            Long idemisionActual = actual.getIdemision_emisiones().getIdemision();

            // =========================
            // ✅ extraer idrutas como List<Long>
            // =========================
            List<Long> idrutas = entity.getRutas().stream()
                    .map(Rutas::getIdruta)
                    .toList();

            // Java 11:
            // List<Long> idrutas = entity.getRutas().stream().map(Rutas::getIdruta).collect(Collectors.toList());

            if (!idrutas.isEmpty()) {
                List<Long> ocupadas = usrxrutasRepository
                        .findRutasOcupadasEnEmisionPorOtros(idemisionActual, idusuarioActual, idrutas);

                if (!ocupadas.isEmpty()) {
                    throw new RutasOcupadasException("Existen rutas ya asignadas a otro lector.", ocupadas);
                }
            }

            actual.setRutas(entity.getRutas());
        }

        return usrxrutasRepository.save(actual);
    }

    @Override
    @Transactional(readOnly = true)
    public Usrxrutas obtenerPorId(Long id) {
        return usrxrutasRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Registro no existe: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usrxrutas> listar() {
        return usrxrutasRepository.findAll();
    }

    @Override
    public void eliminar(Long id) {
        if (!usrxrutasRepository.existsById(id)) {
            throw new EntityNotFoundException("Registro no existe: " + id);
        }
        usrxrutasRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usrxrutas> listarPorUsuario(Long idusuario) {
        return usrxrutasRepository.findByUsuario(idusuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usrxrutas> listarPorEmision(Long idemision) {
        return usrxrutasRepository.findByEmision(idemision);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usrxrutas> findByUsuarioAndEmision(Long idusuario, Long idemision) {
        return usrxrutasRepository.findByUsuarioAndEmision(idusuario, idemision);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usrxrutas> findByEmision(Long idemision) {
        return usrxrutasRepository.findByEmision(idemision);
    }
}
