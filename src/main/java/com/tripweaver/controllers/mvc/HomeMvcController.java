package com.tripweaver.controllers.mvc;

import com.tripweaver.controllers.helpers.AuthenticationHelper;
import com.tripweaver.models.Travel;
import com.tripweaver.models.User;
import com.tripweaver.models.filterOptions.TravelFilterOptions;
import com.tripweaver.services.contracts.FeedbackService;
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

import java.util.HashMap;

@Controller
@RequestMapping("/")
public class HomeMvcController {

    private final UserService userService;
    private final TravelService travelService;
    private final AuthenticationHelper authenticationHelper;
    private final RoleService roleService;
    private final FeedbackService feedbackService;

    public HomeMvcController(UserService userService,
                             TravelService travelService,
                             AuthenticationHelper authenticationHelper,
                             RoleService roleService,
                             FeedbackService feedbackService) {
        this.userService = userService;
        this.travelService = travelService;
        this.authenticationHelper = authenticationHelper;
        this.roleService = roleService;
        this.feedbackService = feedbackService;
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
        model.addAttribute("topTenTravelOrganizers", userService.getTopTwelveTravelOrganizersByRating());
        model.addAttribute("topTenPassengers", userService.getTopTwelveTravelPassengersByRating());
        model.addAttribute("fiveStarRatingFeedbackCount", feedbackService.getAllFiveStarReviewsCount());

        HashMap<String, Integer> totalTravelsAsDriverHashMap = new HashMap<>();
        HashMap<String, Integer> totalDistancAsDrivereHashMap = new HashMap<>();
        TravelFilterOptions travelFilterOptions = new TravelFilterOptions(null, null, null,
                null, null, null, null, null, null,
                null);
        for (User driver : userService.getTopTwelveTravelOrganizersByRating()) {
            int totalDistance = travelService.getTravelsByDriver(driver, driver, travelFilterOptions)
                    .stream()
                    .map(Travel::getDistanceInKm)
                    .reduce(0, Integer::sum);
            totalDistancAsDrivereHashMap.put(driver.getUsername(), totalDistance);
            int totalTravels = travelService.getTravelsByDriver(driver, driver, travelFilterOptions).size();
            totalTravelsAsDriverHashMap.put(driver.getUsername(), totalTravels);
        }
        model.addAttribute("driverTotalDistance", totalDistancAsDrivereHashMap);
        model.addAttribute("driverTotalTravels", totalTravelsAsDriverHashMap);


        HashMap<String, Integer> totalTravelsAsPassengerHashMap = new HashMap<>();
        HashMap<String, Integer> totalDistanceAsPassengerHashMap = new HashMap<>();
        for (User passenger : userService.getTopTwelveTravelPassengersByRating()) {
            int totalDistance = travelService.getTravelsByPassenger(passenger, passenger, travelFilterOptions)
                    .stream()
                    .map(Travel::getDistanceInKm)
                    .reduce(0, Integer::sum);
            totalDistanceAsPassengerHashMap.put(passenger.getUsername(), totalDistance);
            int totalTravels = travelService.getTravelsByPassenger(passenger, passenger, travelFilterOptions).size();
            totalTravelsAsPassengerHashMap.put(passenger.getUsername(), totalTravels);
        }
        model.addAttribute("passengerTotalDistance", totalDistanceAsPassengerHashMap);
        model.addAttribute("passengerTotalTravels", totalTravelsAsPassengerHashMap);


        return "Home";
    }

    @GetMapping("/about")
    public String showAboutPage() {
        return "About";
    }
}
