package com.employeselfservice.services.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendWelcomeEmail(String firstName, String email, String password) {
        try {

            // Create MimeMessageHelper instance with multipart set to true to support inline images
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            // Set email properties
            helper.setTo(email);
            helper.setSubject("Welcome to DRC, " + firstName);

            // Read the HTML template into a String variable
            String htmlContent = "<!DOCTYPE html>"
                    + "<html>"
                    + "<head>"
                    + "<style>"
                    + "body { font-family: Arial, sans-serif; }"
                    + "h2 { color: #0066cc; }"
                    + "ul { list-style-type: none; padding: 0; }"
                    + "li { margin-bottom: 5px; list-style:square; }"
                    + "img { max-width: 200px; }"
                    + "svg { fill: #3C91E6; }"
                    + "</style>"
                    + "</head>"
                    + "<body>"
                    + "<h2>Welcome to DRC Systems</h2>"
                    + "<p>Dear " + firstName + ",</p>"
                    + "<p>You have been successfully added as an employee.</p>"
                    + "<p>Your login details:</p>"
                    + "<ul>"
                    + "<li><strong>Email:</strong> " + email + "</li>"
                    + "<li><strong>Password:</strong> " + password + "</li>"
                    + "</ul>"
                    + "<p>Thank you,<br/>Admin Team</p>"
                    + "</body>"
                    + "</html>";

            // Set the HTML content and inline images
            mimeMessage.setContent(htmlContent, "text/html; charset=utf-8");


            // Send the email
            javaMailSender.send(mimeMessage);
            System.out.println("Welcome email sent successfully to: " + email);
        } catch (MessagingException e) {
            System.out.println("Failed to send welcome email: " + e.getMessage());
        }
    }
}