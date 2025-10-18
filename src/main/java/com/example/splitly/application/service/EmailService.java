package com.example.splitly.application.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j // For professional logging
public class EmailService {
    @Autowired
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${spring.application.name}")
    private String appName;

    /**
     * Sends an OTP email asynchronously using an HTML template.
     *
     * @param to The recipient's email address.
     * @param otp The One-Time Password.
     * @param expiryMinutes The validity duration of the OTP in minutes.
     */
    @Async // Marks this method for asynchronous execution
    public void sendOtpEmail(String to, String otp, int expiryMinutes) {
        final String subject = "Your " + appName + " Verification Code";

        // Prepare the variables for the Thymeleaf template
        Context context = new Context();
        context.setVariable("appName", appName);
        context.setVariable("otpCode", otp);
        context.setVariable("expiryMinutes", expiryMinutes);

        try {
            sendHtmlEmail(to, subject, "otp-email", context);
        } catch (MessagingException e) {
            // Log the specific error for easier debugging
            log.error("Failed to send OTP email to {}: {}", to, e.getMessage());
            // In a production environment, you might want to add this failed email to a retry queue.
        }
    }

    /**
     * A generic helper method to send HTML emails.
     *
     * @param to The recipient's email address.
     * @param subject The email subject.
     * @param templateName The name of the Thymeleaf template file (without .html).
     * @param context The Thymeleaf context containing variables for the template.
     * @throws MessagingException if the email fails to send.
     */
    private void sendHtmlEmail(String to, String subject, String templateName, Context context) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

        // Process the HTML template with the given context
        String htmlContent = templateEngine.process(templateName, context);

        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true); // true = this is an HTML email

        mailSender.send(mimeMessage);
        log.info("Successfully sent HTML email to {}", to);
    }
}