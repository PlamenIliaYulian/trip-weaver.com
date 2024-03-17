package com.tripweaver.controllers.mvc;


import com.tripweaver.controllers.helpers.AuthenticationHelper;
import com.tripweaver.controllers.helpers.contracts.ModelsMapper;
import com.tripweaver.exceptions.*;
import com.tripweaver.models.Feedback;
import com.tripweaver.models.Role;
import com.tripweaver.models.Travel;
import com.tripweaver.models.User;
import com.tripweaver.models.dtos.FeedbackDto;
import com.tripweaver.models.dtos.TravelFilterOptionsDto;
import com.tripweaver.models.dtos.UserDto;
import com.tripweaver.models.dtos.UserFilterOptionsDto;
import com.tripweaver.models.filterOptions.TravelFilterOptions;
import com.tripweaver.models.filterOptions.UserFilterOptions;
import com.tripweaver.services.contracts.AvatarService;
import com.tripweaver.services.contracts.RoleService;
import com.tripweaver.services.contracts.TravelService;
import com.tripweaver.services.contracts.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.tripweaver.services.helpers.ConstantHelper.ADMIN_ID;
import static com.tripweaver.services.helpers.ConstantHelper.CONFIRM_PASSWORD_SHOULD_MATCH_PASSWORD;


@Controller
@RequestMapping("/users")
public class UserMvcController {

    private final AuthenticationHelper authenticationHelper;
    private final ModelsMapper modelsMapper;
    private final UserService userService;
    private final RoleService roleService;
    private final TravelService travelService;
    private final AvatarService avatarService;

    public UserMvcController(AuthenticationHelper authenticationHelper,
                             ModelsMapper modelsMapper,
                             UserService userService,
                             RoleService roleService,
                             TravelService travelService,
                             AvatarService avatarService) {
        this.authenticationHelper = authenticationHelper;
        this.modelsMapper = modelsMapper;
        this.userService = userService;
        this.roleService = roleService;
        this.travelService = travelService;
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
                        .contains(roleService.getRoleById(ADMIN_ID)));
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
    public String showAllUsersPage(@ModelAttribute("userFilterOptionsDto") UserFilterOptionsDto userFilterOptionsDto,
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
            userService.unBlockUser(userToBeUnblocked, loggedUser);

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

    @GetMapping("/{id}")
    public String showSingleUserPage(@PathVariable int id,
                                     @ModelAttribute("travelFilterOptions") TravelFilterOptionsDto dto,
                                     Model model,
                                     HttpSession session) {
        try {
            User loggedInUser = authenticationHelper.tryGetUserFromSession(session);
            User user = userService.getUserById(id);
            List<Travel> allTravels = new ArrayList<>();
            TravelFilterOptions travelFilterOptions = modelsMapper.travelFilterOptionsFromDto(dto);
            TravelFilterOptions travelFilterOptions2 = modelsMapper.travelFilterOptionsFromDto(dto);
            if(loggedInUser.getUserId() == id){
                allTravels.addAll(travelService.getTravelsByDriver(user, loggedInUser, travelFilterOptions));
                allTravels.addAll(travelService.getTravelsByPassenger(user, loggedInUser, travelFilterOptions2));
            }

            model.addAttribute("userById", user);
            model.addAttribute("userTravels", allTravels);
            model.addAttribute("feedbackForDriver", userService.getAllFeedbackForDriver(user));
            model.addAttribute("feedbackForPassenger", userService.getAllFeedbackForPassenger(user));
            model.addAttribute("travelFilterOptions", dto);
            model.addAttribute("userTotalDistanceAsPassenger", userService
                    .getTotalDistanceAsPassengerHashMap(Collections.singletonList(user)));
            model.addAttribute("userTotalDistanceAsDriver", userService
                    .getTotalDistanceAsDriverHashMap(Collections.singletonList(user)));
            model.addAttribute("userTotalTravelsAsDriver", userService
                    .getTotalTravelsAsDriverHashMap(Collections.singletonList(user)));
            model.addAttribute("userTotalTravelsAsPassenger", userService
                    .getTotalTravelsAsPassengerHashMap(Collections.singletonList(user)));

            return "SingleUser";
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

    @GetMapping("/{id}/edit")
    public String showEditPage(@PathVariable int id,
                               Model model,
                               HttpSession session) {
        try {
            User userLoggedIn = authenticationHelper.tryGetUserFromSession(session);
            User userById = userService.getUserById(id);
            if (!userLoggedIn.equals(userById)) {
                model.addAttribute("error", HttpStatus.FORBIDDEN.getReasonPhrase());
                return "Error";
            }
            UserDto userDto = modelsMapper.userDtoFromUser(userById);
            model.addAttribute("userDto", userDto);
            model.addAttribute("userById", userById);
            return "UserEdit";
        } catch (AuthenticationException e) {
            return "redirect:/auth/login";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "Error";
        }
    }

    @PostMapping("/{id}/edit")
    public String handleEditUser(@PathVariable int id,
                                 @Valid @ModelAttribute("userDto") UserDto userDto,
                                 BindingResult errors,
                                 HttpSession session,
                                 Model model) {

        User userById = null;
        try {
            userById = userService.getUserById(id);
            model.addAttribute("userById", userById);
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "Error";
        }

        if (errors.hasErrors()) {
            return "UserEdit";
        }

        if (!userDto.getPassword().equals(userDto.getConfirmPassword())) {
            errors.rejectValue("confirmPassword", "password_error", CONFIRM_PASSWORD_SHOULD_MATCH_PASSWORD);
            return "UserEdit";
        }
        try {
            User userLoggedIn = authenticationHelper.tryGetUserFromSession(session);
            if (!userLoggedIn.equals(userById)) {
                model.addAttribute("error", HttpStatus.FORBIDDEN.getReasonPhrase());
                return "Error";
            }

            User userUpdated = modelsMapper.userFromDto(userDto, id);
            userService.updateUser(userUpdated, userLoggedIn);
            return "redirect:/users/{id}";
        } catch (AuthenticationException e) {
            model.addAttribute("error", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            return "Error";
        } catch (DuplicateEntityException e) {
            errors.rejectValue("email", "email_exists", e.getMessage());
            return "UserEdit";
        }
    }


    @PostMapping("/{id}/edit/photos/upload")
    public String uploadAvatar(@PathVariable int id,
                               Model model,
                               @RequestParam("file") MultipartFile file,
                               HttpSession session) {
        String message = "";


        try {
            String newAvatar = avatarService.uploadPictureToCloudinary(file);
            User userById = userService.getUserById(id);
            User loggedUser = authenticationHelper.tryGetUserFromSession(session);
            userService.addAvatar(userById, newAvatar, loggedUser);
            message = "Uploaded the file successfully: " + file.getOriginalFilename();
            model.addAttribute("message", message);
            return "redirect:/users/{id}";
        } catch (AuthenticationException e) {
            model.addAttribute("error", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            return "redirect:/auth/login";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "Error";
        } catch (RuntimeException e) {
            model.addAttribute("message", e.getMessage());
            return "Error";
        }
    }




}
