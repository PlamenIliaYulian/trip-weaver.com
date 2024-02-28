package com.tripweaver.controllers.rest;

import com.tripweaver.controllers.helpers.AuthenticationHelper;
import com.tripweaver.controllers.helpers.contracts.ModelsMapper;
import com.tripweaver.exceptions.*;
import com.tripweaver.models.*;
import com.tripweaver.models.dtos.FeedbackDto;
import com.tripweaver.models.dtos.UserDtoCreate;
import com.tripweaver.models.dtos.UserDtoUpdate;
import com.tripweaver.models.filterOptions.TravelFilterOptions;
import com.tripweaver.models.filterOptions.UserFilterOptions;
import com.tripweaver.services.contracts.AvatarService;
import com.tripweaver.services.contracts.TravelService;
import com.tripweaver.services.contracts.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("api/v1/users")
public class UserRestController {

    private final UserService userService;

    private final TravelService travelService;
    private final AuthenticationHelper authenticationHelper;
    private final ModelsMapper modelsMapper;
    private final AvatarService avatarService;


    @Autowired
    public UserRestController(UserService userService,
                              AuthenticationHelper authenticationHelper,
                              ModelsMapper modelsMapper,
                              AvatarService avatarService,
                              TravelService travelService) {
        this.userService = userService;
        this.authenticationHelper = authenticationHelper;
        this.modelsMapper = modelsMapper;
        this.travelService = travelService;
        this.avatarService = avatarService;
    }

