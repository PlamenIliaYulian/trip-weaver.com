package com.tripweaver.controllers.mvc;

import com.tripweaver.controllers.helpers.AuthenticationHelper;
import com.tripweaver.controllers.helpers.contracts.ModelsMapper;
import com.tripweaver.exceptions.AuthenticationException;
import com.tripweaver.exceptions.DuplicateEntityException;
import com.tripweaver.models.User;
import com.tripweaver.models.dtos.LoginDto;
import com.tripweaver.models.dtos.UserDtoCreate;
import com.tripweaver.services.contracts.RoleService;
import com.tripweaver.services.contracts.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.tripweaver.services.helpers.ConstantHelper.ADMIN_ID;

@Controller
@RequestMapping("/auth")
public class AuthenticationMvcController {
    private final ModelsMapper modelsMapper;
    private final AuthenticationHelper authenticationHelper;
    private final UserService userService;
    private final RoleService roleService;

    public AuthenticationMvcController(ModelsMapper modelsMapper,
                                       AuthenticationHelper authenticationHelper,
                                       UserService userService,
                                       RoleService roleService) {
        this.modelsMapper = modelsMapper;
        this.authenticationHelper = authenticationHelper;
        this.userService = userService;
        this.roleService = roleService;
    }

    @ModelAttribute("isBlocked")
    public boolean populateIsBlocked(HttpSession httpSession) {
        return (httpSession.getAttribute("currentUser") != null &&
                authenticationHelper
                        .tryGetUserFromSession(httpSession)
                        .isBlocked()
        );
    }

    @ModelAttribute("requestURI")
    public String requestURI(final HttpServletRequest request) {
        return request.getRequestURI();
    }


    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("login", new LoginDto());
        return "Login";
    }

    @PostMapping("/login")
    public String handleLogin(@Valid @ModelAttribute("login") LoginDto loginDto,
                              BindingResult errors,
                              HttpSession session) {

        if (errors.hasErrors()) {
            return "Login";
        }
        try {
            authenticationHelper.verifyAuthentication(loginDto.getUsername(), loginDto.getPassword());
            session.setAttribute("currentUser", loginDto.getUsername());
            return "redirect:/users/2";
        } catch (AuthenticationException e) {
            errors.rejectValue("username", "auth_error", e.getMessage());
            return "Login";
        }
    }

    @GetMapping("/logout")
    public String handleLogout(HttpSession session) {
        session.removeAttribute("currentUser");
        return "redirect:/";
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("register", new UserDtoCreate());
        return "Register";
    }

    @PostMapping("/register")
    public String handleRegister(@Valid @ModelAttribute("register") UserDtoCreate userDtoCreate,
                                 BindingResult errors) {

        if (errors.hasErrors()) {
            return "Register";
        }
        if (!userDtoCreate.getPassword().equals(userDtoCreate.getConfirmPassword())) {
            errors.rejectValue("passwordConfirm", "password_error", "Password confirmation should match password.");
            return "Register";
        }

        try {
            User user = modelsMapper.userFromDtoCreate(userDtoCreate);
            userService.createUser(user);
            return "redirect:/auth/login";
        } catch (DuplicateEntityException e) {
            errors.rejectValue("username", "duplication_error", e.getMessage());
            return "Register";
        }
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

    @ModelAttribute("isAdmin")
    public boolean populateIsLoggedAndAdmin(HttpSession httpSession) {
        return (httpSession.getAttribute("currentUser") != null &&
                authenticationHelper
                        .tryGetUserFromSession(httpSession)
                        .getRoles()
                        .contains(roleService.getRoleById(ADMIN_ID)));
    }

    @ModelAttribute("isNotBlocked")
    public boolean populateIsLoggedAndNotBlocked(HttpSession httpSession) {
        return (httpSession.getAttribute("currentUser") != null &&
                !authenticationHelper
                        .tryGetUserFromSession(httpSession)
                        .isBlocked());
    }


}
