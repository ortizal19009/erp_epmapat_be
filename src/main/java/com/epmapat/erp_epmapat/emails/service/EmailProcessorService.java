package com.epmapat.erp_epmapat.emails.service;

import com.epmapat.erp_epmapat.emails.model.EmailMessage;
import com.epmapat.erp_epmapat.emails.model.EmailStatus;
import com.epmapat.erp_epmapat.emails.repository.EmailMessageR;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class EmailProcessorService {

    private final MailSenderService sender;
    private final EmailMessageR emailRepo;

    public EmailProcessorService(MailSenderService sender,
                                 EmailMessageR emailRepo) {
        this.sender = sender;
        this.emailRepo = emailRepo;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void procesarEmail(UUID emailId, int maxAttempts) {
        EmailMessage msg = emailRepo.lockById(emailId)
                .orElse(null);

        if (msg == null) {
            return;
        }

        if (msg.getStatus() != EmailStatus.PENDING || msg.getAttempts() >= maxAttempts) {
            return;
        }

        msg.setAttempts(msg.getAttempts() + 1);

        try {
            sender.sendNow(msg);

            msg.setStatus(EmailStatus.SENT);
            msg.setSentAt(OffsetDateTime.now());
            msg.setLastError(null);

        } catch (EmailBlacklistViolationException e) {

            msg.setLastError(e.getMessage());
            msg.setStatus(EmailStatus.FAILED);

        } catch (Exception e) {

            msg.setLastError(e.getMessage());

            if (msg.getAttempts() >= maxAttempts) {
                msg.setStatus(EmailStatus.FAILED);
            }
        }

        emailRepo.saveAndFlush(msg);
    }
}
