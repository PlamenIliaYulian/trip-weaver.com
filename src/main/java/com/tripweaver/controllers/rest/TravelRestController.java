package com.tripweaver.controllers.rest;

import com.tripweaver.controllers.helpers.AuthenticationHelper;
import com.tripweaver.controllers.helpers.contracts.ModelsMapper;
import com.tripweaver.exceptions.AuthenticationException;
import com.tripweaver.exceptions.EntityNotFoundException;
import com.tripweaver.exceptions.InvalidOperationException;
import com.tripweaver.exceptions.UnauthorizedOperationException;
import com.tripweaver.models.Travel;
import com.tripweaver.models.User;
import com.tripweaver.models.dtos.TravelDto;
import com.tripweaver.models.filterOptions.TravelFilterOptions;
import com.tripweaver.services.contracts.BingMapService;
import com.tripweaver.services.contracts.TravelService;
import com.tripweaver.services.contracts.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;

import static com.tripweaver.services.helpers.ConstantHelper.TRAVEL_STATUS_CREATED_ID;

@RestController
@RequestMapping("/api/v1/travels")
@Tag(name = "Travel")
public class TravelRestController {
    private final ModelsMapper modelsMapper;
    private final AuthenticationHelper authenticationHelper;
    private final TravelService travelService;
    private final UserService userService;
    private final BingMapService bingMapService;

    public TravelRestController(ModelsMapper modelsMapper,
                                AuthenticationHelper authenticationHelper,
                                TravelService travelService,
                                UserService userService,
                                BingMapService bingMapService) {
        this.modelsMapper = modelsMapper;
        this.authenticationHelper = authenticationHelper;
        this.travelService = travelService;
        this.userService = userService;
        this.bingMapService = bingMapService;
    }

