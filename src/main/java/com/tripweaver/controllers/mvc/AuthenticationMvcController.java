package com.tripweaver.controllers.mvc;

import com.tripweaver.controllers.helpers.AuthenticationHelper;
import com.tripweaver.controllers.helpers.contracts.ModelsMapper;
import com.tripweaver.exceptions.AuthenticationException;
import com.tripweaver.exceptions.DuplicateEntityException;
import com.tripweaver.exceptions.EntityNotFoundException;
import com.tripweaver.models.User;
import com.tripweaver.models.dtos.ForgottenPasswordDto;
import com.tripweaver.models.dtos.LoginDto;
import com.tripweaver.models.dtos.UserDtoCreate;
import com.tripweaver.models.enums.EmailVerificationType;
import com.tripweaver.services.contracts.MailSenderService;
import com.tripweaver.services.contracts.RoleService;
import com.tripweaver.services.contracts.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.net.URISyntaxException;

import static com.tripweaver.services.helpers.ConstantHelper.ADMIN_ID;

@Controller
@RequestMapping("/auth")
public class AuthenticationMvcController {
    private final ModelsMapper modelsMapper;
    private final AuthenticationHelper authenticationHelper;
    private final UserService userService;
    private final RoleService roleService;
    private final MailSenderService mailSenderService;

    public AuthenticationMvcController(ModelsMapper modelsMapper,
                                       AuthenticationHelper authenticationHelper,
                                       UserService userService,
                                       RoleService roleService,
                                       MailSenderService mailSenderService) {
        this.modelsMapper = modelsMapper;
        this.authenticationHelper = authenticationHelper;
        this.userService = userService;
        this.roleService = roleService;
        this.mailSenderService = mailSenderService;
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

    @GetMapping("/email-verification")
    public String verifyEmail(@RequestParam("email") String email,
                              Model model) {
        try {
            User userToBeVerified = userService.getUserByEmail(email);
            userService.verifyEmail(userToBeVerified);
            return "redirect:/";
        } catch (AuthenticationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "Error";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "Error";
        }
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("login", new LoginDto());
        model.addAttribute("forgottenPassword", new ForgottenPasswordDto());
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
            return "redirect:/";
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

    @GetMapping("/send-new-email-verification")
    public String sendNewVerificationEmail(HttpSession session,
                                           Model model,
                                           HttpServletRequest servletRequest) {
        try {
            User userToBeVerified = authenticationHelper.tryGetUserFromSession(session);
            mailSenderService.sendEmail(userToBeVerified, EmailVerificationType.MVC);
            String referer = servletRequest.getHeader("Referer");
            URI refererUri = new URI(referer);
            return "redirect:" + refererUri.getPath();
        } catch (AuthenticationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "Error";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "Error";
        } catch (Exception e) {
            model.addAttribute("statusCode", HttpStatus.BAD_REQUEST.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "Error";
        }
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
            mailSenderService.sendEmail(user, EmailVerificationType.MVC);
            return "redirect:/auth/login";
        } catch (DuplicateEntityException e) {
            errors.rejectValue("username", "duplication_error", e.getMessage());
            return "Register";
        }
    }

    @GetMapping("/send-forgotten-password-email")
    public String forgottenPasswordEmailShow(Model model) {

        try {
            model.addAttribute("forgottenPassword", new ForgottenPasswordDto());
            return "ForgottenPassword";
        } catch (Exception e) {
            model.addAttribute("statusCode", HttpStatus.BAD_REQUEST.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "Error";
        }
    }

    @PostMapping("/send-forgotten-password-email")
    public String sendForgottenPasswordEmail(Model model,
                                             @ModelAttribute("forgottenPassword") ForgottenPasswordDto dto) {

        try {
            User userToBeSentEmailTo = userService.getUserByEmail(dto.getEmail());
            mailSenderService.sendForgottenPasswordEmail(userToBeSentEmailTo, EmailVerificationType.MVC);
            return "redirect:/auth/login";
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "ForgottenPassword";
        }
    }

}
