package com.bankmanagement.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Async
    public void sendKycSubmissionEmail(String toEmail, String name) {
        String subject = "KYC Verification In Progress - Bank Management System";
        String content = String.format(
                """
                        <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 8px;">
                            <h2 style="color: #2563eb;">KYC Verification In Progress</h2>
                            <p>Dear %s,</p>
                            <p>Thank you for registering with Bank Management System. Your KYC documents have been successfully submitted and are currently under review.</p>
                            <div style="background-color: #f3f4f6; padding: 15px; border-radius: 6px; margin: 20px 0;">
                                <p style="margin: 0;"><strong>Status:</strong> <span style="color: #d97706;">PENDING APPROVAL</span></p>
                                <p style="margin: 5px 0 0;"><strong>Submission Time:</strong> %s</p>
                            </div>
                            <p>You will receive another email once your verification is complete. Until then, some features may be restricted.</p>
                            <p>Best regards,<br>Bank Management Team</p>
                        </div>
                        """,
                name, java.time.LocalDateTime.now());

        sendHtmlEmail(toEmail, subject, content);
    }

    @Async
    public void sendKycStatusEmail(String toEmail, String name, String status) {
        boolean isApproved = "APPROVED".equalsIgnoreCase(status);
        String color = isApproved ? "#16a34a" : "#dc2626";
        String subject = isApproved ? "KYC Approved! Welcome to Bank Management System" : "KYC Verification Failed";

        String content = String.format(
                """
                        <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 8px;">
                            <h2 style="color: %s;">KYC %s</h2>
                            <p>Dear %s,</p>
                            <p>Your KYC verification process has been completed.</p>
                            <div style="background-color: #f3f4f6; padding: 15px; border-radius: 6px; margin: 20px 0;">
                                <p style="margin: 0;"><strong>Status:</strong> <span style="color: %s; font-weight: bold;">%s</span></p>
                                <p style="margin: 5px 0 0;"><strong>Processed Time:</strong> %s</p>
                            </div>
                            %s
                            <p>Best regards,<br>Bank Management Team</p>
                        </div>
                        """,
                color,
                isApproved ? "Approved" : "Rejected",
                name,
                color,
                status,
                java.time.LocalDateTime.now(),
                isApproved
                        ? "<p>You now have full access to all banking features including deposits, withdrawals and loans.</p>"
                        : "<p>Please contact support or visit your nearest branch to resolve the issues.</p>");

        sendHtmlEmail(toEmail, subject, content);
    }

    private void sendHtmlEmail(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Async
    public void sendLoanSubmissionEmail(String toEmail, String name, double amount, String purpose) {
        String subject = "Loan Request Submitted - Bank Management System";
        String content = String.format(
                """
                        <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 8px;">
                            <h2 style="color: #2563eb;">Loan Request Submitted Successfully</h2>
                            <p>Dear %s,</p>
                            <p>Your loan request has been successfully submitted and is currently under review.</p>
                            <div style="background-color: #f3f4f6; padding: 15px; border-radius: 6px; margin: 20px 0;">
                                <p style="margin: 0;"><strong>Loan Amount:</strong> ₹%,.2f</p>
                                <p style="margin: 5px 0 0;"><strong>Purpose:</strong> %s</p>
                                <p style="margin: 5px 0 0;"><strong>Status:</strong> <span style="color: #d97706;">PENDING APPROVAL</span></p>
                                <p style="margin: 5px 0 0;"><strong>Submission Time:</strong> %s</p>
                            </div>
                            <p>Our team will review your application and notify you once a decision has been made.</p>
                            <p>Best regards,<br>Bank Management Team</p>
                        </div>
                        """,
                name, amount, purpose, java.time.LocalDateTime.now());

        sendHtmlEmail(toEmail, subject, content);
    }

    @Async
    public void sendLoanStatusEmail(String toEmail, String name, double amount, String purpose, String status) {
        boolean isApproved = "APPROVED".equalsIgnoreCase(status);
        String color = isApproved ? "#16a34a" : "#dc2626";
        String subject = isApproved ? "Loan Approved! - Bank Management System" : "Loan Application Update";
        java.time.LocalDate disbursementDate = java.time.LocalDate.now().plusDays(5);
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy");
        String formattedDisbursementDate = disbursementDate.format(formatter);

        String additionalInfo = isApproved
                ? String.format(
                        "<p><strong>Disbursement Date:</strong> <span style=\"color: #16a34a; font-weight: bold;\">%s</span></p><p>The loan amount will be credited to your account on the disbursement date.</p>",
                        formattedDisbursementDate)
                : "<p>Unfortunately, your loan application could not be approved at this time. Please contact support for more details.</p>";

        String content = String.format(
                """
                        <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 8px;">
                            <h2 style="color: %s;">Loan Application %s</h2>
                            <p>Dear %s,</p>
                            <p>Your loan application has been reviewed.</p>
                            <div style="background-color: #f3f4f6; padding: 15px; border-radius: 6px; margin: 20px 0;">
                                <p style="margin: 0;"><strong>Loan Amount:</strong> ₹%,.2f</p>
                                <p style="margin: 5px 0 0;"><strong>Purpose:</strong> %s</p>
                                <p style="margin: 5px 0 0;"><strong>Status:</strong> <span style="color: %s; font-weight: bold;">%s</span></p>
                                <p style="margin: 5px 0 0;"><strong>Processed Time:</strong> %s</p>
                            </div>
                            %s
                            <p>Best regards,<br>Bank Management Team</p>
                        </div>
                        """,
                color,
                isApproved ? "Approved" : "Update",
                name,
                amount,
                purpose,
                color,
                status,
                java.time.LocalDateTime.now(),
                additionalInfo);

        sendHtmlEmail(toEmail, subject, content);
    }
}
