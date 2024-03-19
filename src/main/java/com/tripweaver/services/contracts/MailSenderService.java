package com.tripweaver.services.contracts;

import com.tripweaver.models.User;
import com.tripweaver.models.enums.EmailVerificationType;
import jakarta.validation.constraints.Email;

public interface MailSenderService {
    void sendEmail(User recipient, EmailVerificationType emailVerificationType);
    void sendForgottenPasswordEmail(User recipient, EmailVerificationType emailVerificationType);
}
