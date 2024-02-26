package com.tripweaver.controllers.rest;

import com.tripweaver.controllers.helpers.AuthenticationHelper;
import com.tripweaver.controllers.helpers.contracts.ModelsMapper;
import com.tripweaver.models.FeedbackForDriver;
import com.tripweaver.models.FeedbackForPassenger;
import com.tripweaver.models.Travel;
import com.tripweaver.models.User;
import com.tripweaver.models.filterOptions.UserFilterOptions;
import com.tripweaver.models.dtos.UserDto;
import com.tripweaver.services.contracts.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/users")
public class UserRestController {

    private final UserService userService;
    private final AuthenticationHelper authenticationHelper;
    private final ModelsMapper modelsMapper;

    public UserRestController(UserService userService,
                              AuthenticationHelper authenticationHelper,
                              ModelsMapper modelsMapper) {
        this.userService = userService;
        this.authenticationHelper = authenticationHelper;
        this.modelsMapper = modelsMapper;
    }

    /*Yuli*/
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public User createUser(@RequestBody @Valid UserDto userDto) {
        return null;
    }

    /*Ilia*/
    @PutMapping("/{userId}")
    User updateUser(@PathVariable int id) {
        return null;
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
    @GetMapping("/{userId}")
    public User getUserById(@PathVariable int id) {

        return userService.getUserById(id);
    }

    /*Ilia*/
    @PutMapping("/{userId}/block")
    public User blockUser(@PathVariable int id) {

        return userService.getUserById(id);
    }

    /*Plamen*/
    @PutMapping("/{userId}/unblock")
    public User unblockUser(@PathVariable int id) {

        return userService.getUserById(id);
    }

    /*Ilia*/
    @GetMapping("/count")
    public Long getAllUsersCount() {

        return null;
    }

    /*Plamen*/
    @GetMapping("/top-10-travel-organizers")
    public List<User> getTopTenTravelOrganizersByRating() {
        return null;
    }

    /*Yuli*/
    @GetMapping("/top-10-passengers")
    public List<User> getTopTenTravelPassengersByRating() {
        return null;
    }

    /*Ilia*/
    @PutMapping("/{userId}/avatar")
    public User addAvatar() {
        return null;
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
    @PostMapping("/{userId}/feedback-for-passenger")
    public User leaveFeedbackForPassenger() {
        return null;
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
    @GetMapping("/{userId}/travels-as-driver")
    public List<Travel> getTravelsByDriver() {
        return null;
    }

    /*Plamen - we agreed we can use TravelService*/
    @GetMapping("/{userId}/travels-as-passenger")
    public List<Travel> getTravelsByPassenger() {
        return null;
    }


}
