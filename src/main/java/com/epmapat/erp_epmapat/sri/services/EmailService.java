package com.epmapat.erp_epmapat.sri.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmailWithAttachment(String to, String subject, String text, byte[] attachment, String attachmentName) 
            throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text);
        
        // Adjuntar el PDF
        helper.addAttachment(attachmentName, new ByteArrayResource(attachment));
        
        mailSender.send(message);
    }
}