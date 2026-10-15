package com.epmapat.erp_epmapat.rrhh.servicio;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.security.MessageDigest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import com.epmapat.erp_epmapat.rrhh.modelo.ThEmployeeFile;
import com.epmapat.erp_epmapat.rrhh.repositorio.PersonalR;
import com.epmapat.erp_epmapat.rrhh.repositorio.ThEmployeeFileR;
import com.epmapat.erp_epmapat.servicio.administracion.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service @RequiredArgsConstructor @Slf4j
public class ThEmployeeFileServicio {
    private final ThEmployeeFileR dao;
    private final PersonalR personalR;
    private final ThAuditServicio audit;
    private final StorageService storage;
    private static final long MAX_BYTES = 10 * 1024 * 1024;
    private static final Map<String,String> TYPES = Map.of(
        "pdf","application/pdf", "jpg","image/jpeg", "jpeg","image/jpeg", "png","image/png",
        "docx","application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "xlsx","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

    private void invalid(String message) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST,message); }
    private void max(String value, int length, String field) {
        if (value != null && value.length() > length) invalid(field + " supera " + length + " caracteres");
    }
    private void validate(ThEmployeeFile f) {
        if (f == null || f.getIdpersonal_personal() == null || f.getIdpersonal_personal().getIdpersonal() == null)
            invalid("Seleccione un empleado");
        if (f.getIdfile() != null) invalid("El registro de documentos no permite sobrescribir otro documento");
        if (f.getTipo_doc() == null || f.getTipo_doc().isBlank()) invalid("Seleccione una categoría");
        if (f.getNombre_archivo() == null || f.getNombre_archivo().isBlank()) invalid("Indique el nombre del documento");
        max(f.getTipo_doc(),50,"Categoría"); max(f.getNombre_archivo(),255,"Nombre");
        max(f.getDescripcion(),2000,"Observaciones"); max(f.getNumero_documento(),120,"Número");
        max(f.getEmisor(),150,"Emisor");
        if (f.getVersion_doc() != null && (f.getVersion_doc() < 1 || f.getVersion_doc() > 9999)) invalid("La versión debe estar entre 1 y 9999");
        if (f.getFecha_emision() != null && f.getFecha_vencimiento() != null
            && f.getFecha_vencimiento().isBefore(f.getFecha_emision())) invalid("El vencimiento no puede ser anterior a la emisión");
        Long id = f.getIdpersonal_personal().getIdpersonal();
        f.setIdpersonal_personal(personalR.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Empleado no encontrado")));
    }
    private ThEmployeeFile persist(ThEmployeeFile f, Long actor) {
        f.setVersion_doc(f.getVersion_doc() == null ? 1 : f.getVersion_doc());
        f.setEstado("ACTIVO"); f.setFeccrea(LocalDate.now()); f.setUsucrea(actor);
        f.setFecmodi(null); f.setUsumodi(null);
        ThEmployeeFile saved = dao.saveAndFlush(f);
        audit.log("TH_EMPLOYEE_FILE",saved.getIdfile(),"CREATE",saved.getTipo_doc()+" - "+saved.getNombre_archivo(),actor);
        return saved;
    }

    @Transactional
    public ThEmployeeFile save(ThEmployeeFile f, Long actor) {
        validate(f);
        if (f.getRuta_archivo() == null || f.getRuta_archivo().isBlank()) invalid("Indique la ruta o enlace del documento");
        String path = f.getRuta_archivo().trim();
        max(path,500,"Ruta"); max(f.getHash_archivo(),128,"Hash");
        if (path.matches("(?is)^(javascript|data|file|vbscript):.*")) invalid("Tipo de enlace no permitido");
        f.setRuta_archivo(path); f.setOrigen("REFERENCIA");
        f.setNombre_original(null); f.setContent_type(null); f.setTamano_bytes(null);
        return persist(f,actor);
    }

    @Transactional
    public ThEmployeeFile upload(ThEmployeeFile f, MultipartFile file, Long actor) throws Exception {
        validate(f);
        if (file == null || file.isEmpty()) invalid("Seleccione un archivo");
        if (file.getSize() > MAX_BYTES) invalid("El archivo supera los 10 MB");
        String name = file.getOriginalFilename() == null ? "" : file.getOriginalFilename().replace('\\','/');
        name = name.substring(name.lastIndexOf('/')+1).replaceAll("[\\r\\n\\u0000]","");
        max(name,255,"Nombre original");
        String extension = name.substring(name.lastIndexOf('.')+1).toLowerCase(Locale.ROOT);
        if (!TYPES.containsKey(extension)) invalid("Use PDF, JPG, PNG, DOCX o XLSX");
        byte[] bytes = file.getBytes();
        boolean pdf = extension.equals("pdf") && bytes.length >= 5 && new String(bytes,0,5,java.nio.charset.StandardCharsets.US_ASCII).equals("%PDF-");
        boolean png = extension.equals("png") && bytes.length >= 8 && bytes[0] == (byte)137 && bytes[1] == 80 && bytes[2] == 78 && bytes[3] == 71;
        boolean jpg = (extension.equals("jpg") || extension.equals("jpeg")) && bytes.length >= 3 && bytes[0] == (byte)255 && bytes[1] == (byte)216 && bytes[2] == (byte)255;
        boolean office = (extension.equals("docx") || extension.equals("xlsx")) && bytes.length >= 4 && bytes[0] == 80 && bytes[1] == 75 && bytes[2] == 3 && bytes[3] == 4;
        if (!pdf && !png && !jpg && !office) invalid("El contenido no corresponde al tipo de archivo seleccionado");
        String folder = "RRHH/EXPEDIENTES/" + f.getIdpersonal_personal().getIdpersonal() + "/" + UUID.randomUUID();
        String path = storage.store(file,folder);
        if (path == null || !path.startsWith(folder + "/")) throw new IllegalStateException("El almacenamiento no devolvió una ruta válida");
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override public void afterCompletion(int status) {
                    if (status == STATUS_ROLLED_BACK) cleanup(path);
                }
            });
        }
        try {
            f.setRuta_archivo(path); f.setOrigen("ARCHIVO"); f.setNombre_original(name);
            f.setContent_type(TYPES.get(extension)); f.setTamano_bytes(file.getSize());
            f.setHash_archivo(java.util.HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes)));
            return persist(f,actor);
        } catch (RuntimeException ex) {
            if (!TransactionSynchronizationManager.isSynchronizationActive()) cleanup(path);
            throw ex;
        }
    }

    private void cleanup(String path) {
        try { storage.delete(path); } catch (Exception ex) { log.warn("No se pudo limpiar un adjunto tras revertir su registro: {}",path); }
    }

    @Transactional(readOnly = true)
    public List<ThEmployeeFile> byPersonal(Long idpersonal) { return dao.findByPersonal(idpersonal); }

    @Transactional(readOnly = true)
    public ThEmployeeFile find(Long id) {
        return dao.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Documento no encontrado"));
    }

    public Resource download(ThEmployeeFile f) throws Exception {
        String prefix = "RRHH/EXPEDIENTES/" + f.getIdpersonal_personal().getIdpersonal() + "/";
        if (!"ARCHIVO".equals(f.getOrigen()) || f.getRuta_archivo() == null
            || !f.getRuta_archivo().startsWith(prefix) || f.getRuta_archivo().contains(".."))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Este registro es una referencia, no un archivo adjunto");
        return storage.load(f.getRuta_archivo());
    }

    @Transactional
    public ThEmployeeFile state(Long id, String estado, Long actor) {
        if (!"ACTIVO".equals(estado) && !"ARCHIVADO".equals(estado)) invalid("Estado no permitido");
        ThEmployeeFile f = dao.findForUpdate(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Documento no encontrado"));
        if (estado.equals(f.getEstado())) return f;
        f.setEstado(estado); f.setUsumodi(actor); f.setFecmodi(LocalDate.now());
        audit.log("TH_EMPLOYEE_FILE",id,estado,"Cambio de estado del documento",actor);
        return dao.saveAndFlush(f);
    }
}

