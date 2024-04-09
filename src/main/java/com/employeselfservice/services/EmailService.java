package com.employeselfservice.services;

import com.employeselfservice.models.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.MimeMessageHelper;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.io.File;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendWelcomeEmail(String firstName, String email, String password) {
        try {

            System.out.println("Employee firstName in sendWelcomeEmail"+firstName);
            System.out.println("Employee email in sendWelcomeEmail"+email);
            System.out.println("Employee password in sendWelcomeEmail"+password);

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
                    + "<p>Welcome to DRC Systems! You have been successfully added as an employee.</p>"
                    + "<p>Your login details:</p>"
                    + "<ul>"
                    + "<li><strong>Email:</strong> " + email + "</li>"
                    + "<li><strong>Password:</strong> " + password + "</li>"
                    + "</ul>"
//                    + "<img src='cid:companyLogo' />" // Fallback image
                    + "<svg xmlns=\"http://www.w3.org/2000/svg\" height=\"24\" viewBox=\"0 -960 960 960\" width=\"24\"><path d=\"M480-480q-66 0-113-47t-47-113q0-66 47-113t113-47q66 0 113 47t47 113q0 66-47 113t-113 47ZM160-160v-112q0-34 17.5-62.5T224-378q62-31 126-46.5T480-440q66 0 130 15.5T736-378q29 15 46.5 43.5T800-272v112H160Zm80-80h480v-32q0-11-5.5-20T700-306q-54-27-109-40.5T480-360q-56 0-111 13.5T260-306q-9 5-14.5 14t-5.5 20v32Zm240-320q33 0 56.5-23.5T560-640q0-33-23.5-56.5T480-720q-33 0-56.5 23.5T400-640q0 33 23.5 56.5T480-560Zm0-80Zm0 400Z\"/></svg>"
                    + "<p>Thank you,<br/>Admin Team</p>"
                    + "</body>"
                    + "</html>";

            // Set the HTML content and inline images
//            helper.addInline("companyLogo", new ClassPathResource("com/employeselfservice/services/images/drcsystems.png"));
//            helper.addAttachment("logo.png", new File("com/employeselfservice/services/images/drcsystems.png"));
            mimeMessage.setContent(htmlContent, "text/html; charset=utf-8");


            // Send the email
            javaMailSender.send(mimeMessage);
            System.out.println("Welcome email sent successfully to: " + email);
        } catch (MessagingException e) {
            System.out.println("Failed to send welcome email: " + e.getMessage());
        }
    }
}
