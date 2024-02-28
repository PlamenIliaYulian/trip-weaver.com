package com.tripweaver.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripweaver.services.contracts.BingMapService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;

@Service
public class BingMapServiceImpl implements BingMapService {

    private String locationEndpointUrl = "http://dev.virtualearth.net/REST/v1/Locations?q=";
    private String calculateDistanceEndpointUrl = "https://dev.virtualearth.net/REST/v1/Routes/DistanceMatrix?origins=";

    @Value("${bing.maps.key}")
    private String apiKey;

    @Override
    public String getLocation(String address) {
        StringBuilder sb = new StringBuilder();
        sb.append(locationEndpointUrl).append(address.replaceAll(" ", "%20")).append(apiKey);
        try {
            HttpRequest getRequest = HttpRequest.newBuilder()
                    .uri(new URI(sb.toString()))
                    .build();
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpResponse<String> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(getResponse.body());
            JsonNode childNode = jsonNode.get("resourceSets").get(0).get("resources").get(0).get("point").get("coordinates");
            StringBuilder builder = new StringBuilder();
            builder.append(childNode.get(0)).append(System.lineSeparator()).append(childNode.get(1));
            return builder.toString();
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public HashMap<String, Integer> calculateDistanceAndDuration(String startingPoint, String endingPoint) {

        StringBuilder sb = new StringBuilder(calculateDistanceEndpointUrl)
                .append(startingPoint)
                .append("&destinations=")
                .append(endingPoint)
                .append("&travelMode=driving")
                .append(apiKey);
        try {
            HttpRequest getRequest = HttpRequest.newBuilder()
                    .uri(new URI(sb.toString()))
                    .build();
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpResponse<String> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(getResponse.body());
            JsonNode travelDistance =
                    jsonNode.get("resourceSets").get(0)
                            .get("resources").get(0).get("results").get(0).get("travelDistance");
            JsonNode travelDuration =
                    jsonNode.get("resourceSets").get(0)
                            .get("resources").get(0).get("results").get(0).get("travelDuration");
            HashMap<String, Integer> result = new HashMap<>();
            result.put("travelDistance", travelDistance.asInt());
            result.put("travelDuration", travelDuration.asInt());
            return result;
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
