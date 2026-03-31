package com.epmapat.erp_epmapat.emails.controller;

import com.epmapat.erp_epmapat.emails.component.EmailSpecs;
import com.epmapat.erp_epmapat.emails.dtos.EmailResponse;
import com.epmapat.erp_epmapat.emails.dtos.SendEmailRequest;
import com.epmapat.erp_epmapat.emails.model.EmailMessage;
import com.epmapat.erp_epmapat.emails.model.EmailStatus;
import com.epmapat.erp_epmapat.emails.model.EmailType;
import com.epmapat.erp_epmapat.emails.repository.EmailMessageR;
import com.epmapat.erp_epmapat.emails.service.EmailComposerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.OffsetDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/emails")
public class EmailController {

    private final EmailComposerService composer;
    private final EmailMessageR emailRepo;

    public EmailController(EmailComposerService composer, EmailMessageR emailRepo) {
        this.composer = composer;
        this.emailRepo = emailRepo;
    }

    // 1) Documentos (XML/PDF)
    @PostMapping("/documents")
    public ResponseEntity<?> sendDocuments(@Valid @RequestBody SendEmailRequest req) {
        return enqueue(EmailType.DOC_ELECTRONICO, req);
    }

    // 2) Notificaciones
    @PostMapping("/notifications")
    public ResponseEntity<?> sendNotifications(@Valid @RequestBody SendEmailRequest req) {
        return enqueue(EmailType.NOTIFICACION, req);
    }

    // 3) Custom
    @PostMapping("/custom")
    public ResponseEntity<?> sendCustom(@Valid @RequestBody SendEmailRequest req) {
        return enqueue(EmailType.CUSTOM, req);
    }

    // Admin: detalle
    @GetMapping("/{id}")
    public ResponseEntity<EmailResponse> getOne(@PathVariable UUID id) {
        return emailRepo.findById(id)
                .map(e -> ResponseEntity.ok(toResponse(e)))
                .orElse(ResponseEntity.notFound().build());
    }

    // Admin: listar simple (puedes mejorar con Specifications)
    @GetMapping
    public Page<EmailResponse> list(
            @RequestParam(required = false) EmailStatus status,
            @RequestParam(required = false) EmailType type,
            @RequestParam(required = false) String correlationId,
            @RequestParam(required = false) Long accountId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        var spec = EmailSpecs.filter(status, type, correlationId, accountId);
        return emailRepo.findAll(spec, pageable).map(this::toResponse);
    }

    @GetMapping("/pending/stale")
    public Page<EmailResponse> listStalePending(
            @RequestParam(defaultValue = "30") int olderThanMinutes,
            @RequestParam(required = false) Integer minAttempts,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        OffsetDateTime createdBefore = OffsetDateTime.now().minusMinutes(Math.max(olderThanMinutes, 0));
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdAt"));
        var spec = EmailSpecs.pendingStale(createdBefore, minAttempts);
        return emailRepo.findAll(spec, pageable).map(this::toResponse);
    }

    // Admin: retry (si está FAILED o PENDING con intentos < max)
    @PostMapping("/{id}/retry")
    public ResponseEntity<?> retry(@PathVariable UUID id) {
        return emailRepo.findById(id).map(e -> {
            e.setStatus(EmailStatus.PENDING);
            e.setLastError(null);
            emailRepo.save(e);
            return ResponseEntity.accepted().body(new IdResponse(id));
        }).orElse(ResponseEntity.notFound().build());
    }

    // Admin: cancel (solo si PENDING)
    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancel(@PathVariable UUID id) {
        return emailRepo.findById(id).map(e -> {
            if (e.getStatus() == EmailStatus.PENDING) {
                e.setStatus(EmailStatus.CANCELLED);
                emailRepo.save(e);
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.status(409).body("Solo se puede cancelar si está PENDING");
        }).orElse(ResponseEntity.notFound().build());
    }

    private EmailResponse toResponse(EmailMessage e) {
        EmailResponse r = new EmailResponse();
        r.id = e.getId();
        r.type = e.getType();
        r.status = e.getStatus();
        r.subject = e.getSubject();
        r.correlationId = e.getCorrelationId();
        if (e.getAccount() != null) {
            r.accountId = e.getAccount().getId();
            r.accountCode = e.getAccount().getCode();
            r.accountName = e.getAccount().getName();
        }
        r.fromAddress = e.getFromAddress();
        r.attempts = e.getAttempts();
        r.lastError = e.getLastError();
        r.createdAt = e.getCreatedAt();
        r.sentAt = e.getSentAt();
        return r;
    }

    public record IdResponse(UUID id) {}

    private ResponseEntity<?> enqueue(EmailType type, SendEmailRequest req) {
        try {
            UUID id = composer.enqueue(type, req);
            return ResponseEntity.accepted().body(new IdResponse(id));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(409).body(ex.getMessage());
        }
    }
}
