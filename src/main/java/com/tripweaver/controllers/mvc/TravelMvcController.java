package com.tripweaver.controllers.mvc;

import com.tripweaver.controllers.helpers.AuthenticationHelper;
import com.tripweaver.controllers.helpers.contracts.ModelsMapper;
import com.tripweaver.exceptions.AuthenticationException;
import com.tripweaver.exceptions.EntityNotFoundException;
import com.tripweaver.exceptions.UnauthorizedOperationException;
import com.tripweaver.models.Travel;
import com.tripweaver.models.User;
import com.tripweaver.models.dtos.TravelDto;
import com.tripweaver.models.dtos.TravelFilterOptionsDto;
import com.tripweaver.models.filterOptions.TravelFilterOptions;
import com.tripweaver.services.contracts.RoleService;
import com.tripweaver.services.contracts.TravelService;
import com.tripweaver.services.contracts.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/travels")
public class TravelMvcController {

    private final AuthenticationHelper authenticationHelper;
    private final RoleService roleService;
    private final TravelService travelService;
    private final ModelsMapper modelsMapper;
    private final UserService userService;

    public TravelMvcController(AuthenticationHelper authenticationHelper,
                               RoleService roleService,
                               TravelService travelService,
                               ModelsMapper modelsMapper,
                               UserService userService) {
        this.authenticationHelper = authenticationHelper;
        this.roleService = roleService;
        this.travelService = travelService;
        this.modelsMapper = modelsMapper;
        this.userService = userService;
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


    @GetMapping("/new")
    public String showCreateNewTravelPage(HttpSession httpSession,
                                          Model model){
        try {
            authenticationHelper.tryGetUserFromSession(httpSession);
            model.addAttribute("travelDto", new TravelDto());
            return "CreateTravel";
        } catch (AuthenticationException e) {
            return "redirect:/auth/login";
        }
    }

    @PostMapping("/new")
    public String handleCreateTravel(@Valid @ModelAttribute("travelDto") TravelDto travelDto,
                                   BindingResult errors,
                                   Model model,
                                   HttpSession session) {

        if (errors.hasErrors()) {
            return "CreateTravel";
        }

        try {
            User user = authenticationHelper.tryGetUserFromSession(session);
            Travel travelFromDto = modelsMapper.travelFromDto(travelDto);
            Travel travelToBeCreated = travelService.createTravel(travelFromDto, user);
            int travelId = travelToBeCreated.getTravelId();
            StringBuilder sb = new StringBuilder();
            sb.append("redirect:/travels/").append(travelId);
            return sb.toString();
        } catch (AuthenticationException e) {
            return "redirect:/auth/login";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("error", e.getMessage());
            return "Error";
        }
    }

    @GetMapping("/search")
    public String showAllTravels(@ModelAttribute("filterDto") TravelFilterOptionsDto travelFilterOptionsDto,
                                 HttpSession session,
                                 Model model) {

        User user;
        try {
            user = authenticationHelper.tryGetUserFromSession(session);
        } catch (AuthenticationException e) {
            return "redirect:/auth/login";
        }
        TravelFilterOptions filterOptions = modelsMapper.travelFilterOptionsFromDto(travelFilterOptionsDto);
        List<Travel> travels = travelService.getAllTravels(filterOptions);
        model.addAttribute("travels", travels);

        if (!travels.isEmpty()) {
            Travel travel = travels
                    .stream()
                    .sorted(Comparator.comparing(Travel::getDepartureTime))
                    .limit(1)
                    .toList()
                    .get(0);
            model.addAttribute("departingSoonestTravel", travel);
        } else {
            model.addAttribute("departingSoonestTravel", new Travel());
        }

        model.addAttribute("filterDto", travelFilterOptionsDto);
        return "AllTravels";
    }
    @GetMapping("/{id}")
    public String showSinglePost(@PathVariable int id,
                                 Model model,
                                 HttpSession session) {

        try {
            User loggedInUser = authenticationHelper.tryGetUserFromSession(session);
            Travel travel = travelService.getTravelById(id);
            model.addAttribute("travel", travel);

            TravelFilterOptions travelFilterOptions = new TravelFilterOptions(null, null, null,
                    null, null, null, null, null, null,
                    null);
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

            return "SingleTravel";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "Error";
        } catch (AuthenticationException e) {
            return "redirect:/auth/login";
        }
    }
}
