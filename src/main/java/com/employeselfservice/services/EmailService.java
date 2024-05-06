package com.employeselfservice.services.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

    public void sendWelcomeEmail(String firstName, String email, String password) {
        try {
            // Load HTML content from the .html file
            String htmlContent = loadHtmlFromTemplate("welcome_email.html");

            // Replace placeholders in the HTML content with dynamic details
            htmlContent = htmlContent.replace("[[FIRST_NAME]]", firstName);
            htmlContent = htmlContent.replace("[[EMAIL]]", email);
            htmlContent = htmlContent.replace("[[PASSWORD]]", password);

            // Create MimeMessageHelper instance with multipart set to true to support inline images
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            // Set email properties
            helper.setTo(email);
            helper.setSubject("Welcome to DRC, " + firstName);

            // Set the HTML content
            mimeMessage.setContent(htmlContent, "text/html; charset=utf-8");

            // Send the email
            javaMailSender.send(mimeMessage);
            System.out.println("Welcome email sent successfully to: " + email);
        } catch (MessagingException | IOException e) {
            System.out.println("Failed to send welcome email: " + e.getMessage());
        }
    }

    public void sendProjectAssignmentEmail(String projectName, Long userId, Long projectId, String addedBy, String email) {
        try {
            // Load HTML content from the .html file
            String htmlContent = loadHtmlFromTemplate("project_assignment_email.html");

            // Replace placeholders in the HTML content with dynamic details
            htmlContent = htmlContent.replace("[[PROJECT_NAME]]", projectName);
            htmlContent = htmlContent.replace("[[USER_ID]]", String.valueOf(userId));
            htmlContent = htmlContent.replace("[[PROJECT_ID]]", String.valueOf(projectId));
            htmlContent = htmlContent.replace("[[ADDED_BY]]", addedBy);

            // Create MimeMessageHelper instance with multipart set to true to support inline images
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            // Set email properties
            helper.setTo(email);
            helper.setSubject("Project Assign-Truflux");

            // Set the HTML content
            helper.setText(htmlContent, true);

            // Send the email
            javaMailSender.send(mimeMessage);
            System.out.println("Project Assignment email sent successfully to: " + userId);
        } catch (MessagingException | IOException e) {
            System.out.println("Failed to send project assignment email: " + e.getMessage());
        }
    }


    private String loadHtmlFromTemplate(String templateName) throws IOException {
        // Load HTML content from the .html file
        ClassPathResource resource = new ClassPathResource("HTML/" + templateName);
        byte[] fileBytes = resource.getInputStream().readAllBytes();
        return new String(fileBytes, StandardCharsets.UTF_8);
    }


}
