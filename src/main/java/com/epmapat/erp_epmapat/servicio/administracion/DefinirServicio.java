package com.epmapat.erp_epmapat.servicio.administracion;

import java.io.IOException;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.epmapat.erp_epmapat.config.AESUtil;
import com.epmapat.erp_epmapat.interfaces.DefinirProjection;
import com.epmapat.erp_epmapat.modelo.administracion.Definir;
import com.epmapat.erp_epmapat.repositorio.administracion.DefinirR;

@Service
public class DefinirServicio {
    @Autowired
    DefinirR dao;

    @SuppressWarnings("null")
    public Optional<Definir> findById(Long id) {
        return dao.findById(id);
    }

    @SuppressWarnings("null")
    public <S extends Definir> S save(S entity) {
        return dao.save(entity);
    }

    public Definir ultima() {
        return dao.findTopByOrderByIddefinirDesc();
    }

    public DefinirProjection findDefinirWithoutFirma(Long id) {
        return dao.findDefinirWithoutFirma(id);
    }

    public Definir guardarFirma(Long id, MultipartFile archivo, String claveFirma) throws Exception {
        // Validar parámetros de entrada
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }
        if (archivo == null || archivo.isEmpty()) {
            throw new IllegalArgumentException("El archivo de firma no puede estar vacío");
        }
        if (claveFirma == null || claveFirma.trim().isEmpty()) {
            throw new IllegalArgumentException("La clave de firma no puede estar vacía");
        }

        // Buscar el registro
        Definir definir = dao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Registro no encontrado con ID: " + id));

        try {
            // Procesar los datos
            byte[] firmaBytes = archivo.getBytes();
            String claveCifrada = AESUtil.cifrar(claveFirma);

            // Actualizar el objeto
            definir.setFirma(firmaBytes);
            definir.setClave_firma(claveCifrada);

            // Guardar y retornar
            return dao.save(definir);
        } catch (IOException e) {
            throw new RuntimeException("Error al leer el archivo de firma", e);
        } catch (Exception e) {
            throw new RuntimeException("Error al cifrar la clave o guardar la firma", e);
        }
    }

    public Object desEncriptar(Long id) throws Exception {
        // Buscar el registro
        Definir definir = dao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Registro no encontrado con ID: " + id));
        String claveCifrada = AESUtil.descifrar(definir.getClave_email());
        return claveCifrada;

    }

    public String encriptar(String clave) throws Exception {
        String claveCifrada = AESUtil.cifrar(clave);
        return claveCifrada;
    }
}
