package com.tripweaver.controllers.mvc;

import com.tripweaver.controllers.helpers.AuthenticationHelper;
import com.tripweaver.models.User;
import com.tripweaver.services.contracts.RoleService;
import com.tripweaver.services.contracts.TravelService;
import com.tripweaver.services.contracts.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/")
public class HomeMvcController {

    private final UserService userService;
    private final TravelService travelService;
    private final AuthenticationHelper authenticationHelper;
    private final RoleService roleService;

    public HomeMvcController(UserService userService,
                             TravelService travelService,
                             AuthenticationHelper authenticationHelper,
                             RoleService roleService) {
        this.userService = userService;
        this.travelService = travelService;
        this.authenticationHelper = authenticationHelper;
        this.roleService = roleService;
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession httpSession) {
        return httpSession.getAttribute("currentUser") != null;
    }


    @ModelAttribute("requestURI")
    public String requestURI(final HttpServletRequest request) {
        return request.getRequestURI();
    }


    @ModelAttribute("isAdmin")
    public boolean populateIsLoggedAndAdmin(HttpSession httpSession) {
        return (httpSession.getAttribute("currentUser") != null &&
                authenticationHelper
                        .tryGetUserFromSession(httpSession)
                        .getRoles()
                        .contains(roleService.getRoleById(1)));
    }

    @ModelAttribute("isBlocked")
    public boolean populateIsBlocked(HttpSession httpSession){
        return (httpSession.getAttribute("currentUser") != null &&
                authenticationHelper
                        .tryGetUserFromSession(httpSession)
                        .isBlocked()
        );
    }

    @ModelAttribute("loggedUser")
    public User populateLoggedUser(HttpSession httpSession) {
        if (httpSession.getAttribute("currentUser") != null) {
            return authenticationHelper.tryGetUserFromSession(httpSession);
        }
        return new User();
    }

    @GetMapping
    public String showHomePage(Model model) {

        model.addAttribute("totalUsersCount", userService.getAllUsersCount());
        model.addAttribute("totalTravelsCount", travelService.getAllTravelsCount());
        model.addAttribute("topTenTravelOrganizers", userService.getTopTenTravelOrganizersByRating());
        model.addAttribute("topTenPassengers", userService.getTopTenTravelPassengersByRating());
        return "Home";
    }

    @GetMapping("/about")
    public String showAboutPage() {
        return "About";
    }
}
