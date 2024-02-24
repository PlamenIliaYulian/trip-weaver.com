package com.tripweaver.services.helpers;

import com.tripweaver.exceptions.UnauthorizedOperationException;
import com.tripweaver.models.Role;
import com.tripweaver.models.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PermissionHelper {
    private static final String ADMIN = "ADMIN";

    public static void isAdminOrSameUser(User userToBeUpdated,
                                         User loggedUser,
                                         String message) {
        boolean isAuthorized = false;

        if (userToBeUpdated.equals(loggedUser)) {
            isAuthorized = true;
        } else {
            List<Role> rolesOfAuthorizedUser = loggedUser.getRoles().stream().toList();
            for (Role currentRoleToBeChecked : rolesOfAuthorizedUser) {
                if (currentRoleToBeChecked.getRoleName().equals(ADMIN)) {
                    isAuthorized = true;
                    break;
                }
            }
        }
        if (!isAuthorized) {
            throw new UnauthorizedOperationException(message);
        }
    }
}
