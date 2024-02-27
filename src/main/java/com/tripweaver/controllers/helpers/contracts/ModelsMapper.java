package com.tripweaver.controllers.helpers.contracts;

import com.tripweaver.models.Travel;
import com.tripweaver.models.dtos.TravelDto;

public interface ModelsMapper {

    Travel travelFromDto (TravelDto travelDto);
}
