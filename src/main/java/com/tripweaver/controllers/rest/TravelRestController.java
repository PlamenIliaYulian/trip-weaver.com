package com.tripweaver.controllers.rest;

import com.tripweaver.models.Travel;
import com.tripweaver.models.User;
import com.tripweaver.models.filterOptions.TravelFilterOptions;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/travels")
public class TravelRestController {

    /*Ilia*/
    @PostMapping
    public Travel createTravel(){
        return null;
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
    public Travel getTravelById(){
        return null;
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
    public Travel approvePassenger(){
        return null;
    }

    /*Plamen*/
    @DeleteMapping("/{travelId}/applications/{userId}")
    public Travel declinePassenger(){
        return null;
    }


}
