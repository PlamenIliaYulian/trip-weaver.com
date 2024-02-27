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
import com.tripweaver.services.contracts.TravelService;
import com.tripweaver.services.contracts.UserService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("api/v1/travels")
public class TravelRestController {

    private final ModelsMapper modelsMapper;
    private final AuthenticationHelper authenticationHelper;
    private final TravelService travelService;
    private final UserService userService;

    public TravelRestController(ModelsMapper modelsMapper,
                                AuthenticationHelper authenticationHelper,
                                TravelService travelService,
                                UserService userService) {
        this.modelsMapper = modelsMapper;
        this.authenticationHelper = authenticationHelper;
        this.travelService = travelService;
        this.userService = userService;
    }

    /*Ilia*/
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Travel createTravel(@Valid @RequestBody TravelDto travelDto,
                               @RequestHeader HttpHeaders headers){
        try {
            User loggedUser = authenticationHelper.tryGetUserFromHeaders(headers);
            Travel travel = modelsMapper.travelFromDto(travelDto);
            return travelService.createTravel(travel,loggedUser);
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
    public Travel cancelTravel(){
        return null;
    }

    /*Yuli*/
    @PutMapping("/{travelId}/status-completed")
    public Travel completeTravel(){
        return null;
    }

    /*Ilia*/
    @GetMapping("/{travelId}")
    public Travel getTravelById(@PathVariable int travelId,
                                @RequestHeader HttpHeaders headers){
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
    public List<Travel> getAllTravels(){
        return null;
    }

    /*Yuli*/
    @PostMapping("/{travelId}/applications")
    public Travel applyForATrip(){
        return null;
    }

    /*Ilia*/
    @PutMapping("/{travelId}/applications/{userId}")
    public Travel approvePassenger(@PathVariable int travelId,
                                   @PathVariable int userId,
                                   @RequestHeader HttpHeaders headers){
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
    public Travel declinePassenger(){
        return null;
    }


}
