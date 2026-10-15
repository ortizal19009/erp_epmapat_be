package com.epmapat.erp_epmapat.rrhh;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import com.epmapat.erp_epmapat.rrhh.modelo.*;
import com.epmapat.erp_epmapat.rrhh.repositorio.*;
import com.epmapat.erp_epmapat.rrhh.servicio.*;
import com.epmapat.erp_epmapat.servicio.administracion.StorageService;

class ThEmployeeFileServicioTest {
    ThEmployeeFileR dao=mock(ThEmployeeFileR.class);
    PersonalR personal=mock(PersonalR.class);
    ThAuditServicio audit=mock(ThAuditServicio.class);
    StorageService storage=mock(StorageService.class);
    ThEmployeeFileServicio service=new ThEmployeeFileServicio(dao,personal,audit,storage);
    @BeforeEach void setup() throws Exception {
        Personal p=new Personal(); p.setIdpersonal(2L); when(personal.findById(2L)).thenReturn(Optional.of(p));
        when(dao.saveAndFlush(any())).thenAnswer(i -> { ThEmployeeFile f=i.getArgument(0); f.setIdfile(3L); return f; });
        when(storage.store(any(),anyString())).thenAnswer(i -> i.getArgument(1)+"/unique.pdf");
    }
    ThEmployeeFile metadata() {
        Personal p=new Personal(); p.setIdpersonal(2L);
        ThEmployeeFile f=new ThEmployeeFile(); f.setIdpersonal_personal(p); f.setNombre_archivo("Contrato"); f.setTipo_doc("CONTRATO"); f.setRuta_archivo("https://ejemplo.test/documento"); return f;
    }
    MockMultipartFile pdf() { return new MockMultipartFile("file","contrato.pdf","application/pdf","%PDF-1.7 prueba".getBytes()); }
    @Test void referenceUsesServerActorAndRejectsForgedUploadMetadata() {
        var f=metadata(); f.setOrigen("ARCHIVO"); f.setUsucrea(1L); f.setEstado("ARCHIVADO");
        var result=service.save(f,8L);
        assertEquals("REFERENCIA",result.getOrigen()); assertEquals(8L,result.getUsucrea()); assertEquals("ACTIVO",result.getEstado());
        assertNull(result.getNombre_original()); verifyNoInteractions(storage);
        verify(audit).log(eq("TH_EMPLOYEE_FILE"),eq(3L),eq("CREATE"),anyString(),eq(8L));
    }
    @Test void uploadRecordsHashSizeTypeAndEmployeeFolder() throws Exception {
        var result=service.upload(metadata(),pdf(),8L);
        assertEquals("ARCHIVO",result.getOrigen()); assertEquals(64,result.getHash_archivo().length());
        assertEquals("application/pdf",result.getContent_type()); assertEquals(pdf().getSize(),result.getTamano_bytes());
        assertTrue(result.getRuta_archivo().startsWith("RRHH/EXPEDIENTES/2/"));
        verify(storage).store(any(),startsWith("RRHH/EXPEDIENTES/2/")); verify(storage,never()).delete(anyString());
    }
    @Test void validatesFileContentAndSizeBeforeStorage() {
        var fake=new MockMultipartFile("file","contrato.pdf","application/pdf","esto no es pdf".getBytes());
        assertThrows(ResponseStatusException.class,() -> service.upload(metadata(),fake,8L));
        MultipartFile huge=mock(MultipartFile.class); when(huge.getSize()).thenReturn(11*1024*1024L);
        assertThrows(ResponseStatusException.class,() -> service.upload(metadata(),huge,8L));
        verifyNoInteractions(storage,dao);
    }
    @Test void invalidDatesVersionsAndMissingEmployeeNeverWrite() {
        var dates=metadata(); dates.setFecha_emision(LocalDate.of(2026,10,15)); dates.setFecha_vencimiento(LocalDate.of(2026,10,1));
        assertThrows(ResponseStatusException.class,() -> service.save(dates,8L));
        var version=metadata(); version.setVersion_doc(0); assertThrows(ResponseStatusException.class,() -> service.save(version,8L));
        var missing=metadata(); missing.getIdpersonal_personal().setIdpersonal(999L); assertThrows(ResponseStatusException.class,() -> service.save(missing,8L));
        verifyNoInteractions(storage,dao,audit);
    }
    @Test void forbidsOverwritingAndActiveLinks() {
        var overwrite=metadata(); overwrite.setIdfile(44L); assertThrows(ResponseStatusException.class,() -> service.save(overwrite,8L));
        var script=metadata(); script.setRuta_archivo("javascript:alert(1)"); assertThrows(ResponseStatusException.class,() -> service.save(script,8L));
        verifyNoInteractions(dao,storage);
    }
    @Test void removesOnlyNewUploadWhenPersistenceFails() throws Exception {
        doThrow(new IllegalStateException("DB failed")).when(dao).saveAndFlush(any());
        assertThrows(IllegalStateException.class,() -> service.upload(metadata(),pdf(),8L));
        verify(storage).delete(startsWith("RRHH/EXPEDIENTES/2/"));
    }
    @Test void rollbackAfterMethodReturnsAlsoCleansUpload() throws Exception {
        TransactionSynchronizationManager.initSynchronization();
        try {
            service.upload(metadata(),pdf(),8L);
            var callbacks=TransactionSynchronizationManager.getSynchronizations(); assertEquals(1,callbacks.size());
            callbacks.forEach(s -> s.afterCompletion(TransactionSynchronization.STATUS_ROLLED_BACK));
            verify(storage).delete(startsWith("RRHH/EXPEDIENTES/2/"));
        } finally { TransactionSynchronizationManager.clearSynchronization(); }
    }
    @Test void archivesWithoutDeletingAndRejectsArbitraryDownloadPaths() {
        var f=metadata(); f.setIdfile(3L); f.setEstado("ACTIVO"); when(dao.findForUpdate(3L)).thenReturn(Optional.of(f));
        assertEquals("ARCHIVADO",service.state(3L,"ARCHIVADO",8L).getEstado());
        verifyNoInteractions(storage);
        f.setOrigen("ARCHIVO"); f.setRuta_archivo("../../secret");
        assertThrows(ResponseStatusException.class,() -> service.download(f)); verifyNoInteractions(storage);
    }
}
