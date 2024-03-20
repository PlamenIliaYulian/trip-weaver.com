package com.tripweaver.services;

import com.tripweaver.models.User;
import com.tripweaver.models.enums.EmailVerificationType;
import com.tripweaver.services.contracts.MailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import static com.tripweaver.services.helpers.ConstantHelper.API_DOMAIN;

@Service
public class MailSenderServiceImpl implements MailSenderService {

    private final JavaMailSender mailSender;
    private final String mailSubject = "www.trip-weaver.com - Please Verify Your Email Address";
    private final String mailForgottenPasswordSubject = "www.trip-weaver.com - Forgotten password request";
    private String mailbody = """
            Dear %s,
                        
            Thank you for signing up for our service. To complete your registration and gain access to all features, please verify your email address by clicking the link below:
                        
            %s
                        
            If you did not create an account with us, please disregard this email.
            Thank you,
            Trip Weavers
            """;

    private String forgottenPasswordBody = """
            Dear %s,
                        
            You recently requested to receive an email with your password for www.trip-weavers.com. To complete the process, please copy the password below:
            
            %s
            
            If you did not request a password reset, please ignore this email. This link is valid for 24 hours.
            Thank you,
            Trip Weavers
            """;

    private final String emailEndpoint = "/email-verification?email=%s";
    private final String forgottenPasswordEndpoint = "/send-forgotten-password-email";

    @Value("${spring.mail.username}")
    private String senderMail;

    @Autowired
    public MailSenderServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendEmail(User recipient, EmailVerificationType emailVerificationType) {
        SimpleMailMessage message = new SimpleMailMessage();

        StringBuilder staticPartOfTheLink = new StringBuilder();
        staticPartOfTheLink.append(API_DOMAIN).append(emailVerificationType.getText()).append(emailEndpoint);
        String verificationFullLink = String.format(staticPartOfTheLink.toString(), recipient.getEmail());

        message.setFrom(senderMail);
        message.setTo(recipient.getEmail());
        message.setText(String.format(mailbody, recipient.getFirstName(), verificationFullLink));
        message.setSubject(mailSubject);
        mailSender.send(message);
    }

    @Override
    public void sendForgottenPasswordEmail(User recipient, EmailVerificationType emailVerificationType) {
        SimpleMailMessage message = new SimpleMailMessage();

        StringBuilder staticPartOfTheLink = new StringBuilder();
        staticPartOfTheLink.append(API_DOMAIN).append(emailVerificationType.getText()).append(forgottenPasswordEndpoint);

        message.setFrom(senderMail);
        message.setTo(recipient.getEmail());
        message.setText(String.format(forgottenPasswordBody, recipient.getFirstName(), recipient.getPassword()));
        message.setSubject(mailForgottenPasswordSubject);
        mailSender.send(message);
    }
}
