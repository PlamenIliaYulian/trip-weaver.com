package com.tripweaver.controllers.mvc;

import com.tripweaver.controllers.helpers.AuthenticationHelper;
import com.tripweaver.controllers.helpers.contracts.ModelsMapper;
import com.tripweaver.exceptions.AuthenticationException;
import com.tripweaver.exceptions.EntityNotFoundException;
import com.tripweaver.exceptions.InvalidOperationException;
import com.tripweaver.exceptions.UnauthorizedOperationException;
import com.tripweaver.models.Feedback;
import com.tripweaver.models.Travel;
import com.tripweaver.models.User;
import com.tripweaver.models.dtos.FeedbackDto;
import com.tripweaver.models.dtos.TravelDto;
import com.tripweaver.models.dtos.TravelFilterOptionsDto;
import com.tripweaver.models.filterOptions.TravelFilterOptions;
import com.tripweaver.services.contracts.RoleService;
import com.tripweaver.services.contracts.TravelService;
import com.tripweaver.services.contracts.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.hibernate.Session;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

import static com.tripweaver.services.helpers.ConstantHelper.*;

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
                        .contains(roleService.getRoleById(ADMIN_ID)));
    }

    @ModelAttribute("isBlocked")
    public boolean populateIsBlocked(HttpSession httpSession) {
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
                                          Model model) {
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

        try {
            authenticationHelper.tryGetUserFromSession(session);
        } catch (AuthenticationException e) {
            return "redirect:/auth/login";
        }
        TravelFilterOptions filterOptions = modelsMapper.travelFilterOptionsFromDto(travelFilterOptionsDto);
        filterOptions.setStatusId(Optional.of(TRAVEL_STATUS_CREATED_ID));
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
    public String showSingleTravel(@PathVariable int id,
                                   Model model,
                                   HttpSession session) {
        try {

            User loggedUser = authenticationHelper.tryGetUserFromSession(session);
            Travel travel = travelService.getTravelById(id);
            model.addAttribute("travel", travel);


            List<User> listOfApprovedAndAppliedPassengers = new ArrayList<>();
            listOfApprovedAndAppliedPassengers.addAll(travel.getUsersAppliedForTheTravel());
            listOfApprovedAndAppliedPassengers.addAll(travel.getUsersApprovedForTheTravel());
            model.addAttribute("passengerTotalDistance",
                    userService.getTotalDistanceAsPassengerHashMap(listOfApprovedAndAppliedPassengers));
            model.addAttribute("passengerTotalTravels",
                    userService.getTotalTravelsAsPassengerHashMap(listOfApprovedAndAppliedPassengers));
            model.addAttribute("isLoggedUserApprovedPassenger",
                    listOfApprovedAndAppliedPassengers.contains(loggedUser));
            model.addAttribute("feedbackForm", new FeedbackDto());
            model.addAttribute("hasLoggedUserAlreadyLeftFeedback", !travel.getDriver().getFeedback().stream()
                    .filter(feedback -> feedback.getAuthor().equals(loggedUser))
                    .filter(feedback -> feedback.getTravel().equals(travel))
                    .filter(feedback -> feedback.getReceiver().equals(travel.getDriver()))
                    .collect(Collectors.toList()).isEmpty());

            HashMap<String, Boolean> hasDriverLeftReviewForPassengers = new HashMap<>();
            for (User user : travel.getUsersApprovedForTheTravel()) {
                hasDriverLeftReviewForPassengers.put(user.getUsername(), user.getFeedback()
                        .stream()
                        .filter(feedback -> feedback.getTravel().equals(travel))
                        .filter(feedback -> feedback.getReceiver().equals(user))
                        .filter(feedback -> feedback.getAuthor().equals(travel.getDriver()))
                        .collect(Collectors.toList()).isEmpty());
            }
            model.addAttribute("hasLoggedDriverAlreadyLeftFeedbackHashMap", hasDriverLeftReviewForPassengers);

            return "SingleTravel";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "Error";
        } catch (AuthenticationException e) {
            return "redirect:/auth/login";
        }
    }

    @GetMapping("/{travelId}/applications/user/{userId}/approve")
    public String approvePassenger(@PathVariable int travelId,
                                   @PathVariable int userId,
                                   HttpSession session,
                                   Model model) {
        try {
            User loggedUser = authenticationHelper.tryGetUserFromSession(session);
            Travel travel = travelService.getTravelById(travelId);
            User userToBeApproved = userService.getUserById(userId);
            travelService.approvePassenger(userToBeApproved, loggedUser, travel);

            StringBuilder sb = new StringBuilder();
            sb.append("redirect:/travels/").append(travelId);
            return sb.toString();

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
        } catch (InvalidOperationException e) {
            model.addAttribute("statusCode", HttpStatus.BAD_REQUEST.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "Error";
        }
    }

    @GetMapping("/{travelId}/applications/user/{userId}/decline")
    public String declinePassenger(@PathVariable int travelId,
                                   @PathVariable int userId,
                                   HttpSession session,
                                   Model model) {

        try {
            User loggedUser = authenticationHelper.tryGetUserFromSession(session);
            Travel travel = travelService.getTravelById(travelId);
            User userToBeDeclined = userService.getUserById(userId);
            travelService.declinePassenger(userToBeDeclined, travel, loggedUser);

            StringBuilder sb = new StringBuilder();
            sb.append("redirect:/travels/").append(travelId);
            return sb.toString();

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
        } catch (InvalidOperationException e) {
            model.addAttribute("statusCode", HttpStatus.BAD_REQUEST.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "Error";
        }
    }

    @GetMapping("/{travelId}/passengers/{passengerId}/cancel-participation")
    public String cancelParticipationAsPassenger(@PathVariable int travelId,
                                                 @PathVariable int passengerId,
                                                 HttpServletRequest servletRequest,
                                                 HttpSession session,
                                                 Model model) {
        try {
            User loggedUser = authenticationHelper.tryGetUserFromSession(session);
            User userToBeDeclined = userService.getUserById(passengerId);
            Travel travel = travelService.getTravelById(travelId);
            travelService.declinePassenger(userToBeDeclined, travel, loggedUser);

            String referer = servletRequest.getHeader("Referer");
            URI refererUri = new URI(referer);
            return "redirect:" + refererUri.getPath();
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
        } catch (Exception e) {
            model.addAttribute("statusCode", HttpStatus.BAD_REQUEST.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "Error";
        }
    }

    @GetMapping("/{travelId}/complete-travel")
    public String completeTravel(@PathVariable int travelId,
                                 HttpServletRequest servletRequest,
                                 HttpSession session,
                                 Model model) {
        try {
            User loggedInUser = authenticationHelper.tryGetUserFromSession(session);
            Travel travelToMarkAsCompleted = travelService.getTravelById(travelId);
            travelService.completeTravel(travelToMarkAsCompleted, loggedInUser);

            String referer = servletRequest.getHeader("Referer");
            URI refererUri = new URI(referer);
            return "redirect:" + refererUri.getPath();
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
        } catch (Exception e) {
            model.addAttribute("statusCode", HttpStatus.BAD_REQUEST.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "Error";
        }
    }

    @GetMapping("/{travelId}/cancel-travel")
    public String cancelTravel(@PathVariable int travelId,
                               HttpServletRequest servletRequest,
                               HttpSession session,
                               Model model) {
        try {
            User loggedInUser = authenticationHelper.tryGetUserFromSession(session);
            Travel travelToMarkAsCompleted = travelService.getTravelById(travelId);
            travelService.cancelTravel(travelToMarkAsCompleted, loggedInUser);

            String referer = servletRequest.getHeader("Referer");
            URI refererUri = new URI(referer);
            return "redirect:" + refererUri.getPath();
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
        } catch (Exception e) {
            model.addAttribute("statusCode", HttpStatus.BAD_REQUEST.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "Error";
        }
    }

    @PostMapping("/{travelId}/driver/{driverId}/feedback-for-driver")
    public String leaveFeedbackForDriver(@PathVariable int driverId,
                                         @PathVariable int travelId,
                                         Model model,
                                         HttpSession session,
                                         @Valid @ModelAttribute("feedbackForm") FeedbackDto feedbackDto,
                                         BindingResult errors) {


        try {
            if (errors.hasErrors()) {
                return "redirect:/travels/{travelId}";
            }

            User passenger = authenticationHelper.tryGetUserFromSession(session);
            User driver = userService.getUserById(driverId);
            Travel travel = travelService.getTravelById(travelId);
            Feedback feedbackForDriver = modelsMapper.feedbackForDriverFromDto(feedbackDto);
            userService.leaveFeedbackForDriver(feedbackForDriver, travel, passenger, driver);

            return "redirect:/travels/{travelId}";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "Error";
        } catch (AuthenticationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "Error";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("statusCode", HttpStatus.FORBIDDEN.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "Error";
        } catch (InvalidOperationException e) {
            model.addAttribute("statusCode", HttpStatus.CONFLICT.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "Error";
        }
    }

    @PostMapping("/{travelId}/passengers/{passengerId}/feedback-for-passenger")
    public String leaveFeedbackForPassenger(@PathVariable int passengerId,
                                            @PathVariable int travelId,
                                            Model model,
                                            HttpSession session,
                                            @Valid @ModelAttribute("feedbackForm") FeedbackDto feedbackDto,
                                            BindingResult errors) {


        try {
            if (errors.hasErrors()) {
                return "redirect:/travels/{travelId}";
            }

            User driver = authenticationHelper.tryGetUserFromSession(session);
            User passenger = userService.getUserById(passengerId);
            Travel travel = travelService.getTravelById(travelId);
            Feedback feedbackForPassenger = modelsMapper.feedbackForPassengerFromDto(feedbackDto);
            userService.leaveFeedbackForPassenger(feedbackForPassenger, travel, driver, passenger);

            return "redirect:/travels/{travelId}";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "Error";
        } catch (AuthenticationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "Error";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("statusCode", HttpStatus.FORBIDDEN.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "Error";
        } catch (InvalidOperationException e) {
            model.addAttribute("statusCode", HttpStatus.CONFLICT.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "Error";
        }
    }

    @GetMapping("/{travelId}/apply")
    public String applyForATrip(HttpSession session,
                                @PathVariable int travelId,
                                Model model) {
        try {
            User loggedInUser = authenticationHelper.tryGetUserFromSession(session);
            Travel travelToApplyFor = travelService.getTravelById(travelId);
            travelService.applyForATrip(loggedInUser, travelToApplyFor);

            return "redirect:/travels/{travelId}";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "Error";
        } catch (AuthenticationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "Error";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("statusCode", HttpStatus.FORBIDDEN.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "Error";
        } catch (InvalidOperationException e) {
            model.addAttribute("statusCode", HttpStatus.CONFLICT.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "Error";
        }
    }
}
