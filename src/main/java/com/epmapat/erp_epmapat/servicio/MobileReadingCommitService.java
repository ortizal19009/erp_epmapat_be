package com.epmapat.erp_epmapat.servicio;

import com.epmapat.erp_epmapat.DTO.LecturaUploadItemDto;
import com.epmapat.erp_epmapat.modelo.*;
import com.epmapat.erp_epmapat.repositorio.MobileReadingReceiptR;
import com.fasterxml.jackson.databind.*;
import javax.persistence.EntityManager;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MobileReadingCommitService {
    private final EntityManager em;
    private final MobileReadingReceiptR receipts;
    private final LecturaServicio readings;
    private final ObjectMapper mapper;
    private final LecturaFotoStorageService photos;
    public MobileReadingCommitService(EntityManager em, MobileReadingReceiptR receipts,
            LecturaServicio readings, ObjectMapper mapper, LecturaFotoStorageService photos) {
        this.photos = photos; this.em = em; this.receipts = receipts; this.readings = readings;
        this.mapper = mapper.copy().configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
    }

    @Transactional
    public void commit(LecturaUploadItemDto item, Lecturas reading, Long userId, String note, String type) {
        String operation = item.getOperationId();
        String hash = null;
        if (operation != null) {
            try {
                if (!UUID.fromString(operation).toString().equalsIgnoreCase(operation)) throw new IllegalArgumentException();
            } catch (IllegalArgumentException ex) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "INVALID_OPERATION_ID");
            }
            operation = UUID.fromString(operation).toString();
            lock("mobile-operation:" + operation);
            hash = hash(item, userId, note, type);
            MobileReadingReceipt receipt = receipts.findById(operation).orElse(null);
            if (receipt != null) {
                if (!"READING".equals(receipt.getOperationKind()) || !java.util.Objects.equals(userId, receipt.getReaderId()) || !hash.equals(receipt.getPayloadHash()))
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "OPERATION_PAYLOAD_CONFLICT");
                return;
            }
        }
        lock("mobile-reading:" + item.getIdlectura());
        readings.actualizarLecturaConAuditoriaAtomica(item.getIdlectura(), reading, userId, note, type);
        if (operation != null) {
            MobileReadingReceipt receipt = new MobileReadingReceipt();
            receipt.setOperationId(operation); receipt.setReaderId(userId);
            receipt.setReadingId(item.getIdlectura()); receipt.setPayloadHash(hash);
            receipt.setConfirmedAt(java.time.Instant.now()); receipts.saveAndFlush(receipt);
        }
    }

    @Transactional(rollbackFor = java.io.IOException.class)
    public String commitPhoto(String operation, Long readingId, org.springframework.web.multipart.MultipartFile file,
            String folder, Long user, String note, String type) throws java.io.IOException {
        try { if (!UUID.fromString(operation).toString().equalsIgnoreCase(operation)) throw new IllegalArgumentException(); }
        catch (IllegalArgumentException ex) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "INVALID_OPERATION_ID"); }
        operation = UUID.fromString(operation).toString();
        String contentHash;
        try { contentHash = java.util.HexFormat.of().formatHex(java.security.MessageDigest.getInstance("SHA-256").digest(file.getBytes())); }
        catch (java.security.NoSuchAlgorithmException ex) { throw new IllegalStateException(ex); }
        String payloadHash = contentHash + ":" + readingId + ":" + user;
        lock("mobile-operation:" + operation);
        MobileReadingReceipt existing = receipts.findById(operation).orElse(null);
        if (existing != null) {
            if (!"PHOTO".equals(existing.getOperationKind()) || !java.util.Objects.equals(existing.getReaderId(), user)
                    || !payloadHash.equals(existing.getPayloadHash()))
                throw new ResponseStatusException(HttpStatus.CONFLICT, "OPERATION_PAYLOAD_CONFLICT");
            return existing.getResultPath();
        }
        lock("mobile-reading:" + readingId);
        String path = photos.saveIdempotent(readingId, file, folder, operation + "_" + contentHash);
        if (path == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "EMPTY_PHOTO");
        readings.actualizarFotoConAuditoriaAtomica(readingId, path, user, note, type);
        MobileReadingReceipt receipt = new MobileReadingReceipt();
        receipt.setOperationId(operation); receipt.setOperationKind("PHOTO"); receipt.setReaderId(user);
        receipt.setReadingId(readingId); receipt.setPayloadHash(payloadHash); receipt.setResultPath(path);
        receipt.setConfirmedAt(java.time.Instant.now()); receipts.saveAndFlush(receipt);
        return path;
    }

    private void lock(String key) {
        em.createNativeQuery("SELECT 1 FROM pg_advisory_xact_lock(hashtextextended(:key, 0))")
            .setParameter("key", key).getSingleResult();
    }
    private String hash(LecturaUploadItemDto item, Long user, String note, String type) {
        try {
            com.fasterxml.jackson.databind.node.ObjectNode payload = mapper.valueToTree(item);
            payload.remove("operationId");
            byte[] bytes = mapper.writeValueAsBytes(java.util.Arrays.asList(payload, user, note, type));
            return java.util.HexFormat.of().formatHex(java.security.MessageDigest.getInstance("SHA-256").digest(bytes));
        } catch (Exception ex) { throw new IllegalStateException("No se pudo identificar la operacion", ex); }
    }
}
