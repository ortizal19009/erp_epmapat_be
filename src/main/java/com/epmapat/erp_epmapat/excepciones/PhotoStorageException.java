package com.epmapat.erp_epmapat.excepciones;

import java.io.IOException;
import com.github.sardine.impl.SardineException;

/** Reports storage failures without exposing WebDAV bodies or credentials. */
public class PhotoStorageException extends IOException {
    private final String code;
    private final Integer upstreamStatus;
    private final boolean retryable;

    private PhotoStorageException(String code, Integer status, boolean retryable, Throwable cause) {
        super(code, cause); this.code=code; this.upstreamStatus=status; this.retryable=retryable;
    }
    public String getCode() { return code; }
    public Integer getUpstreamStatus() { return upstreamStatus; }
    public boolean isRetryable() { return retryable; }

    public static PhotoStorageException from(Throwable cause) {
        Throwable current=cause;
        for (int depth=0; current!=null && depth<20; depth++,current=current.getCause()) {
            if (current instanceof PhotoStorageException) return (PhotoStorageException)current;
            if (current instanceof SardineException) {
                int status=((SardineException)current).getStatusCode();
                String code=status==401 ? "PHOTO_STORAGE_AUTHENTICATION_FAILED"
                    : status==403 ? "PHOTO_STORAGE_PERMISSION_DENIED"
                    : status==507 ? "PHOTO_STORAGE_QUOTA_EXCEEDED"
                    : status==404 || status>=300 && status<400 ? "PHOTO_STORAGE_CONFIGURATION_ERROR"
                    : "PHOTO_STORAGE_UNAVAILABLE";
                return new PhotoStorageException(code,status,status!=507 && (status>=500 || status==408 || status==429),cause);
            }
        }
        return new PhotoStorageException("PHOTO_STORAGE_UNAVAILABLE",null,true,cause);
    }
}
