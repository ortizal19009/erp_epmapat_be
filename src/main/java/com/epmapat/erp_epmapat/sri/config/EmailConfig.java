package com.epmapat.erp_epmapat.sri.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailConfig {

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com"); // Cambiar por tu servidor SMTP
        mailSender.setPort(587);
        mailSender.setUsername("ortizln9@gmail.com"); // Cambiar por tu email
        mailSender.setPassword("Roller@086411421"); // Cambiar por tu contraseña
        
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true"); // Solo para desarrollo
        
        return mailSender;
    }
}