package com.tripweaver.services.contracts;

import com.tripweaver.models.User;

public interface MailSenderService {
    void sendEmail(User recipient);
}
