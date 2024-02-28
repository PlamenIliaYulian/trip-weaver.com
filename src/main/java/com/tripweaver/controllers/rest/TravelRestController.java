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
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("api/v1/travels")
public class TravelRestController {
    public static final int TRAVEL_STATUS_CREATED_ID = 1;
    private final ModelsMapper modelsMapper;
    private final AuthenticationHelper authenticationHelper;
    private final TravelService travelService;
    private final UserService userService;

    private final BingMapService bingMapService;

    public TravelRestController(ModelsMapper modelsMapper, AuthenticationHelper authenticationHelper, TravelService travelService, UserService userService, BingMapService bingMapService) {
        this.modelsMapper = modelsMapper;
        this.authenticationHelper = authenticationHelper;
        this.travelService = travelService;
        this.userService = userService;
        this.bingMapService = bingMapService;
    }

    /*Ilia*/
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Travel createTravel(@Valid @RequestBody TravelDto travelDto, @RequestHeader HttpHeaders headers) {
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
    public Travel cancelTravel(@PathVariable int travelId, @RequestHeader HttpHeaders httpHeaders) {
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

    /*Yuli*/
    @PutMapping("/{travelId}/status-completed")
    public Travel completeTravel(@RequestHeader HttpHeaders headers, @PathVariable int travelId) {
        try {
            User loggedInUser = authenticationHelper.tryGetUserFromHeaders(headers);
            Travel travelToMarkAsCompleted = travelService.getTravelById(travelId);
            return travelService.completeTravel(travelToMarkAsCompleted, loggedInUser);
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    /*Ilia*/
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
    @GetMapping
    public List<Travel> getAllTravels(@RequestHeader HttpHeaders headers, @RequestParam(required = false) String startingPoint, @RequestParam(required = false) String endingPoint, @RequestParam(required = false) String departureBefore, @RequestParam(required = false) String departureAfter, @RequestParam(required = false) Integer minFreeSeats, @RequestParam(required = false) String driverUsername, @RequestParam(required = false) String commentContains, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortOrder) {
        try {
            authenticationHelper.tryGetUserFromHeaders(headers);
            TravelFilterOptions travelFilterOptions = new TravelFilterOptions(startingPoint, endingPoint, departureBefore, departureAfter, minFreeSeats, driverUsername, commentContains, TRAVEL_STATUS_CREATED_ID, sortBy, sortOrder);
            return travelService.getAllTravels(travelFilterOptions);
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    /*Yuli*/
    @PostMapping("/{travelId}/applications")
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

    /*Ilia*/
    @PutMapping("/{travelId}/applications/{userId}")
    public Travel approvePassenger(@PathVariable int travelId, @PathVariable int userId, @RequestHeader HttpHeaders headers) {
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
    public Travel declinePassenger(@PathVariable int travelId, @PathVariable int userId, @RequestHeader HttpHeaders headers) {
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

    @GetMapping("/count")
    public Long getAllTravelsCount() {
        return travelService.getAllTravelsCount();
    }


    @GetMapping("/getLocation")
    public String getStartingCoordinates(@RequestParam("q") String address) {
        return bingMapService.getLocation(address);
    }

    @GetMapping("/getDistanceAndDuration")
    public HashMap<String, Integer> getStartingCoordinates(@RequestParam("startingPoint") String startingPoint,
                                                           @RequestParam("endingPoint") String endingPoint) {
        return bingMapService.calculateDistanceAndDuration(startingPoint, endingPoint);
    }

}
