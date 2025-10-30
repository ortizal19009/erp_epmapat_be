package com.epmapat.erp_epmapat.sri.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import java.io.File;
import java.util.List;

import javax.activation.DataSource;
import javax.mail.AuthenticationFailedException;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.SendFailedException;
import javax.mail.Transport;

@Service
public class EmailService {
    public boolean envioEmail(final String emisor, final String password, List<String> receptores,
            String asunto, String mensajeHtml, MultipartFile file) {
        boolean envioExitoso = true;
        String domiCorreo = "smtp.cmaginet.net";
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", domiCorreo);
        props.setProperty("mail.smtp.port", "465");//465-26
        props.setProperty("mail.smtp.auth", "true");
        props.setProperty("mail.smtp.user", emisor);
        props.setProperty("mail.smtp.password", password);
        props.put("mail.smtp.ssl.trust", domiCorreo);
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.connectiontimeout", "5000");
        props.put("mail.smtp.timeout", "5000");
        props.put("mail.smtp.writetimeout", "5000");

        try {
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(emisor, password);
                }
            });

            MimeMessage message = new MimeMessage(session);

            // Remitente y destinatarios
            message.setFrom(new InternetAddress(emisor));
            InternetAddress[] destinos = new InternetAddress[receptores.size()];
            for (int i = 0; i < receptores.size(); i++) {
                destinos[i] = new InternetAddress(receptores.get(i));
            }
            message.addRecipients(Message.RecipientType.TO, destinos);
            message.setSubject(asunto, "UTF-8");

            // Cuerpo HTML
            MimeBodyPart contenidoHtml = new MimeBodyPart();
            contenidoHtml.setContent(mensajeHtml, "text/html; charset=utf-8");

            // Crear Multipart
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(contenidoHtml);

            // Si hay archivo, añadirlo
            if (file != null && !file.isEmpty()) {
                MimeBodyPart adjunto = new MimeBodyPart();
                adjunto.setFileName(file.getOriginalFilename());
                adjunto.setDataHandler(
                        new DataHandler(new ByteArrayDataSource(file.getBytes(), file.getContentType())));
                multipart.addBodyPart(adjunto);
            }

            // Asignar contenido al mensaje
            message.setContent(multipart);

            // Enviar
            Transport.send(message, emisor, password);

        } catch (Exception e) {
            System.err.println("Error en envío de correo: " + e.getMessage());
            envioExitoso = false;

            if (e instanceof AuthenticationFailedException) {
                System.err.println("Error de autenticación con el servidor SMTP");
            } else if (e instanceof SendFailedException) {
                System.err.println("Error al enviar a uno o más destinatarios");
            }
        }

        return envioExitoso;
    }

    public boolean EEenvioEmail(final String emisor, final String password, List<String> receptores,
            String asunto, String mensajeHtml) {
        boolean envioExitoso = true;
        String domiCorreo = "smtp.cmaginet.net";
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", domiCorreo);
        props.setProperty("mail.smtp.port", "465");
        props.setProperty("mail.smtp.auth", "true");
        props.setProperty("mail.smtp.user", emisor);
        props.setProperty("mail.smtp.password", password);
        props.put("mail.smtp.ssl.trust", domiCorreo);
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.enable", "true"); // <- Esta línea es clave

        // Configuración adicional para mejor rendimiento
        props.put("mail.smtp.connectiontimeout", "5000");
        props.put("mail.smtp.timeout", "5000");
        props.put("mail.smtp.writetimeout", "5000");

        try {
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(emisor, password);
                }
            });

            MimeMessage message = new MimeMessage(session);

            // Configurar remitente
            message.setFrom(new InternetAddress(emisor));
            InternetAddress[] replyTo = { new InternetAddress(emisor) };
            message.setReplyTo(replyTo);

            // Configurar destinatarios
            InternetAddress[] destinos = new InternetAddress[receptores.size()];
            for (int i = 0; i < receptores.size(); i++) {
                destinos[i] = new InternetAddress(receptores.get(i));
            }
            message.addRecipients(Message.RecipientType.TO, destinos);

            // Asunto del correo
            message.setSubject(asunto, "UTF-8");

            // Contenido del mensaje en HTML
            MimeBodyPart contenidoHtml = new MimeBodyPart();
            contenidoHtml.setContent(mensajeHtml, "text/html; charset=utf-8");

            // Crear multipart para el mensaje
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(contenidoHtml);

            // Asignar contenido al mensaje
            message.setContent(multipart);

            // Enviar el mensaje con un tiempo de espera
            Transport.send(message, emisor, password);

        } catch (Exception e) {
            System.err.println("Error en envío de correo: " + e.getMessage());
            envioExitoso = false;

            // Manejo específico de excepciones comunes
            if (e instanceof AuthenticationFailedException) {
                System.err.println("Error de autenticación con el servidor SMTP");
            } else if (e instanceof SendFailedException) {
                System.err.println("Error al enviar a uno o más destinatarios");
            }
        }

        return envioExitoso;
    }

    public boolean __envioArchivo(final String emisor, final String password, List<String> receptores, String asunto,
            List<String> adjuntos, final String domiCorreo) {
        boolean envioExitoso = true;
        Properties props = new Properties();

        // final String smtpUsername = "facturacion@emapasr.gob.ec";
        // final String smtpPassword = "(santarosa)fact#9";

        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", domiCorreo);// indica el protocolo y que servidor de correo va utilizar
        props.setProperty("mail.smtp.port", "465"); // inidica el puerto (por defecto 465)
        props.setProperty("mail.smtp.auth", "true");// indica la autenticacion en el servidor (por defecto true)
        props.setProperty("mail.smtp.user", emisor);
        props.setProperty("mail.smtp.password", password);

        props.put("mail.smtp.ssl.trust", domiCorreo);
        props.put("mail.smtp.starttls.enable", "true");

        try {

            Session session = Session.getDefaultInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(emisor, password);
                }
            });

            BodyPart texto = new MimeBodyPart();
            texto.setText(asunto);

            MimeMessage message = new MimeMessage(session);
            InternetAddress[] dest = new InternetAddress[receptores.size()];
            for (int i = 0; i <= dest.length - 1; i++) {
                dest[i] = new InternetAddress(receptores.get(i));
            }
            // Se define el emisor del email
            message.setFrom(new InternetAddress(emisor));
            InternetAddress[] replyTo = new InternetAddress[1];
            replyTo[0] = new InternetAddress(emisor);
            message.setReplyTo(replyTo);
            // Se definen a los destinatarios
            message.addRecipients(Message.RecipientType.TO, dest);
            // Se define el asunto del email
            message.setSubject(asunto);

            /***************************/
            BodyPart adjunto = new MimeBodyPart();
            Multipart multipart = new MimeMultipart();

            /*********************/

            // Se adjuntan los archivos al correo
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

            // Se junta el mensaje y los archivos adjuntos
            message.setContent(multipart);

            Transport transport = session.getTransport("smtp");
            transport.send(message);

            // transport.close();

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
}