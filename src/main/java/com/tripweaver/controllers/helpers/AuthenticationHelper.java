package com.tripweaver.controllers.helpers;

import com.tripweaver.exceptions.AuthenticationException;
import com.tripweaver.exceptions.EntityNotFoundException;
import com.tripweaver.models.User;
import com.tripweaver.services.contracts.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import static com.tripweaver.services.helpers.ConstantHelper.*;

@Component
public class AuthenticationHelper {


    private UserService userService;

    @Autowired
    public AuthenticationHelper(UserService userService) {
        this.userService = userService;
    }

    public User tryGetUserFromHeaders(HttpHeaders headers) {
        if (!headers.containsKey(AUTHORIZATION_HEADER_NAME)) {
            throw new AuthenticationException(INVALID_AUTHENTICATION_ERROR);
        }

        try {
            String userInfo = headers.getFirst(AUTHORIZATION_HEADER_NAME);
            String username = getUsername(userInfo);
            String password = getPassword(userInfo);

            User user = userService.getUserByUsername(username);

            if (!user.getPassword().equals(password)) {
                throw new AuthenticationException(INVALID_AUTHENTICATION_ERROR);
            }

            return user;

        } catch (EntityNotFoundException e) {
            throw new AuthenticationException(INVALID_AUTHENTICATION_ERROR);
        }
    }

    public User tryGetUserFromSession(HttpSession httpSession) {
        String currentUser = (String) httpSession.getAttribute("currentUser");

        if(currentUser == null){
            throw new AuthenticationException(LOGGED_USER_ERROR);
        }

        return userService.getUserByUsername(currentUser);
    }



    private String getUsername(String userInfo) {
        int firstSpaceIndex = userInfo.indexOf(" ");
        if (firstSpaceIndex == -1) {
            throw new AuthenticationException(INVALID_AUTHENTICATION_ERROR);
        }
        return userInfo.substring(0, firstSpaceIndex);
    }

    private String getPassword(String userInfo) {
        int firstSpaceIndex = userInfo.indexOf(" ");
        if (firstSpaceIndex == -1) {
            throw new AuthenticationException(INVALID_AUTHENTICATION_ERROR);
        }
        return userInfo.substring(firstSpaceIndex + 1);
    }

    public User verifyAuthentication(String username, String password) {
        try {
            User user = userService.getUserByUsername(username);
            if (!user.getPassword().equals(password)) {
                throw new AuthenticationException(INVALID_AUTHENTICATION_ERROR);
            }
            return user;

        } catch (EntityNotFoundException e) {
            throw new AuthenticationException(INVALID_AUTHENTICATION_ERROR);
        }
    }
}