    /*Yuli*/
    /*TODO we should send verification email*/
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public User createUser(@RequestBody @Valid UserDtoCreate userDtoCreate) {
        try {
            User user = modelsMapper.userFromDtoCreate(userDtoCreate);
            return userService.createUser(user);
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    /*Ilia*/
    @PutMapping("/{userId}")
    User updateUser(@PathVariable int userId,
                    @RequestBody UserDtoUpdate userDtoUpdate,
                    @RequestHeader HttpHeaders headers) {
        try {
            User userToBeUpdated = modelsMapper.userFromDtoUpdate(userDtoUpdate, userId);
            User loggedUser = authenticationHelper.tryGetUserFromHeaders(headers);
            return userService.updateUser(userToBeUpdated, loggedUser);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    /*Plamen*/
    @GetMapping("/search")
    public List<User> getAllUsers(@RequestHeader HttpHeaders headers,
                                  @RequestParam(required = false) String username,
                                  @RequestParam(required = false) String email,
                                  @RequestParam(required = false) String phoneNumber,
                                  @RequestParam(required = false) String sortBy,
                                  @RequestParam(required = false) String sortOrder) {
        try {
            User loggedUser = authenticationHelper.tryGetUserFromHeaders(headers);
            UserFilterOptions userFilterOptions = new UserFilterOptions(username, email, phoneNumber, sortBy, sortOrder);
            return userService.getAllUsers(userFilterOptions, loggedUser);
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    /*Yuli*/
    @GetMapping("/{userId}")
    public User getUserById(@RequestHeader HttpHeaders headers,
                            @PathVariable int userId) {
        try {
            authenticationHelper.tryGetUserFromHeaders(headers);
            return userService.getUserById(userId);
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    e.getMessage());
        }
    }

    /*Ilia*/
    @PutMapping("/{userId}/block")
    public User blockUser(@PathVariable int userId,
                          @RequestHeader HttpHeaders headers) {
        try {
            User userToBeBlocked = userService.getUserById(userId);
            User loggedUser = authenticationHelper.tryGetUserFromHeaders(headers);
            return userService.blockUser(userToBeBlocked, loggedUser);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    /*Plamen*/
    @PutMapping("/{userId}/unblock")
    public User unblockUser(@PathVariable int userId,
                            @RequestHeader HttpHeaders httpHeaders) {
        try {
            User loggedUser = authenticationHelper.tryGetUserFromHeaders(httpHeaders);
            User userToBeUnblocked = userService.getUserById(userId);
            return userService.unBlockUser(userToBeUnblocked, loggedUser);
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    /*Ilia*/
    @GetMapping("/count")
    public Long getAllUsersCount() {
        return userService.getAllUsersCount();
    }

    /*Plamen*/
    @GetMapping("/top-10-travel-organizers")
    public List<User> getTopTenTravelOrganizersByRating() {
        return userService.getTopTenTravelOrganizersByRating();
    }

    /*Yuli*/
    @GetMapping("/top-10-passengers")
    public List<User> getTopTenTravelPassengersByRating() {
        return userService.getTopTenTravelPassengersByRating();
    }

    /*Ilia*/
    @PutMapping("/{userId}/avatar")
    public User addAvatar(@PathVariable int userId,
                          @RequestParam("avatar") MultipartFile multipartFile,
                          @RequestHeader HttpHeaders headers) {
        try {
            User user = userService.getUserById(userId);
            User loggedUser = authenticationHelper.tryGetUserFromHeaders(headers);
            String avatarUrl = avatarService.uploadPictureToCloudinary(multipartFile);
            return userService.addAvatar(user, avatarUrl, loggedUser);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /*Plamen*/
    @DeleteMapping("/{userId}/avatar")
    public User deleteAvatar(@PathVariable int userId,
                             @RequestHeader HttpHeaders headers) {
        try {
            User loggedUser = authenticationHelper.tryGetUserFromHeaders(headers);
            User userToBeRemovedAvatarFrom = userService.getUserById(userId);
            return userService.deleteAvatar(userToBeRemovedAvatarFrom, loggedUser);
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    /*Yuli*/
    @PostMapping("/{userId}/travels/{travelId}/feedback-for-driver")
    public Feedback leaveFeedbackForDriver(@RequestHeader HttpHeaders headers,
                                           @PathVariable int userId,
                                           @PathVariable int travelId,
                                           @Valid @RequestBody FeedbackDto feedbackDto) {
        try {
            User loggedInUser = authenticationHelper.tryGetUserFromHeaders(headers);
            User driver = userService.getUserById(userId);
            Travel travel = travelService.getTravelById(travelId);
            Feedback feedbackForDriver = modelsMapper.feedbackForDriverFromDto(feedbackDto);
            return userService.leaveFeedbackForDriver(feedbackForDriver, travel, loggedInUser);
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    e.getMessage());
        } catch (InvalidOperationException e) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    e.getMessage());
        }
    }

    /*Ilia*/
    @PostMapping("/{userId}/travels/{travelId}/feedback-for-passenger")
    public Feedback leaveFeedbackForPassenger(@PathVariable int userId,
                                              @PathVariable int travelId,
                                              @RequestHeader HttpHeaders headers,
                                              @RequestBody FeedbackDto feedbackDto) {
        try {
            User passenger = userService.getUserById(userId);
            User loggedUser = authenticationHelper.tryGetUserFromHeaders(headers);
            Travel travel = travelService.getTravelById(travelId);
            Feedback feedbackForPassenger = modelsMapper.feedbackForPassengerFromDto(feedbackDto);
            return userService.leaveFeedbackForPassenger(feedbackForPassenger, travel, loggedUser);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (InvalidOperationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /*Plamen*/
    @GetMapping("/{driverId}/feedback-for-driver")
    public List<Feedback> getAllFeedbackForDriver(@PathVariable int driverId,
                                                  @RequestHeader HttpHeaders headers) {
        try {
            authenticationHelper.tryGetUserFromHeaders(headers);
            User driver = userService.getUserById(driverId);
            return userService.getAllFeedbackForDriver(driver);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    /*Yuli*/
    @GetMapping("/{userId}/feedback-for-passenger")
    public List<Feedback> getAllFeedbackForPassenger(@PathVariable int userId,
                                                                 @RequestHeader HttpHeaders httpHeaders) {
        try {
            authenticationHelper.tryGetUserFromHeaders(httpHeaders);
            User passenger = userService.getUserById(userId);
            return userService.getAllFeedbackForPassenger(passenger);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    /*Ilia*/
    @GetMapping("/{userId}/travels-for-driver")
    public List<Travel> getTravelsByDriver(@PathVariable int userId,
                                           @RequestHeader HttpHeaders headers,
                                           @RequestParam(required = false) String startingPoint,
                                           @RequestParam(required = false) String endingPoint,
                                           @RequestParam(required = false) String departureBefore,
                                           @RequestParam(required = false) String departureAfter,
                                           @RequestParam(required = false) Integer minFreeSeats,
                                           @RequestParam(required = false) String driverUsername,
                                           @RequestParam(required = false) String commentContains,
                                           @RequestParam(required = false) Integer statusId,
                                           @RequestParam(required = false) String sortBy,
                                           @RequestParam(required = false) String sortOrder) {
        try {
            User user = userService.getUserById(userId);
            User loggedUser = authenticationHelper.tryGetUserFromHeaders(headers);
            TravelFilterOptions filterOptions = new TravelFilterOptions(
                    startingPoint, endingPoint, departureBefore, departureAfter, minFreeSeats,
                    driverUsername, commentContains, statusId, sortBy, sortOrder);
            return travelService.getTravelsByDriver(user, loggedUser, filterOptions);
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /*Plamen*/
    @GetMapping("/{userId}/travels-for-passenger")
    public List<Travel> getTravelsByPassenger(@PathVariable int userId,
                                              @RequestHeader HttpHeaders headers,
                                              @RequestParam(required = false) String startingPoint,
                                              @RequestParam(required = false) String endingPoint,
                                              @RequestParam(required = false) String departureBefore,
                                              @RequestParam(required = false) String departureAfter,
                                              @RequestParam(required = false) Integer minFreeSeats,
                                              @RequestParam(required = false) String driverUsername,
                                              @RequestParam(required = false) String commentContains,
                                              @RequestParam(required = false) Integer statusId,
                                              @RequestParam(required = false) String sortBy,
                                              @RequestParam(required = false) String sortOrder
    ) {
        try {
            TravelFilterOptions travelFilterOptions = new TravelFilterOptions(
                    startingPoint,
                    endingPoint,
                    departureBefore,
                    departureAfter,
                    minFreeSeats,
                    driverUsername,
                    commentContains,
                    statusId,
                    sortBy,
                    sortOrder);
            User loggedUser = authenticationHelper.tryGetUserFromHeaders(headers);
            User passenger = userService.getUserById(userId);
            return travelService.getTravelsByPassenger(passenger, loggedUser, travelFilterOptions);
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }


}
