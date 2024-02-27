package com.tripweaver.controllers.rest;

import com.tripweaver.controllers.helpers.AuthenticationHelper;
import com.tripweaver.controllers.helpers.contracts.ModelsMapper;
import com.tripweaver.exceptions.AuthenticationException;
import com.tripweaver.exceptions.DuplicateEntityException;
import com.tripweaver.exceptions.EntityNotFoundException;
import com.tripweaver.exceptions.UnauthorizedOperationException;
import com.tripweaver.exceptions.*;
import com.tripweaver.models.FeedbackForDriver;
import com.tripweaver.models.FeedbackForPassenger;
import com.tripweaver.models.Travel;
import com.tripweaver.models.User;
import com.tripweaver.models.dtos.FeedbackDto;
import com.tripweaver.models.dtos.UserDtoUpdate;
import com.tripweaver.models.filterOptions.TravelFilterOptions;
import com.tripweaver.models.filterOptions.UserFilterOptions;
import com.tripweaver.models.dtos.UserDto;
import com.tripweaver.services.contracts.AvatarService;
import com.tripweaver.services.contracts.TravelService;
import com.tripweaver.services.contracts.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("api/v1/users")
public class UserRestController {

    private final UserService userService;
    private final AuthenticationHelper authenticationHelper;
    private final ModelsMapper modelsMapper;
    private final AvatarService avatarService;
    private final TravelService travelService;

    public UserRestController(UserService userService,
                              AuthenticationHelper authenticationHelper,
                              ModelsMapper modelsMapper,
                              AvatarService avatarService,
                              TravelService travelService) {
        this.userService = userService;
        this.authenticationHelper = authenticationHelper;
        this.modelsMapper = modelsMapper;
        this.avatarService = avatarService;
        this.travelService = travelService;
    }

    /*Yuli*/
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public User createUser(@RequestBody @Valid UserDto userDto) {
        try {
            User user = modelsMapper.userFromDto(userDto);
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
                                  @RequestParam(required = false) String firstName,
                                  @RequestParam(required = false) String sortBy,
                                  @RequestParam(required = false) String sortOrder) {

        return userService.getAllUsers(new UserFilterOptions(null, null, null, null, null), new User());
    }

    /*Yuli*/
    /*The public part of the project does not say anything about allowing users to browse other users
     * without logging in. Hence - made this endpoint require validation.*/
    @GetMapping("/{userId}")
    public User getUserById(@RequestHeader HttpHeaders headers,
                            @PathVariable int userId) {
        try {
            authenticationHelper.tryGetUser(headers);
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
    public User unblockUser(@PathVariable int id) {

        return userService.getUserById(id);
    }

    /*Ilia*/
    @GetMapping("/count")
    public Long getAllUsersCount() {
        return userService.getAllUsersCount();
    }

    /*Plamen*/
    @GetMapping("/top-10-travel-organizers")
    public List<User> getTopTenTravelOrganizersByRating() {
        return null;
    }

    /*Yuli*/
    @GetMapping("/top-10-passengers")
    public List<User> getTopTenTravelPassengersByRating() {
        return userService.getTopTenTravelPassengersByRating();
    }

    /*Ilia*/
    /*ToDo This userId is unnecessary because we can use endpoint where
    *  you only have to be login. We get your details from headers/session
    *  and then change your picture.*/
    @PutMapping("/{userId}/avatar")
    public User addAvatar(@PathVariable int userId,
                          @RequestParam("avatar")MultipartFile multipartFile,
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
    public User deleteAvatar() {
        return null;
    }

    /*Yuli*/
    @PostMapping("/{userId}/feedback-for-driver")
    public User leaveFeedbackForDriver() {
        return null;
    }

    /*Ilia*/
    /*ToDo FeedbackDto must be changed. We either point the receiver of the feedback
    *  through endpoint or through the body. We do not need the two at the same time.*/

    /*ToDo This method is not ready. Have to discuss how to get the travel to pass it
    *  to the service.*/
    @PostMapping("/{userId}/feedback-for-passenger")
    public User leaveFeedbackForPassenger(@PathVariable int userId,
                                          @RequestHeader HttpHeaders headers,
                                          @RequestBody FeedbackDto feedbackDto) {

        try {
            User user = userService.getUserById(userId);
            User loggedUser = authenticationHelper.tryGetUserFromHeaders(headers);
            FeedbackForPassenger feedbackForPassenger = modelsMapper.feedbackForPassengerFromDto(feedbackDto);
           /* return userService.leaveFeedbackForPassenger(
                    feedbackForPassenger,
                    travel,
                    loggedUser,
                    user);*/
            return null;
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
    @GetMapping("/{userId}/feedback-for-driver")
    public List<FeedbackForDriver> getAllFeedbackForDriver() {
        return null;
    }

    /*Yuli*/
    @GetMapping("/{userId}/feedback-for-passenger")
    public List<FeedbackForPassenger> getAllFeedbackForPassenger() {
        return null;
    }

    /*Ilia - we agreed we can use TravelService*/
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
        }
    }

    /*Plamen - we agreed we can use TravelService*/
    @GetMapping("/{userId}/travels-for-passenger")
    public List<Travel> getTravelsByPassenger() {
        return null;
    }

}
