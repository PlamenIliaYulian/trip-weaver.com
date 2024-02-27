package com.tripweaver.controllers.helpers;

import com.tripweaver.controllers.helpers.contracts.ModelsMapper;
import com.tripweaver.models.Travel;
import com.tripweaver.models.dtos.TravelDto;
import org.springframework.stereotype.Component;

@Component
public class ModelsMapperImpl implements ModelsMapper {
    @Override
    public Travel travelFromDto(TravelDto dto) {
        Travel travel = new Travel();
        travel.setStartingPoint(dto.getStartingPoint());
        travel.setEndingPoint(dto.getEndingPoint());
        travel.setDepartureTime(dto.getDepartureTime());
        travel.setFreeSeats(dto.getFreeSeats());
        travel.setComment(dto.getComment());
        return travel;
    }
}
