package com.epmapat.erp_epmapat.sri.services;

import com.epmapat.erp_epmapat.emails.model.EmailAccount;
import com.epmapat.erp_epmapat.emails.model.EmailAccountSecurityType;
import com.epmapat.erp_epmapat.emails.model.EmailAccountTransportType;
import com.epmapat.erp_epmapat.emails.model.EmailType;
import com.epmapat.erp_epmapat.emails.service.EmailAccountService;
import com.epmapat.erp_epmapat.emails.service.EmailBlacklistService;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.AuthenticationFailedException;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.File;
import java.util.List;
import java.util.Properties;

@Service
public class EmailService {
    private final EmailBlacklistService blacklistService;
    private final EmailAccountService emailAccountService;

    public EmailService(EmailBlacklistService blacklistService, EmailAccountService emailAccountService) {
        this.blacklistService = blacklistService;
        this.emailAccountService = emailAccountService;
    }

    public boolean envioEmail(final String emisor, final String password, List<String> receptores,
            String asunto, String mensajeHtml, MultipartFile file) {
        return envioEmail(emisor, password, receptores, asunto, mensajeHtml,
                file == null ? List.of() : List.of(file));
    }

    public boolean envioEmail(final String emisor, final String password, List<String> receptores,
            String asunto, String mensajeHtml, List<? extends MultipartFile> files) {
        boolean envioExitoso = true;
        blacklistService.validateRecipients(receptores);

        try {
            EmailAccount account = emailAccountService.resolveAccount(null, EmailType.DOC_ELECTRONICO);
            if (account.getTransportType() != EmailAccountTransportType.SMTP) {
                throw new IllegalStateException("La cuenta configurada para documentos electronicos no usa SMTP");
            }

            JavaMailSenderImpl sender = buildSender(account);
            MimeMessage message = sender.createMimeMessage();

            message.setFrom(new InternetAddress(account.getFromAddress()));
            if (account.getReplyTo() != null && !account.getReplyTo().isBlank()) {
                message.setReplyTo(new InternetAddress[] { new InternetAddress(account.getReplyTo()) });
            }

            InternetAddress[] destinos = new InternetAddress[receptores.size()];
            for (int i = 0; i < receptores.size(); i++) {
                destinos[i] = new InternetAddress(receptores.get(i));
            }
            message.addRecipients(Message.RecipientType.TO, destinos);
            message.setSubject(asunto, "UTF-8");

            MimeBodyPart contenidoHtml = new MimeBodyPart();
            contenidoHtml.setContent(mensajeHtml, "text/html; charset=utf-8");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(contenidoHtml);

            if (files != null) {
                for (MultipartFile file : files) {
                    if (file == null || file.isEmpty()) {
                        continue;
                    }
                    MimeBodyPart adjunto = new MimeBodyPart();
                    adjunto.setFileName(file.getOriginalFilename());
                    adjunto.setDataHandler(new DataHandler(new ByteArrayDataSource(file.getBytes(), file.getContentType())));
                    multipart.addBodyPart(adjunto);
                }
            }

            message.setContent(multipart);
            sender.send(message);
        } catch (Exception e) {
            System.err.println("Error en envio de correo: " + e.getMessage());
            envioExitoso = false;

            if (e instanceof AuthenticationFailedException) {
                System.err.println("Error de autenticacion con el servidor SMTP");
            } else if (e instanceof SendFailedException) {
                System.err.println("Error al enviar a uno o mas destinatarios");
            }
        }

        return envioExitoso;
    }

    public boolean EEenvioEmail(final String emisor, final String password, List<String> receptores,
            String asunto, String mensajeHtml) {
        return envioEmail(emisor, password, receptores, asunto, mensajeHtml, (MultipartFile) null);
    }

