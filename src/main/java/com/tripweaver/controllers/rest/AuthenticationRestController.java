package com.tripweaver.controllers.rest;

import com.tripweaver.controllers.helpers.AuthenticationHelper;
import com.tripweaver.exceptions.AuthenticationException;
import com.tripweaver.exceptions.EntityNotFoundException;
import com.tripweaver.models.User;
import com.tripweaver.services.contracts.MailSenderService;
import com.tripweaver.services.contracts.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication")
public class AuthenticationRestController {
    private final UserService userService;
    private final AuthenticationHelper authenticationHelper;

    private final MailSenderService mailSenderService;

    @Autowired
    public AuthenticationRestController(UserService userService, AuthenticationHelper authenticationHelper, MailSenderService mailSenderService) {
        this.userService = userService;
        this.authenticationHelper = authenticationHelper;
        this.mailSenderService = mailSenderService;
    }

    /*ToDo How to describe this method in Swagger?*/
    @GetMapping("/email-verification")
    public User verifyEmail(@RequestParam("email") String email) {
        try {
            User userToBeVerified = userService.getUserByEmail(email);
            return userService.verifyEmail(userToBeVerified);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/send-new-email-verification")
    public User sendNewVerificationEmail(@RequestHeader HttpHeaders headers) {
        try {
            User userToBeVerified = authenticationHelper.tryGetUserFromHeaders(headers);
            mailSenderService.sendEmail(userToBeVerified);
            return userToBeVerified;
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    e.getMessage());
        }
    }
}
