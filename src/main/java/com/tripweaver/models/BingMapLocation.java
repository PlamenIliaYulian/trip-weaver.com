package com.tripweaver.models;

import com.google.gson.JsonArray;

public class BingMapLocation {

    private JsonArray resourceSets;

    private String authenticationResultCode;

    public JsonArray getResourceSets() {
        return resourceSets;
    }

    public void setResourceSets(JsonArray resourceSets) {
        this.resourceSets = resourceSets;
    }

    public String getAuthenticationResultCode() {
        return authenticationResultCode;
    }

    public void setAuthenticationResultCode(String authenticationResultCode) {
        this.authenticationResultCode = authenticationResultCode;
    }


}
