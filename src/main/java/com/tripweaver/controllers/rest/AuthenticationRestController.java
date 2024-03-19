package com.tripweaver.controllers.rest;

import com.tripweaver.controllers.helpers.AuthenticationHelper;
import com.tripweaver.exceptions.AuthenticationException;
import com.tripweaver.exceptions.EntityNotFoundException;
import com.tripweaver.models.Feedback;
import com.tripweaver.models.User;
import com.tripweaver.models.enums.EmailVerificationType;
import com.tripweaver.services.contracts.MailSenderService;
import com.tripweaver.services.contracts.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    @Operation(
            summary = "Verify email.",
            description = "Endpoint helping verification of an email of a newly registered user.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = Feedback.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)
                    )
            }
    )
    @GetMapping("/email-verification")
    public User verifyEmail(@RequestParam("email") String email) {
        try {
            User userToBeVerified = userService.getUserByEmail(email);
            return userService.verifyEmail(userToBeVerified);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Operation(
            summary = "Send new verification email.",
            description = "Endpoint helping verification of an email of a newly registered user by sending another email to the" +
                    "user's email.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = Feedback.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)
                    )
            }
    )
    @GetMapping("/send-new-email-verification")
    public User sendNewVerificationEmail(@RequestHeader HttpHeaders headers) {
        try {
            User userToBeVerified = authenticationHelper.tryGetUserFromHeaders(headers);
            mailSenderService.sendEmail(userToBeVerified, EmailVerificationType.REST);
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
