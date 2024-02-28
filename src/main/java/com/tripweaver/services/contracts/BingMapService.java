package com.tripweaver.services.contracts;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tripweaver.models.BingMapLocation;
import com.tripweaver.models.Travel;

public interface BingMapService {

    String getLocation(String address);

}
