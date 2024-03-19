package com.tripweaver.services;

import com.tripweaver.models.User;
import com.tripweaver.models.enums.EmailVerificationType;
import com.tripweaver.services.contracts.MailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailSenderServiceImpl implements MailSenderService {

    public static final String API_DOMAIN = "http://localhost:8081";
    private final JavaMailSender mailSender;
    private final String mailSubject = "www.trip-weaver.com - Please Verify Your Email Address";
    private String mailbody = """
            Dear %s,
                        
            Thank you for signing up for our service. To complete your registration and gain access to all features, please verify your email address by clicking the link below:
                        
            %s
                        
            If you did not create an account with us, please disregard this email.
            Thank you,
            Trip Weavers
            """;

    private final String endpoint = "/email-verification?email=%s";

    @Value("${spring.mail.username}")
    private String senderMail;

    @Autowired
    public MailSenderServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendEmail(User recipient, EmailVerificationType emailVerificationType) {
        SimpleMailMessage message = new SimpleMailMessage();
        /*TODO don't forget to update the verificationLink*/
        String domain = API_DOMAIN;
        StringBuilder staticPartOfTheLink = new StringBuilder();
        staticPartOfTheLink.append(domain).append(emailVerificationType.getText()).append(endpoint);
        String verificationFullLink = String.format(staticPartOfTheLink.toString(), recipient.getEmail());
        message.setFrom(senderMail);
        message.setTo(recipient.getEmail());
        message.setText(String.format(mailbody, recipient.getUsername(), verificationFullLink));
        message.setSubject(mailSubject);
        mailSender.send(message);
    }
}
