package com.epmapat.erp_epmapat.servicio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.epmapat.erp_epmapat.excepciones.*;
import com.epmapat.erp_epmapat.servicio.administracion.StorageService;
import com.github.sardine.impl.SardineException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

class MobilePhotoStorageTest {
    @Test void upstreamForbiddenIsStorageRepairNotMobileLoginFailure() throws Exception {
        var storage=mock(StorageService.class);
        when(storage.storeIdempotent(any(),anyString(),anyString()))
            .thenThrow(new IllegalStateException("private WebDAV response",new SardineException("Forbidden",403,"Forbidden")));
        var service=new LecturaFotoStorageService(storage);
        var file=new MockMultipartFile("file","photo.jpg","image/jpeg",new byte[]{1});
        var error=assertThrows(PhotoStorageException.class,()->service.saveIdempotent(1L,file,"photos","key"));
        assertEquals("PHOTO_STORAGE_PERMISSION_DENIED",error.getCode());
        assertEquals(403,error.getUpstreamStatus()); assertFalse(error.isRetryable());
        var response=new MobilePhotoExceptionHandler().storageFailure(error);
        assertEquals(502,response.getStatusCodeValue());
        assertEquals(false,response.getBody().get("retryable"));
        assertFalse(response.getBody().toString().contains("private WebDAV"));
    }
    @Test void networkRetriesButQuotaAndCredentialsNeedRepair() {
        assertTrue(PhotoStorageException.from(new java.io.IOException("timeout")).isRetryable());
        assertEquals("PHOTO_STORAGE_AUTHENTICATION_FAILED",PhotoStorageException.from(new SardineException("Unauthorized",401,"Unauthorized")).getCode());
        assertFalse(PhotoStorageException.from(new SardineException("Quota",507,"Quota")).isRetryable());
        assertTrue(PhotoStorageException.from(new SardineException("Busy",503,"Busy")).isRetryable());
    }
}
