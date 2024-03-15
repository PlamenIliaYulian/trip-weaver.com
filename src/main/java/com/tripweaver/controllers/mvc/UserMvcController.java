package com.tripweaver.controllers.mvc;


import com.tripweaver.controllers.helpers.AuthenticationHelper;
import com.tripweaver.controllers.helpers.contracts.ModelsMapper;
import com.tripweaver.exceptions.AuthenticationException;
import com.tripweaver.exceptions.EntityNotFoundException;
import com.tripweaver.exceptions.InvalidOperationException;
import com.tripweaver.exceptions.UnauthorizedOperationException;
import com.tripweaver.models.Role;
import com.tripweaver.models.User;
import com.tripweaver.models.dtos.UserFilterOptionsDto;
import com.tripweaver.models.filterOptions.UserFilterOptions;
import com.tripweaver.services.contracts.AvatarService;
import com.tripweaver.services.contracts.RoleService;
import com.tripweaver.services.contracts.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/users")
public class UserMvcController {


    public static final String PASSWORD_CONFIRMATION_SHOULD_MATCH_PASSWORD = "Password confirmation should match password.";
    private final AuthenticationHelper authenticationHelper;
    private final ModelsMapper modelsMapper;
    private final UserService userService;
    private final RoleService roleService;
    private final AvatarService avatarService;

    public UserMvcController(AuthenticationHelper authenticationHelper,
                             ModelsMapper modelsMapper,
                             UserService userService,
                             RoleService roleService,
                             AvatarService avatarService) {
        this.authenticationHelper = authenticationHelper;
        this.modelsMapper = modelsMapper;
        this.userService = userService;
        this.roleService = roleService;
        this.avatarService = avatarService;
    }

    @ModelAttribute("requestURI")
    public String requestURI(final HttpServletRequest request) {
        return request.getRequestURI();
    }

    @ModelAttribute("isBlocked")
    public boolean populateIsBlocked(HttpSession httpSession) {
        return (httpSession.getAttribute("currentUser") != null &&
                authenticationHelper
                        .tryGetUserFromSession(httpSession)
                        .isBlocked()
        );
    }

    @ModelAttribute("isAdmin")
    public boolean populateIsLoggedAndAdmin(HttpSession httpSession) {
        return (httpSession.getAttribute("currentUser") != null &&
                authenticationHelper
                        .tryGetUserFromSession(httpSession)
                        .getRoles()
                        .contains(roleService.getRoleById(1)));
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession httpSession) {
        return httpSession.getAttribute("currentUser") != null;
    }

    @ModelAttribute("loggedUser")
    public User populateLoggedUser(HttpSession httpSession) {
        if (httpSession.getAttribute("currentUser") != null) {
            return authenticationHelper.tryGetUserFromSession(httpSession);
        }
        return new User();
    }

    @ModelAttribute("adminRole")
    public Role populateAdminRole() {
        return roleService.getRoleById(1);
    }

    @GetMapping("/search")
    public String showUsersAdminPage(@ModelAttribute("userFilterOptionsDto") UserFilterOptionsDto userFilterOptionsDto,
                                     Model model,
                                     HttpSession session) {

        User loggedUser;
        try {
            loggedUser = authenticationHelper.tryGetUserFromSession(session);
            Role adminRole = roleService.getRoleById(1);
            if (loggedUser.getRoles().contains(adminRole)) {
                UserFilterOptions userFilterOptions = modelsMapper.userFilterOptionsFromDto(userFilterOptionsDto);
                model.addAttribute("users", userService.getAllUsers(userFilterOptions, loggedUser));
                model.addAttribute("userFilterOptionsDto", userFilterOptionsDto);
                return "AllUsers";
            }
            model.addAttribute("error", HttpStatus.FORBIDDEN.getReasonPhrase());
            return "Error";
        } catch (AuthenticationException e) {
            return "redirect:/auth/login";
        }
    }
    @GetMapping("/{userId}/block")
    public String blockUser(@PathVariable int userId,
                            HttpSession session,
                            Model model) {

        try {
            User loggedUser = authenticationHelper.tryGetUserFromSession(session);
            User userToBeBlocked = userService.getUserById(userId);
            userService.blockUser(userToBeBlocked, loggedUser);

            return "redirect:/users/search";

        } catch (AuthenticationException e) {
            return "redirect:/auth/login";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "Error";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("statusCode", HttpStatus.FORBIDDEN.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "Error";
        }
    }

    @GetMapping("/{userId}/unblock")
    public String unblockUser(@PathVariable int userId,
                            HttpSession session,
                            Model model) {
        try {
            User loggedUser = authenticationHelper.tryGetUserFromSession(session);
            User userToBeUnblocked = userService.getUserById(userId);
            userService.blockUser(userToBeUnblocked, loggedUser);

            return "redirect:/users/search";

        } catch (AuthenticationException e) {
            return "redirect:/auth/login";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "Error";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("statusCode", HttpStatus.FORBIDDEN.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "Error";
        }
    }



}
