package com.tripweaver.controllers.rest;

import com.tripweaver.controllers.helpers.AuthenticationHelper;
import com.tripweaver.controllers.helpers.contracts.ModelsMapper;
import com.tripweaver.exceptions.*;
import com.tripweaver.models.Feedback;
import com.tripweaver.models.Travel;
import com.tripweaver.models.User;
import com.tripweaver.models.dtos.FeedbackDto;
import com.tripweaver.models.dtos.UserDto;
import com.tripweaver.models.dtos.UserDtoCreate;
import com.tripweaver.models.filterOptions.TravelFilterOptions;
import com.tripweaver.models.filterOptions.UserFilterOptions;
import com.tripweaver.services.contracts.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static com.tripweaver.services.helpers.ConstantHelper.CONFIRM_PASSWORD_SHOULD_MATCH_PASSWORD;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User")
public class UserRestController {

    private final UserService userService;
    private final MailSenderService mailService;
    private final TravelService travelService;
    private final AuthenticationHelper authenticationHelper;
    private final ModelsMapper modelsMapper;
    private final AvatarService avatarService;
    private final CarPictureService carPictureService;

    @Autowired
    public UserRestController(UserService userService, MailSenderService mailService,
                              AuthenticationHelper authenticationHelper,
                              ModelsMapper modelsMapper,
                              AvatarService avatarService,
                              TravelService travelService,
                              CarPictureService carPictureService) {
        this.userService = userService;
        this.mailService = mailService;
        this.authenticationHelper = authenticationHelper;
        this.modelsMapper = modelsMapper;
        this.travelService = travelService;
        this.avatarService = avatarService;
        this.carPictureService = carPictureService;
    }

