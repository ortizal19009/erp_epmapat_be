package com.epmapat.erp_epmapat.emails.service;

import com.epmapat.erp_epmapat.emails.dtos.AttachmentInput;
import com.epmapat.erp_epmapat.emails.dtos.SendEmailRequest;
import com.epmapat.erp_epmapat.emails.interfaces.AttachmentStorage;
import com.epmapat.erp_epmapat.emails.model.EmailAttachment;
import com.epmapat.erp_epmapat.emails.model.EmailAccount;
import com.epmapat.erp_epmapat.emails.model.EmailMessage;
import com.epmapat.erp_epmapat.emails.model.EmailStatus;
import com.epmapat.erp_epmapat.emails.model.EmailType;
import com.epmapat.erp_epmapat.emails.repository.EmailAttachmentR;
import com.epmapat.erp_epmapat.emails.repository.EmailMessageR;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EmailComposerService {

    private final EmailMessageR emailRepo;
    private final EmailAttachmentR attachRepo;
    private final TemplateService templateService;
    private final AttachmentStorage storage;
    private final EmailAccountService accountService;
    private final EmailBlacklistService blacklistService;

    public EmailComposerService(EmailMessageR emailRepo,
                                EmailAttachmentR attachRepo,
                                TemplateService templateService,
                                AttachmentStorage storage,
                                EmailAccountService accountService,
                                EmailBlacklistService blacklistService) {
        this.emailRepo = emailRepo;
        this.attachRepo = attachRepo;
        this.templateService = templateService;
        this.storage = storage;
        this.accountService = accountService;
        this.blacklistService = blacklistService;
    }

    @Transactional
    public UUID enqueue(EmailType type, SendEmailRequest req) {
        blacklistService.validateRecipients(mergeRecipients(req.to, req.cc, req.bcc));
        EmailAccount account = accountService.resolveAccount(req.accountId, type);

        EmailMessage msg = new EmailMessage();
        msg.setType(type);
        msg.setStatus(EmailStatus.PENDING);
        msg.setToRecipients(join(req.to));
        msg.setCcRecipients(join(req.cc));
        msg.setBccRecipients(join(req.bcc));
        msg.setSubject(req.subject);
        msg.setCorrelationId(req.correlationId);
        msg.setAccount(account);
        msg.setFromAddress(account.getFromAddress());
        msg.setAttempts(0);

        // cuerpo
        String html = null;
        String text = req.text;

        if (req.templateKey != null && !req.templateKey.isBlank()) {
            html = templateService.render(req.templateKey, req.variables);
        } else if (req.html != null && !req.html.isBlank()) {
            html = req.html;
        }
        msg.setBodyHtml(html);
        msg.setBodyText(text);

        EmailMessage saved = emailRepo.save(msg);

        // adjuntos
        if (req.attachments != null) {
            for (AttachmentInput a : req.attachments) {
                byte[] bytes = Base64.getDecoder().decode(a.base64);
                var stored = storage.save(a.name, a.contentType, bytes);

                EmailAttachment att = new EmailAttachment();
                att.setEmail(saved);
                att.setFilename(a.name);
                att.setContentType(a.contentType);
                att.setSize(stored.size());
                att.setStorageRef(stored.storageRef());
                att.setSha256(stored.sha256());
                attachRepo.save(att);
            }
        }
        return saved.getId();
    }

    private String join(List<String> xs) {
        if (xs == null || xs.isEmpty()) return null;
        return xs.stream().map(String::trim).filter(s -> !s.isBlank()).collect(Collectors.joining(","));
    }

    @SafeVarargs
    private final List<String> mergeRecipients(List<String>... groups) {
        return java.util.Arrays.stream(groups)
                .filter(list -> list != null && !list.isEmpty())
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }
}
