package com.schoolfinance.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromAddress;

    @Value("${app.frontend.url}")
    private String frontendUrl;


    public void sendInvitation(
            String to,
            String firstName,
            String username,
            UUID token
    ) {

        String link =
                frontendUrl + "/activate-account/" + token;

        String html =
                "<div style=\"font-family: Arial, sans-serif; max-width: 480px;\">"
                + "<h2>Bienvenue sur School Finance</h2>"
                + "<p>Bonjour " + firstName + ",</p>"
                + "<p>Un compte a ete cree pour vous avec l'identifiant : <strong>" + username + "</strong></p>"
                + "<p>Cliquez sur le bouton ci-dessous pour activer votre compte et definir votre mot de passe :</p>"
                + "<p style=\"margin: 24px 0;\">"
                + "<a href=\"" + link + "\" style=\"background:#1e293b;color:#fff;padding:12px 24px;border-radius:8px;text-decoration:none;\">"
                + "Activer mon compte"
                + "</a>"
                + "</p>"
                + "<p style=\"color:#6b7280;font-size:13px;\">Ce lien est valable 72 heures. Si vous n'etes pas a l'origine de cette demande, ignorez cet email.</p>"
                + "</div>";

        try {

            MimeMessage message =
                    mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(
                            message,
                            true,
                            "UTF-8"
                    );

            helper.setTo(to);
            helper.setFrom(fromAddress);
            helper.setSubject("Activation de votre compte School Finance");
            helper.setText(html, true);

            mailSender.send(message);

        }
        catch (MessagingException e) {

            throw new RuntimeException(
                    "Erreur lors de l'envoi de l'email d'invitation",
                    e
            );
        }
    }
}