    @Operation(
            summary = "Create new user.",
            description = "This endpoint is used for the creation / registration of new users in the system.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = """
                            The body of the request consists of the following fields:
                                'username',
                                'password',
                                'confirmPassword',
                                'firstName',
                                'lastName',
                                'email',
                                'phoneNumber',
                            """),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Created",
                            content = @Content(
                                    schema = @Schema(implementation = User.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request",
                            content = @Content(
                                    examples = {
                                            @ExampleObject(
                                                    name = "Bad request",
                                                    value = "Bad request",
                                                    description = "Confirm password should match password.")
                                    },
                                    mediaType = "Plain text")
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Conflict",
                            content = @Content(
                                    examples = {
                                            @ExampleObject(
                                                    name = "Conflict",
                                                    value = "Conflict",
                                                    description = "A user with the same username, email or phone number already exists in the system.")
                                    },
                                    mediaType = "Plain text")
                    )
            }
    )
    /*Yuli*/
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public User createUser(@RequestBody @Valid UserDtoCreate userDtoCreate) {
        try {
            if (!userDtoCreate.getPassword().equals(userDtoCreate.getConfirmPassword())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, CONFIRM_PASSWORD_SHOULD_MATCH_PASSWORD);
            }
            User user = modelsMapper.userFromDtoCreate(userDtoCreate);
            user = userService.createUser(user);
            mailService.sendEmail(user);
            return user;
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Operation(
            summary = "Updates the information of the user found by the numeric ID.",
            description = "Used to update User's first name, last name, email, phone number or password.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Body consists of the password, confirmation password, first name, last name, phone number and the email of the user."),
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "ID must be numeric.",
                            example = "/api/v1/users/3"
                    ),
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Success response.",
                            content = @Content(
                                    schema = @Schema(implementation = User.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Missing Authentication.",
                            content = @Content(
                                    examples = {
                                            @ExampleObject(
                                                    name = "Not authenticated",
                                                    value = "The requested resource requires authentication.",
                                                    description = "You need to be authenticated to update a User information.")
                                    },
                                    mediaType = "Plain text")
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Not authorized.",
                            content = @Content(
                                    examples = {
                                            @ExampleObject(
                                                    name = "Logged user not the same",
                                                    value = "Unauthorized operation.",
                                                    description = "The logged user who is updating the info is not the same" +
                                                            "as the one who will be updated.")
                                    },
                                    mediaType = "Plain text")
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found status.",
                            content = @Content(
                                    examples = {
                                            @ExampleObject(
                                                    name = "Missing user",
                                                    value = "User with ID '100' not found.",
                                                    description = "There is no such user with the provided ID.")
                                    },
                                    mediaType = "Plain text")
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Conflict.",
                            content = @Content(
                                    examples = {
                                            @ExampleObject(
                                                    name = "Email",
                                                    value = "Duplication exist. Email has to be unique.",
                                                    description = "Provided email already exists in the database."),
                                            @ExampleObject(
                                                    name = "Phone number",
                                                    value = "Duplication exist. Phone number has to be unique.",
                                                    description = "Provided phone number already exists in the database.")

                                    },
                                    mediaType = "Plain text")
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request.",
                            content = @Content(
                                    examples = {
                                            @ExampleObject(
                                                    name = "Travel status not 'created'",
                                                    value = "Invalid operation.",
                                                    description = "The travel has a status different from 'CREATED'."),
                                            @ExampleObject(
                                                    name = "User not applied",
                                                    value = "Invalid operation.",
                                                    description = "User to be approved has not applied for the travel.")
                                    },
                                    mediaType = "Plain text")
                    )
            },
            security = {@SecurityRequirement(name = "Authorization")}
    )
    @PutMapping("/{userId}")
    User updateUser(@PathVariable int userId,
                    @Valid @RequestBody UserDto userDto,
                    @RequestHeader HttpHeaders headers) {
        try {
            if (!userDto.getPassword().equals(userDto.getConfirmPassword())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, CONFIRM_PASSWORD_SHOULD_MATCH_PASSWORD);
            }
            User userToBeUpdated = modelsMapper.userFromDto(userDto, userId);
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
    @Operation(
            summary = "View all users registered in the application with the option to filter and sort them.",
            description = "Get a list of all users. Also you can filter and sort them.",
            parameters = {
                    @Parameter(
                            name = "username",
                            description = "Gets the user with a given 'username'",
                            example = "John_doe"),
                    @Parameter(
                            name = "email",
                            description = "Gets the user with a given 'email'",
                            example = "email@email.com"),
                    @Parameter(
                            name = "phoneNumber",
                            description = "Gets the user with a given 'phoneNumber'",
                            example = "0888123345"),
                    @Parameter(
                            name = "sortBy",
                            description = "You can choose to sort the users list by 'username', 'email', 'phoneNumber', 'createdOn' or 'createdBy'.",
                            example = "desc"),
                    @Parameter(
                            name = "sortOrder",
                            description = "You can choose to sort the travel list in descending order by typing 'desc'. The default is an ascending order.",
                            example = "desc"),
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Success Response"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Missing Authentication.",
                            content = @Content(
                                    examples = {
                                            @ExampleObject(
                                                    name = "Not authenticated", value = "The requested resource requires authentication.",
                                                    description = "You need to be authenticated to view the list of all posts.")
                                    },
                                    mediaType = "Plain text")
                    )
            })
    @SecurityRequirement(name = "Authorization")
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

    @Operation(
            summary = "Retrieves the information of a specific user.",
            description = "This endpoint is used for retrieving the details of a specific user registered in the system.",
            parameters = {
                    @Parameter(
                            name = "userId",
                            description = "user ID must be numeric.",
                            example = "3"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "The request has been completed successfully by the server.",
                            content = @Content(
                                    schema = @Schema(implementation = User.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Missing Authentication.",
                            content = @Content(
                                    examples = {
                                            @ExampleObject(
                                                    name = "Not authenticated",
                                                    value = "The requested resource requires authentication.",
                                                    description = "You need to be authenticated to retrieve the information of an existing user.")
                                    },
                                    mediaType = "Plain text")
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found status.",
                            content = @Content(
                                    examples = {
                                            @ExampleObject(
                                                    name = "Missing user",
                                                    value = "User with ID '{userId}' not found.",
                                                    description = "There is no user that corresponds to the provided user ID.")
                                    },
                                    mediaType = "Plain text")
                    )
            },
            security = {@SecurityRequirement(name = "Authorization")}
    )
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

    @Operation(
            summary = "Blocks a user found by the numeric ID.",
            description = "Used to from admin of the system to block a certain user by his/her numeric ID.",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "ID must be numeric.",
                            example = "/api/v1/users/3/block"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Success response.",
                            content = @Content(
                                    schema = @Schema(implementation = User.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Missing Authentication.",
                            content = @Content(
                                    examples = {
                                            @ExampleObject(
                                                    name = "Not authenticated",
                                                    value = "The requested resource requires authentication.",
                                                    description = "You need to be authenticated to update a User information.")
                                    },
                                    mediaType = "Plain text")
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Not authorized.",
                            content = @Content(
                                    examples = {
                                            @ExampleObject(
                                                    name = "Not admin",
                                                    value = "Unauthorized operation.",
                                                    description = "You have to admin to block another user.")
                                    },
                                    mediaType = "Plain text")
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found status.",
                            content = @Content(
                                    examples = {
                                            @ExampleObject(
                                                    name = "Missing user",
                                                    value = "User with ID '100' not found.",
                                                    description = "There is no such user with the provided ID.")
                                    },
                                    mediaType = "Plain text")
                    )
            },
            security = {@SecurityRequirement(name = "Authorization")}
    )
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
    @Operation(
            summary = "Unblocks a user found by the numeric ID.",
            description = "Used from admin of the system to unblock a certain user by his/her numeric ID.",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "ID must be numeric.",
                            example = "/api/v1/users/3/block"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Success response.",
                            content = @Content(
                                    schema = @Schema(implementation = User.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Missing Authentication.",
                            content = @Content(
                                    examples = {
                                            @ExampleObject(
                                                    name = "Not authenticated",
                                                    value = "The requested resource requires authentication.",
                                                    description = "You need to be authenticated to update a User information.")
                                    },
                                    mediaType = "Plain text")
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Not authorized.",
                            content = @Content(
                                    examples = {
                                            @ExampleObject(
                                                    name = "Not admin",
                                                    value = "Unauthorized operation.",
                                                    description = "You have to admin to block another user.")
                                    },
                                    mediaType = "Plain text")
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found status.",
                            content = @Content(
                                    examples = {
                                            @ExampleObject(
                                                    name = "Missing user",
                                                    value = "User with ID '100' not found.",
                                                    description = "There is no such user with the provided ID.")
                                    },
                                    mediaType = "Plain text")
                    )
            },
            security = {@SecurityRequirement(name = "Authorization")}
    )
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

    @Operation(
            summary = "Pulls from the database the count of all created users in the system.",
            description = "Used to obtain the total amount of users registered in the system.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Success Response"
                    )
            })
    @GetMapping("/count")
    public Long getAllUsersCount() {
        return userService.getAllUsersCount();
    }

    /*Plamen*/
    @Operation(
            summary = "Pulls from the database the top twelve travel organizers of all created users in the system.",
            description = "Used to obtain the top twelve travel organizers registered in the system.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Success Response"
                    )
            })
    @GetMapping("/top-12-travel-organizers")
    public List<User> getTopTwelveTravelOrganizersByRating() {
        return userService.getTopTwelveTravelOrganizersByRating();
    }

    @Operation(
            summary = "Get the 12 highest rated passengers.",
            description = "Retrieves information related to the top 12 highest rated (highest passenger rating) passengers in the system.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Created",
                            content = @Content(array = @ArraySchema(
                                    schema = @Schema(implementation = User.class)))
                    )
            }
    )
    /*Yuli*/
    @GetMapping("/top-12-passengers")
    public List<User> getTopTwelveTravelPassengersByRating() {
        return userService.getTopTwelveTravelPassengersByRating();
    }

    @Operation(
            summary = "Uploads an avatar / profile picture to user's profile.",
            description = "Used to update user's profile by updating his/her avatar / profile picture.",
            parameters = {
                    @Parameter(name = "userId",
                            description = "ID of the user whose profile will be updated.",
                            example = "/api/v1/users/3/avatar"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Success response.",
                            content = @Content(
                                    schema = @Schema(implementation = User.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Not authorized.",
                            content = @Content(
                                    examples = {
                                            @ExampleObject(
                                                    name = "Logged user not the same",
                                                    value = "Unauthorized operation.",
                                                    description = "The logged user who is adding the avatar is not the same" +
                                                            "as the one who will be updated.")
                                    },
                                    mediaType = "Plain text")
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Missing Authentication.",
                            content = @Content(
                                    examples = {
                                            @ExampleObject(
                                                    name = "Not authenticated",
                                                    value = "The requested resource requires authentication.",
                                                    description = "You need to be authenticated to update a User information.")
                                    },
                                    mediaType = "Plain text")
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found status.",
                            content = @Content(
                                    examples = {
                                            @ExampleObject(
                                                    name = "Missing user",
                                                    value = "User with ID '100' not found.",
                                                    description = "There is no such user with the provided ID.")
                                    },
                                    mediaType = "Plain text")
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request.",
                            content = @Content(
                                    examples = {
                                            @ExampleObject(
                                                    name = "Uploading picture",
                                                    value = "Error during uploading the picture.",
                                                    description = "The provided picture failed to be uploaded " +
                                                            "because the size is too large.")
                                    },
                                    mediaType = "Plain text")
                    )

            },
            security = {@SecurityRequirement(name = "Authorization")}
    )
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
    @Operation(
            summary = "Deletes an avatar / profile picture from user's profile.",
            description = "Used to update user's profile by deleting his/her avatar / profile picture.",
            parameters = {
                    @Parameter(name = "userId",
                            description = "ID of the user whose profile will be updated.",
                            example = "/api/v1/users/3/avatar"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Success response.",
                            content = @Content(
                                    schema = @Schema(implementation = User.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Not authorized.",
                            content = @Content(
                                    examples = {
                                            @ExampleObject(
                                                    name = "Logged user not the same",
                                                    value = "Unauthorized operation.",
                                                    description = "The logged user who is adding the avatar is not the same" +
                                                            "as the one who will be updated.")
                                    },
                                    mediaType = "Plain text")
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Missing Authentication.",
                            content = @Content(
                                    examples = {
                                            @ExampleObject(
                                                    name = "Not authenticated",
                                                    value = "The requested resource requires authentication.",
                                                    description = "You need to be authenticated to update a User information.")
                                    },
                                    mediaType = "Plain text")
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found status.",
                            content = @Content(
                                    examples = {
                                            @ExampleObject(
                                                    name = "Missing user",
                                                    value = "User with ID '100' not found.",
                                                    description = "There is no such user with the provided ID.")
                                    },
                                    mediaType = "Plain text")
                    ),

            },
            security = {@SecurityRequirement(name = "Authorization")}
    )
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

    @Operation(
            summary = "Leave a feedback to a passenger.",
            description = "Driver of the travel leaves a feedback to one of his passengers of a certain travel. " +
                    "Numeric ID of the passenger and numeric ID of the travel have to be provided in the endpoint.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Body consists of a rating [0-5] and a content (optional) of the feedback."),
            parameters = {
                    @Parameter(name = "id",
                            description = "Specific id to search in the system",
                            example = "/api/v1/users/3/travels/20/feedback-for-passenger"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Success response.",
                            content = @Content(schema = @Schema(implementation = Feedback.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Not authorized.",
                            content = @Content(
                                    examples = {
                                            @ExampleObject(
                                                    name = "User not the driver",
                                                    value = "Unauthorized operation.",
                                                    description = "User leaving feedback for passenger is not the driver" +
                                                            "of the travel.")
                                    },
                                    mediaType = "Plain text")
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found status.",
                            content = @Content(
                                    examples = {
                                            @ExampleObject(
                                                    name = "Missing user",
                                                    value = "User with ID '100' not found.",
                                                    description = "There is no such user with the provided ID."),
                                            @ExampleObject(
                                                    name = "Missing travel",
                                                    value = "Travel with ID '200' not found.",
                                                    description = "There is no such travel with the provided ID.")

                                    },
                                    mediaType = "Plain text")
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Missing Authentication.",
                            content = @Content(
                                    examples = {
                                            @ExampleObject(
                                                    name = "Not authenticated",
                                                    value = "The requested resource requires authentication.",
                                                    description = "You need to be authenticated to update a User information.")
                                    },
                                    mediaType = "Plain text")
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request.",
                            content = @Content(
                                    examples = {
                                            @ExampleObject(
                                                    name = "Travel status not 'completed'",
                                                    value = "Invalid operation.",
                                                    description = "The travel has a status different from 'COMPLETED'."),
                                            @ExampleObject(
                                                    name = "User not approved",
                                                    value = "Invalid operation.",
                                                    description = "The user to be left feedback for is not on the " +
                                                            "approved list for the travel."),
                                            @ExampleObject(
                                                    name = "Feedback left",
                                                    value = "Invalid operation.",
                                                    description = "Feedback for this passenger on this travel " +
                                                            "has already been submitted.")
                                    },
                                    mediaType = "Plain text")
                    )
            },
            security = {@SecurityRequirement(name = "Authorization")}
    )
    @ResponseStatus(HttpStatus.CREATED)
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
            return userService.leaveFeedbackForPassenger(feedbackForPassenger, travel, loggedUser, passenger);
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

    @Operation(
            summary = "Leave a feedback to a driver.",
            description = "A passenger of the travel leaves a feedback to the driver of a certain travel. " +
                    "Numeric ID of the driver and numeric ID of the travel have to be provided in the endpoint.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Body consists of a rating [0-5] and a content (optional) of the feedback."),
            parameters = {
                    @Parameter(name = "id",
                            description = "Specific id to search in the system",
                            example = "/api/v1/users/3/travels/20/feedback-for-driver"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Success response.",
                            content = @Content(schema = @Schema(implementation = Feedback.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Not authorized.",
                            content = @Content(
                                    examples = {
                                            @ExampleObject(
                                                    name = "User not the driver",
                                                    value = "Unauthorized operation.",
                                                    description = "User we are leaving feedback for is not the driver" +
                                                            "of the travel.")
                                    },
                                    mediaType = "Plain text")
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found status.",
                            content = @Content(
                                    examples = {
                                            @ExampleObject(
                                                    name = "Missing user",
                                                    value = "User with ID '100' not found.",
                                                    description = "There is no such user with the provided ID."),
                                            @ExampleObject(
                                                    name = "Missing travel",
                                                    value = "Travel with ID '200' not found.",
                                                    description = "There is no such travel with the provided ID."),
                                            @ExampleObject(
                                                    name = "User not approved",
                                                    value = "Invalid operation.",
                                                    description = "The user to left feedback for the driver is not on the " +
                                                            "approved list for the travel."),

                                    },
                                    mediaType = "Plain text")
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Missing Authentication.",
                            content = @Content(
                                    examples = {
                                            @ExampleObject(
                                                    name = "Not authenticated",
                                                    value = "The requested resource requires authentication.",
                                                    description = "You need to be authenticated to update a User information.")
                                    },
                                    mediaType = "Plain text")
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request.",
                            content = @Content(
                                    examples = {
                                            @ExampleObject(
                                                    name = "Travel status not 'completed'",
                                                    value = "Invalid operation.",
                                                    description = "The travel has a status different from 'COMPLETED'."),
                                            @ExampleObject(
                                                    name = "User is the driver",
                                                    value = "Invalid operation.",
                                                    description = "User leaving feedback for the driver is the driver itself."),
                                            @ExampleObject(
                                                    name = "User not approved",
                                                    value = "Invalid operation.",
                                                    description = "The user to be left feedback for is not on the " +
                                                            "approved list for the travel."),
                                            @ExampleObject(
                                                    name = "Feedback left",
                                                    value = "Invalid operation.",
                                                    description = "Feedback for the driver of this travel from this passenger " +
                                                            "has already been submitted.")
                                    },
                                    mediaType = "Plain text")
                    )
            },
            security = {@SecurityRequirement(name = "Authorization")}
    )
    @ResponseStatus(HttpStatus.CREATED)
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
            return userService.leaveFeedbackForDriver(feedbackForDriver, travel, loggedInUser, driver);
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

    /*Plamen*/
    @Operation(
            summary = "Used to get all feedback for a specific driver.",
            description = "Retrieve all the feedback a specific user received as a driver.",
            parameters = {
                    @Parameter(name = "id",
                            description = "Specific id to search in the system",
                            example = "/api/v1/users/3/feedback-for-driver"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Success response.",
                            content = @Content(schema = @Schema(implementation = Feedback.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found status.",
                            content = @Content(
                                    examples = {
                                            @ExampleObject(
                                                    name = "Missing user",
                                                    value = "User with ID '100' not found.",
                                                    description = "There is no such user with the provided ID."),
                                    },
                                    mediaType = "Plain text")
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Missing Authentication.",
                            content = @Content(
                                    examples = {
                                            @ExampleObject(
                                                    name = "Not authenticated",
                                                    value = "The requested resource requires authentication.",
                                                    description = "You need to be authenticated to update a User information.")
                                    },
                                    mediaType = "Plain text")
                    ),
            },
            security = {@SecurityRequirement(name = "Authorization")}
    )
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

    @Operation(
            summary = "Retrieves the feedback left on user's profile.",
            description = "This endpoint is retrieved to retrieve all feedback / reviews which drivers of different trips have left for this user, when they were a passenger.",
            parameters = {
                    @Parameter(
                            name = "userId",
                            description = "user ID must be numeric.",
                            example = "3"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "The server has successfully completed the request.",
                            content = @Content(
                                    schema = @Schema(implementation = Feedback.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Missing Authentication.",
                            content = @Content(
                                    examples = {
                                            @ExampleObject(
                                                    name = "Not authenticated",
                                                    value = "The requested resource requires authentication.",
                                                    description = "You need to be authenticated to create a Travel.")
                                    },
                                    mediaType = "Plain text")
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found status.",
                            content = @Content(
                                    examples = {
                                            @ExampleObject(
                                                    name = "Missing user",
                                                    value = "User with ID '{userId}' not found.",
                                                    description = "There is no such user with the provided user ID.")

                                    },
                                    mediaType = "Plain text")
                    )
            },
            security = {@SecurityRequirement(name = "Authorization")}
    )
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

    @Operation(
            summary = "User viewing all his/her travels as driver of the travel.",
            description = "Get a list of all his/her travels as driver of the travel no matter the status of the" +
                    "travel. The user can view all his 'created', 'canceled' and 'completed' travels.",
            parameters = {
                    @Parameter(
                            name = "startingPointCity",
                            description = "Get travels' starting point city that consist the text provided " +
                                    "in 'startingPointCity' parameter.",
                            example = "Plovdiv"),
                    @Parameter(
                            name = "endingPointCity",
                            description = "Get travels' ending point city that consist the text provided " +
                                    "in 'startingPointCity' parameter.",
                            example = "Sofia"),
                    @Parameter(
                            name = "departureBefore",
                            description = "Get posts that departures before a certain date provided in the " +
                                    "'departureBefore' parameter.",
                            example = "2024-05-16 22:00:00"),
                    @Parameter(
                            name = "departureAfter",
                            description = "Get posts that departures after a certain date provided in the " +
                                    "'departureAfter' parameter.",
                            example = "2024-04-16 12:00:00"),
                    @Parameter(
                            name = "minFreeSeats",
                            description = "Get travels that have free seats equal or more than a certain integer number",
                            example = "2"),
                    @Parameter(
                            name = "driverUsername",
                            description = "If the travels' driver username consist the text provided in the " +
                                    "'driverUsername' parameter.",
                            example = "pesho"),
                    @Parameter(
                            name = "commentContains",
                            description = "If the travels' comment consist the text provided in the 'commentContains' parameter.",
                            example = "pet allowed"),
                    @Parameter(
                            name = "statusId",
                            description = "Select the ID of te status of the travel to get all travels with  that status." +
                                    "1 - for 'CREATED', 2 - for 'CANCELED', 3 - for 'COMPLETED'.",
                            example = "2"),
                    @Parameter(
                            name = "sortBy",
                            description = "You can choose to sort the travels list by 'startingPointCity', 'endingPointCity', " +
                                    "'departureTime', 'freeSeats', 'createdOn',  'driver', 'distance' or 'duration'.",
                            example = "desc"),
                    @Parameter(
                            name = "sortOrder",
                            description = "You can choose to sort the travels list in descending order by typing 'desc'. " +
                                    "The default is an ascending order.",
                            example = "desc")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Success Response"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Missing Authentication.",
                            content = @Content(
                                    examples = {
                                            @ExampleObject(
                                                    name = "Not authenticated",
                                                    value = "The requested resource requires authentication.",
                                                    description = "You need to be authenticated to view the resource.")
                                    },
                                    mediaType = "Plain text")
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found status.",
                            content = @Content(
                                    examples = {
                                            @ExampleObject(
                                                    name = "Missing user",
                                                    value = "User with ID '100' not found.",
                                                    description = "There is no such user with the provided ID.")

                                    },
                                    mediaType = "Plain text")
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Not authorized.",
                            content = @Content(
                                    examples = {
                                            @ExampleObject(name = "Logged user not the same",
                                                    value = "Unauthorized operation.",
                                                    description = "The logged-in user is attempting to view travels " +
                                                            "belonging to a different user."
                                            )
                                    },
                                    mediaType = "Plain text")
                    )
            },
            security = {@SecurityRequirement(name = "Authorization")}
    )
    @GetMapping("/{userId}/travels-for-driver")
    public List<Travel> getTravelsByDriver(@PathVariable int userId,
                                           @RequestHeader HttpHeaders headers,
                                           @RequestParam(required = false) String startingPointCity,
                                           @RequestParam(required = false) String endingPointCity,
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
                    startingPointCity, endingPointCity, departureBefore, departureAfter, minFreeSeats,
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
    @Operation(
            summary = "User viewing all his/her travels as passenger of the travel.",
            description = "Get a list of all his/her travels as passenger of the travel no matter the status of the" +
                    "travel. The user can view all his 'created', 'canceled' and 'completed' travels.",
            parameters = {
                    @Parameter(
                            name = "startingPointCity",
                            description = "Get travels' starting point city that consist the text provided " +
                                    "in 'startingPointCity' parameter.",
                            example = "Plovdiv"),
                    @Parameter(
                            name = "endingPointCity",
                            description = "Get travels' ending point city that consist the text provided " +
                                    "in 'startingPointCity' parameter.",
                            example = "Sofia"),
                    @Parameter(
                            name = "departureBefore",
                            description = "Get posts that departures before a certain date provided in the " +
                                    "'departureBefore' parameter.",
                            example = "2024-05-16 22:00:00"),
                    @Parameter(
                            name = "departureAfter",
                            description = "Get posts that departures after a certain date provided in the " +
                                    "'departureAfter' parameter.",
                            example = "2024-04-16 12:00:00"),
                    @Parameter(
                            name = "minFreeSeats",
                            description = "Get travels that have free seats equal or more than a certain integer number",
                            example = "2"),
                    @Parameter(
                            name = "driverUsername",
                            description = "If the travels' driver username consist the text provided in the " +
                                    "'driverUsername' parameter.",
                            example = "pesho"),
                    @Parameter(
                            name = "commentContains",
                            description = "If the travels' comment consist the text provided in the 'commentContains' parameter.",
                            example = "pet allowed"),
                    @Parameter(
                            name = "statusId",
                            description = "Select the ID of te status of the travel to get all travels with  that status." +
                                    "1 - for 'CREATED', 2 - for 'CANCELED', 3 - for 'COMPLETED'.",
                            example = "2"),
                    @Parameter(
                            name = "sortBy",
                            description = "You can choose to sort the travels list by 'startingPointCity', 'endingPointCity', " +
                                    "'departureTime', 'freeSeats', 'createdOn',  'driver', 'distance' or 'duration'.",
                            example = "desc"),
                    @Parameter(
                            name = "sortOrder",
                            description = "You can choose to sort the travels list in descending order by typing 'desc'. " +
                                    "The default is an ascending order.",
                            example = "desc")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Success Response"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Missing Authentication.",
                            content = @Content(
                                    examples = {
                                            @ExampleObject(
                                                    name = "Not authenticated",
                                                    value = "The requested resource requires authentication.",
                                                    description = "You need to be authenticated to view the resource.")
                                    },
                                    mediaType = "Plain text")
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found status.",
                            content = @Content(
                                    examples = {
                                            @ExampleObject(
                                                    name = "Missing user",
                                                    value = "User with ID '100' not found.",
                                                    description = "There is no such user with the provided ID.")

                                    },
                                    mediaType = "Plain text")
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Not authorized.",
                            content = @Content(
                                    examples = {
                                            @ExampleObject(name = "Logged user not the same",
                                                    value = "Unauthorized operation.",
                                                    description = "The logged-in user is attempting to view travels " +
                                                            "belonging to a different user."
                                            )
                                    },
                                    mediaType = "Plain text")
                    )
            },
            security = {@SecurityRequirement(name = "Authorization")}
    )
    @GetMapping("/{userId}/travels-for-passenger")
    public List<Travel> getTravelsByPassenger(@PathVariable int userId,
                                              @RequestHeader HttpHeaders headers,
                                              @RequestParam(required = false) String startingPointCity,
                                              @RequestParam(required = false) String endingPointCity,
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
                    startingPointCity,
                    endingPointCity,
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

    @Operation(
            summary = "Delete a single user by numeric ID.",
            description = "Delete a user by providing numeric ID in the endpoint. You need to be logged as " +
                    "the user itself.",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "ID must be numeric.",
                            example = "/api/v1/users/3"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Success response when the user has been deleted."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found status.",
                            content = @Content(
                                    examples = {
                                            @ExampleObject(
                                                    name = "Missing user",
                                                    value = "User with ID '200' not found.",
                                                    description = "There is no such user with the provided ID.")
                                    },
                                    mediaType = "Plain text")
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Missing Authentication.",
                            content = @Content(
                                    examples = {
                                            @ExampleObject(
                                                    name = "Not authenticated",
                                                    value = "The requested resource requires authentication.",
                                                    description = "You need to be authenticated to delete a User.")
                                    },
                                    mediaType = "Plain text")
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Not authorized.",
                            content = @Content(
                                    examples = {
                                            @ExampleObject(
                                                    name = "Not authorized",
                                                    value = "Unauthorized operation.",
                                                    description = "Only the user itself can delete his/her profile.")
                                    },
                                    mediaType = "Plain text")
                    )
            },
            security = {@SecurityRequirement(name = "Authorization")}
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{userId}")
    public void deleteUser(@RequestHeader HttpHeaders headers,
                           @PathVariable int userId) {
        try {
            User userToBeDeleted = userService.getUserById(userId);
            User userLogged = authenticationHelper.tryGetUserFromHeaders(headers);
            userService.deleteUser(userToBeDeleted, userLogged);
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    /*Ilia*/
    @GetMapping("/{userId}/travels-for-applied-passenger")
    public List<Travel> getOpenTravelsUserAppliedFor(@PathVariable int userId,
                                                     @RequestHeader HttpHeaders headers) {
        try {
            User user = userService.getUserById(userId);
            User loggedUser = authenticationHelper.tryGetUserFromHeaders(headers);

            return travelService.getTravelsAsAppliedPassenger(loggedUser, user);
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/{userId}/car-picture")
    public User addCarPicture(@PathVariable int userId,
                              @RequestParam("car_picture") MultipartFile multipartFile,
                              @RequestHeader HttpHeaders headers) {
        try {
            User user = userService.getUserById(userId);
            User loggedUser = authenticationHelper.tryGetUserFromHeaders(headers);
            String avatarUrl = carPictureService.uploadPictureToCloudinary(multipartFile);
            return userService.addCarPicture(user, avatarUrl, loggedUser);
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
    @Operation(
            summary = "Deletes a car picture from user's profile.",
            description = "Used to update user's profile by deleting his/her car picture.",
            parameters = {
                    @Parameter(name = "userId",
                            description = "ID of the user whose profile will be updated.",
                            example = "/api/v1/users/3/avatar"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Success response.",
                            content = @Content(
                                    schema = @Schema(implementation = User.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Not authorized.",
                            content = @Content(
                                    examples = {
                                            @ExampleObject(
                                                    name = "Logged user not the same",
                                                    value = "Unauthorized operation.",
                                                    description = "The logged user who is adding the avatar is not the same" +
                                                            "as the one who will be updated.")
                                    },
                                    mediaType = "Plain text")
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Missing Authentication.",
                            content = @Content(
                                    examples = {
                                            @ExampleObject(
                                                    name = "Not authenticated",
                                                    value = "The requested resource requires authentication.",
                                                    description = "You need to be authenticated to update a User information.")
                                    },
                                    mediaType = "Plain text")
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found status.",
                            content = @Content(
                                    examples = {
                                            @ExampleObject(
                                                    name = "Missing user",
                                                    value = "User with ID '100' not found.",
                                                    description = "There is no such user with the provided ID.")
                                    },
                                    mediaType = "Plain text")
                    ),

            },
            security = {@SecurityRequirement(name = "Authorization")}
    )
    @DeleteMapping("/{userId}/car-picture")
    public User deleteCarPicture(@PathVariable int userId,
                                 @RequestHeader HttpHeaders headers) {
        try {
            User loggedUser = authenticationHelper.tryGetUserFromHeaders(headers);
            User userToBeRemovedCarPictureFrom = userService.getUserById(userId);
            return userService.deleteCarPicture(userToBeRemovedCarPictureFrom, loggedUser);
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

}
