package com.example.gearfit.connections;
import com.example.gearfit.exceptions.EmailServiceException;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class EmailService {

    private final String username = "gearfitfindegrado@gmail.com";
    private final String password = "unwb bbla gkss fxwt"; // contraseña de aplicacion

    public void sendWelcomeEmail(String recipientEmail) {
        // Propiedades del correo
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        // Crear la sesión de correo
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // Crear el mensaje
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Bienvenido a GearFit");
            message.setText("Gracias por registrarte. ¡Esperamos que disfrutes de nuestros servicios!");

            // Enviar el correo
            Transport.send(message);
            // System.out.println("Correo enviado con éxito a " + recipientEmail);
        } catch (MessagingException e) {
            throw new EmailServiceException("Error al enviar el correo a " + recipientEmail, e);
        }
    }
}