    @Operation(
            summary = "Create new travel.",
            description = "Create a new travel in the database. Authentication needed.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Body is consisted of startingPointCity, startingPointAddress, " +
                            "endingPointCity, endingPointAddress (optional), departureTime, freeSeats" +
                            "and comment (optional)."),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Created",
                            content = @Content(
                                    schema = @Schema(implementation = Travel.class),
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
                            responseCode = "403",
                            description = "Not authorized.",
                            content = @Content(
                                    examples = {
                                            @ExampleObject(
                                                    name = "User is blocked",
                                                    value = "Unauthorized operation.",
                                                    description = "A blocked user is not able to create travels."),
                                            @ExampleObject(
                                                    name = "User is not verified",
                                                    value = "Unauthorized operation.",
                                                    description = "Before verifying their email, users cannot participate in rides.")
                                    },
                                    mediaType = "Plain text")
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request.",
                            content = @Content(
                                    examples = {
                                            @ExampleObject(
                                                    name = "Bad request.",
                                                    value = "Invalid operation.",
                                                    description = "Departure time cannot be before current moment.")
                                    },
                                    mediaType = "Plain text")
                    )
            },
            security = {@SecurityRequirement(name = "Authorization")}
    )
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Travel createTravel(@RequestBody @Valid TravelDto travelDto,
                               @RequestHeader HttpHeaders headers) {
        try {
            User loggedUser = authenticationHelper.tryGetUserFromHeaders(headers);
            Travel travel = modelsMapper.travelFromDto(travelDto);
            return travelService.createTravel(travel, loggedUser);
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (InvalidOperationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /*Plamen*/
    @PutMapping("/{travelId}/status-cancelled")
    public Travel cancelTravel(@PathVariable int travelId,
                               @RequestHeader HttpHeaders httpHeaders) {
        try {
            User loggedUser = authenticationHelper.tryGetUserFromHeaders(httpHeaders);
            Travel travel = travelService.getTravelById(travelId);
            return travelService.cancelTravel(travel, loggedUser);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @Operation(
            summary = "Completes a travel.",
            description = "The driver of the travel can use this endpoint to mark a specific travel they have completed as 'Completed' in the system.",
            parameters = {
                    @Parameter(
                            name = "travelId",
                            description = "Travel ID must be numeric.",
                            example = "3"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "The request has successfully been completed by the server.",
                            content = @Content(
                                    schema = @Schema(implementation = Travel.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request.",
                            content = @Content(
                                    examples = {
                                            @ExampleObject(
                                                    name = "Bad request.",
                                                    value = "Invalid operation.",
                                                    description = "Unauthorized operation. User not driver of the travel."),
                                            @ExampleObject(
                                                    name = "Bad request.",
                                                    value = "Invalid operation.",
                                                    description = "Travel not available.")
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
                                                    description = "Completing a travel requires from its driver to first log into the system.")
                                    },
                                    mediaType = "Plain text")
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found status.",
                            content = @Content(
                                    examples = {
                                            @ExampleObject(
                                                    name = "Missing travel", value = "Travel with ID '{travelId}' not found.",
                                                    description = "There is no travel that corresponds to the provided travel ID.")
                                    },
                                    mediaType = "Plain text")
                    )
            },
            security = {@SecurityRequirement(name = "Authorization")}
    )
    /*Yuli*/
    @PutMapping("/{travelId}/status-completed")
    public Travel completeTravel(@RequestHeader HttpHeaders headers, @PathVariable int travelId) {
        try {
            User loggedInUser = authenticationHelper.tryGetUserFromHeaders(headers);
            Travel travelToMarkAsCompleted = travelService.getTravelById(travelId);
            return travelService.completeTravel(travelToMarkAsCompleted, loggedInUser);
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (InvalidOperationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Operation(
            summary = "Get a single travel by numeric ID.",
            description = "Get only one travel info by providing numeric ID in the endpoint.",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "ID must be numeric.",
                            example = "/api/v1/travels/3")

            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Success Response",
                            content = @Content(
                                    schema = @Schema(implementation = Travel.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found status.",
                            content = @Content(
                                    examples = {
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
                                                    description = "You need to be authenticated to get a travel by ID.")
                                    },
                                    mediaType = "Plain text")
                    )
            },
            security = {@SecurityRequirement(name = "Authorization")}
    )
    @GetMapping("/{travelId}")
    public Travel getTravelById(@PathVariable int travelId, @RequestHeader HttpHeaders headers) {
        try {
            authenticationHelper.tryGetUserFromHeaders(headers);
            return travelService.getTravelById(travelId);
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /*Plamen*/
    @GetMapping("/search")
    public List<Travel> getAllTravels(@RequestHeader HttpHeaders headers,
                                      @RequestParam(required = false) String startingPointCity,
                                      @RequestParam(required = false) String endingPointCity,
                                      @RequestParam(required = false) String departureBefore,
                                      @RequestParam(required = false) String departureAfter,
                                      @RequestParam(required = false) Integer minFreeSeats,
                                      @RequestParam(required = false) String driverUsername,
                                      @RequestParam(required = false) String commentContains,
                                      @RequestParam(required = false) String sortBy,
                                      @RequestParam(required = false) String sortOrder) {
        try {
            authenticationHelper.tryGetUserFromHeaders(headers);
            TravelFilterOptions travelFilterOptions = new TravelFilterOptions(startingPointCity, endingPointCity,
                    departureBefore, departureAfter, minFreeSeats, driverUsername, commentContains,
                    TRAVEL_STATUS_CREATED_ID, sortBy, sortOrder);
            return travelService.getAllTravels(travelFilterOptions);
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    /*Yuli*/
    @PutMapping("/{travelId}/applications")
    public Travel applyForATrip(@RequestHeader HttpHeaders headers, @PathVariable int travelId) {
        try {
            User loggedInUser = authenticationHelper.tryGetUserFromHeaders(headers);
            Travel travelToApplyFor = travelService.getTravelById(travelId);
            return travelService.applyForATrip(loggedInUser, travelToApplyFor);
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (InvalidOperationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Operation(
            summary = "Driver approves applied passenger for a certain travel.",
            description = "Approve a passenger that has applied to join a specific travel providing the travel numeric ID " +
                    "in the endpoint as well as the applied passenger numeric ID.",
            parameters = {
                    @Parameter(
                            name = "travelId",
                            description = "Travel ID must be numeric.",
                            example = "3"
                    ),
                    @Parameter(
                            name = "userId",
                            description = "User ID must be numeric.",
                            example = "6"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Success Response",
                            content = @Content(
                                    schema = @Schema(implementation = Travel.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found status.",
                            content = @Content(
                                    examples = {
                                            @ExampleObject(
                                                    name = "Missing travel", value = "Travel with ID '200' not found.",
                                                    description = "There is no such travel with the provided ID."),
                                            @ExampleObject(
                                                    name = "Missing user", value = "User with ID '100' not found.",
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
                                                    description = "You need to be authenticated to view a User.")
                                    },
                                    mediaType = "Plain text")
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Not authorized.",
                            content = @Content(
                                    examples = {
                                            @ExampleObject(
                                                    name = "User to be approved is blocked",
                                                    value = "Unauthorized operation.",
                                                    description = "A blocked user is not able to be approved in travels."),
                                            @ExampleObject(
                                                    name = "User to approve is blocked",
                                                    value = "Unauthorized operation.",
                                                    description = "A blocked user is not able to approve any passengers."),
                                            @ExampleObject(
                                                    name = "User to approve is not the driver",
                                                    value = "Unauthorized operation.",
                                                    description = "A user who is not the driver of the travel is not able to approve any passengers."),
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
    @PutMapping("/{travelId}/applications/user/{userId}")
    public Travel approvePassenger(@PathVariable int travelId,
                                   @PathVariable int userId,
                                   @RequestHeader HttpHeaders headers) {
        try {
            User loggedUser = authenticationHelper.tryGetUserFromHeaders(headers);
            User userToBeApproved = userService.getUserById(userId);
            Travel travel = travelService.getTravelById(travelId);
            return travelService.approvePassenger(userToBeApproved, loggedUser, travel);
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (InvalidOperationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /*Plamen*/
    @DeleteMapping("/{travelId}/applications/{userId}")
    public Travel declinePassenger(@PathVariable int travelId,
                                   @PathVariable int userId,
                                   @RequestHeader HttpHeaders headers) {
        try {
            User loggedUser = authenticationHelper.tryGetUserFromHeaders(headers);
            User userToBeDeclined = userService.getUserById(userId);
            Travel travel = travelService.getTravelById(travelId);
            return travelService.declinePassenger(userToBeDeclined, travel, loggedUser);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @Operation(
            summary = "Pulls from the database the count of all completed travels in the system.",
            description = "Used to obtain the total amount of travels created and then completed in the system.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Success Response"
                    )
            })
    @GetMapping("/count")
    public Long getAllTravelsCount() {
        return travelService.getAllTravelsCount();
    }


    /* Endpoints helping work with Bing Map API.*/

    /*    @GetMapping("/getLocation")
    public HashMap<String, String> getCoordinatesAndValidCityName(@RequestParam("q") String address) {
        return bingMapService.getCoordinatesAndValidCityName(address);
    }

    @GetMapping("/getDistanceAndDuration")
    public HashMap<String, Integer> calculateDistanceAndDuration(@RequestParam("startingPoint") String startingPoint,
                                                                 @RequestParam("endingPoint") String endingPoint) {
        return bingMapService.calculateDistanceAndDuration(startingPoint, endingPoint);
    }*/

}
