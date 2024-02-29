package com.tripweaver.services.contracts;

import java.util.HashMap;

public interface BingMapService {

    HashMap<String, String> getCoordinatesAndValidCityName(String address);

    HashMap<String, Integer> calculateDistanceAndDuration(String startingPoint, String endingPoint);

}