    public boolean __envioArchivo(final String emisor, final String password, List<String> receptores, String asunto,
            List<String> adjuntos, final String domiCorreo) {
        boolean envioExitoso = true;
        blacklistService.validateRecipients(receptores);
        Properties props = new Properties();

        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", domiCorreo);
        props.setProperty("mail.smtp.port", "465");
        props.setProperty("mail.smtp.auth", "true");
        props.setProperty("mail.smtp.user", emisor);
        props.setProperty("mail.smtp.password", password);
        props.put("mail.smtp.ssl.trust", domiCorreo);
        props.put("mail.smtp.starttls.enable", "true");

        try {
            Session session = Session.getDefaultInstance(props);

            BodyPart texto = new MimeBodyPart();
            texto.setText(asunto);

            MimeMessage message = new MimeMessage(session);
            InternetAddress[] dest = new InternetAddress[receptores.size()];
            for (int i = 0; i <= dest.length - 1; i++) {
                dest[i] = new InternetAddress(receptores.get(i));
            }
            message.setFrom(new InternetAddress(emisor));
            InternetAddress[] replyTo = new InternetAddress[1];
            replyTo[0] = new InternetAddress(emisor);
            message.setReplyTo(replyTo);
            message.addRecipients(Message.RecipientType.TO, dest);
            message.setSubject(asunto);

            BodyPart adjunto = new MimeBodyPart();
            Multipart multipart = new MimeMultipart();

            if (adjuntos != null && adjuntos.size() > 0) {
                for (String rutaAdjunto : adjuntos) {
                    adjunto = new MimeBodyPart();
                    File f = new File(rutaAdjunto);
                    if (f.exists()) {
                        DataSource source = new FileDataSource(rutaAdjunto);
                        adjunto.setDataHandler(new DataHandler(source));
                        adjunto.setFileName(f.getName());
                        multipart.addBodyPart(texto);
                        multipart.addBodyPart(adjunto);
                    }
                }
            }

            message.setContent(multipart);

            Transport transport = session.getTransport("smtp");
            transport.connect(domiCorreo, emisor, password);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        } catch (Exception e) {
            e.printStackTrace();
            envioExitoso = false;
        } finally {
            if (adjuntos != null && adjuntos.size() < 0) {
                for (String rutaAdjunto : adjuntos) {
                    try {
                        File arch = new File(rutaAdjunto);
                        arch.delete();
                    } catch (Exception e2) {
                        e2.getMessage();
                    }
                }
            }
        }

        return envioExitoso;
    }

    private JavaMailSenderImpl buildSender(EmailAccount account) {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(account.getHost());
        sender.setPort(account.getPort());
        sender.setProtocol(account.getProtocol());

        if (account.getUsername() != null && !account.getUsername().isBlank()) {
            sender.setUsername(account.getUsername());
        }
        if (account.getPassword() != null && !account.getPassword().isBlank()) {
            sender.setPassword(account.getPassword());
        }

        EmailAccountSecurityType securityType = resolveSecurityType(account);
        Properties props = sender.getJavaMailProperties();
        props.put("mail.transport.protocol", account.getProtocol());
        props.put("mail.smtp.auth", Boolean.toString(account.isAuthRequired()));
        props.put("mail.smtp.starttls.enable", Boolean.toString(securityType == EmailAccountSecurityType.STARTTLS));
        props.put("mail.smtp.starttls.required", Boolean.toString(securityType == EmailAccountSecurityType.STARTTLS));
        props.put("mail.smtp.ssl.enable", Boolean.toString(securityType == EmailAccountSecurityType.SSL_TLS));
        props.put("mail.smtp.ssl.trust", account.getHost());
        if (account.getUsername() != null && !account.getUsername().isBlank()) {
            props.put("mail.smtp.user", account.getUsername());
        }
        if (account.getPassword() != null && !account.getPassword().isBlank()) {
            props.put("mail.smtp.password", account.getPassword());
        }
        if (securityType == EmailAccountSecurityType.SSL_TLS) {
            props.put("mail.smtp.socketFactory.port", Integer.toString(account.getPort()));
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.socketFactory.fallback", "false");
        }
        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.smtp.timeout", "10000");
        props.put("mail.smtp.writetimeout", "10000");
        return sender;
    }

    private EmailAccountSecurityType resolveSecurityType(EmailAccount account) {
        if (account.getSecurityType() == EmailAccountSecurityType.STARTTLS
                && Integer.valueOf(465).equals(account.getPort())) {
            return EmailAccountSecurityType.SSL_TLS;
        }
        return account.getSecurityType() == null ? EmailAccountSecurityType.STARTTLS : account.getSecurityType();
    }
}
