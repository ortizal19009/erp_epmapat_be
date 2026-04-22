package com.epmapat.erp_epmapat.servicio.administracion;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.epmapat.erp_epmapat.modelo.administracion.CorreosEnviados;
import com.epmapat.erp_epmapat.repositorio.administracion.CorreosEnviadosR;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CorreosEnviadosServicio {

    private final CorreosEnviadosR dao;

    public CorreosEnviados registrarEnvio(
            String modulo,
            Long documentoid,
            String documento,
            String destinatarios,
            String asunto,
            String remitente,
            String archivoadjunto,
            String estado,
            String detalle) {

        CorreosEnviados envio = new CorreosEnviados();
        envio.setModulo(limpiar(modulo));
        envio.setDocumentoid(documentoid);
        envio.setDocumento(limpiar(documento));
        envio.setDestinatarios(limpiar(destinatarios));
        envio.setAsunto(limpiar(asunto));
        envio.setRemitente(limpiar(remitente));
        envio.setArchivoadjunto(limpiar(archivoadjunto));
        envio.setEstado(limpiar(estado));
        envio.setDetalle(limpiar(detalle));
        envio.setFechaenvio(new Timestamp(System.currentTimeMillis()));
        return dao.save(envio);
    }

    public List<CorreosEnviados> listar(String modulo, String estado) {
        String moduloFiltro = limpiar(modulo);
        String estadoFiltro = limpiar(estado);
        return dao.findByModuloContainingIgnoreCaseAndEstadoContainingIgnoreCaseOrderByFechaenvioDesc(
                moduloFiltro,
                estadoFiltro);
    }

    private String limpiar(String valor) {
        return StringUtils.hasText(valor) ? valor.trim() : "";
    }
}
