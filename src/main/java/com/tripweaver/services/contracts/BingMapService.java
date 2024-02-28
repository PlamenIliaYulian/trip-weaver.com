package com.tripweaver.services.contracts;

import java.util.HashMap;

public interface BingMapService {

    String getLocation(String address);

    HashMap<String, Integer> calculateDistanceAndDuration(String startingPoint, String endingPoint);

}
