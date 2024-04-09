package com.employeselfservice.services.email;

import com.employeselfservice.models.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendWelcomeEmail(Employee employee) {

        System.out.println("Method called for\n"+employee);

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

        System.out.println("Message--\n"+message);

        try {
            helper.setTo(employee.getEmail());
            helper.setSubject("Welcome to DRC Systems!");

            System.out.println(helper.getEncoding());

            // HTML content with inline CSS styles
            String htmlContent = "<!DOCTYPE html>"
                    + "<html>"
                    + "<head>"
                    + "<style>"
                    + "body { font-family: Arial, sans-serif; }"
                    + "h2 { color: #0066cc; }"
                    + "ul { list-style-type: none; padding: 0; }"
                    + "li { margin-bottom: 5px; list-style:square; }"
                    + "img { max-width: 200px; }"
                    + "</style>"
                    + "</head>"
                    + "<body>"
                    + "<h2>Welcome to Our Company</h2>"
                    + "<p>Dear " + employee.getFirstname() + ",</p>"
                    + "<p>Welcome to DRC Systems! You have been successfully added as an employee.</p>"
                    + "<p>Your login details:</p>"
                    + "<ul>"
                    + "<li><strong>Email:</strong> " + employee.getEmail() + "</li>"
                    + "<li><strong>Password:</strong> " + employee.getPassword() + "</li>"
                    + "</ul>"
                    + "<img src='cid:companyLogo' />"
                    + "<p>Thank you,<br/>Admin Team</p>"
                    + "</body>"
                    + "</html>";

            System.out.println("HTML Content\n"+htmlContent);

            // Set the HTML content and inline image
            helper.setText(htmlContent, true);
            helper.addInline("companyLogo", new ClassPathResource("com/employeselfservice/services/images/logo.svg"));

            javaMailSender.send(message);
            System.out.println("Welcome email sent successfully to: " + employee.getEmail());
        } catch (MessagingException e) {
            System.out.println("Failed to send welcome email: " + e.getMessage());
        }
    }
}
