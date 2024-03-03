package com.tripweaver.controllers.rest;

import com.tripweaver.exceptions.EntityNotFoundException;
import com.tripweaver.models.User;
import com.tripweaver.services.contracts.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication")
public class AuthenticationRestController {
    private final UserService userService;

    @Autowired
    public AuthenticationRestController(UserService userService) {
        this.userService = userService;
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
}
