package com.tripweaver.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tripweaver.models.BingMapLocation;
import com.tripweaver.services.contracts.BingMapService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class BingMapServiceImpl implements BingMapService {


    private String url = "http://dev.virtualearth.net/REST/v1/Locations?addressLine=";

    @Value("${bing.maps.key}")
    private String apiKey;

    @Override
    public String getLocation(String address){

        StringBuilder sb = new StringBuilder();
        sb.append(url).append(address).append(apiKey);
        Gson gson = new Gson();

        BingMapLocation bingMapLocation = new BingMapLocation();

        try {
            HttpRequest getRequest = HttpRequest.newBuilder()
                    .uri(new URI(sb.toString()))
                    .build();
            HttpClient httpClient = HttpClient.newHttpClient();

            HttpResponse<String> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());
            bingMapLocation = gson.fromJson(getResponse.body(), BingMapLocation.class);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(getResponse.toString());
            // Access child property
            JsonNode childNode = jsonNode.get("resourceSets").get(0).get("resources").get(0).get("point").get("coordinates");
            String childValue = childNode.asText();

            /*JsonElement resources = bingMapLocation.getResourceSets().get(0).getAsJsonObject().get("coordinates");
            System.out.println(resources);*/
            System.out.println(childValue);
            return childValue;

        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }


    }
}
