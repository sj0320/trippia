package com.trippia.travel.domain.location.place;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trippia.travel.domain.location.LatLng;
import com.trippia.travel.domain.location.city.City;
import com.trippia.travel.domain.location.city.CityRepository;
import com.trippia.travel.exception.city.CityException;
import com.trippia.travel.util.HttpClient;
import com.trippia.travel.util.PlaceTypeMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.trippia.travel.domain.location.place.PlaceDto.RecommendPlaceResponse;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlaceService {

    @Value("${google.maps.api-key}")
    private String googleApiKey;
    private String geocodeUrlFormat;
    private String nearByUrlFormat;
    private String autoCompletedFormat;
//    private String photoUrlFormat;

    @PostConstruct
    public void init() {
        this.geocodeUrlFormat = "https://maps.googleapis.com/maps/api/geocode/json?address=%s&language=ko&key=" + googleApiKey;
        this.nearByUrlFormat = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=%f,%f&radius=5000&type=%s&language=ko&key=" + googleApiKey;
        this.autoCompletedFormat = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=%s&location=%f,%f&radius=5000&language=ko&key=" + googleApiKey;
        //        this.photoUrlFormat = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photo_reference=%s&key=" + googleApiKey;
    }

    private final CityRepository cityRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient;

    public Set<RecommendPlaceResponse> getRecommendPlacesByType(List<Long> cityIds, String placeType) {
        log.info("result={}", getPlacesByCityAndType(cityIds, placeType).toString());
        return getPlacesByCityAndType(cityIds, placeType);
    }

    public Set<RecommendPlaceResponse> getAutocompletePlaces(List<Long> cityIds, String query) {
        List<String> cityNames = getCityNames(cityIds);
        Set<RecommendPlaceResponse> results = new HashSet<>();

        for (String cityName : cityNames) {
            try {
                LatLng latLng = getCityLocation(cityName);
                Set<RecommendPlaceResponse> places = findPlacesByTextSearch(query, latLng);
                results.addAll(places);
            } catch (Exception e) {
                log.error("자동완성 검색 중 오류 - city: {}, error: {}", cityName, e.getMessage(), e);
            }
        }
        log.info("Auto results={}", results);
        return results;
    }

    private Set<RecommendPlaceResponse> findPlacesByTextSearch(String query, LatLng latLng) throws IOException {
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String autoCompletedUrl = autoCompletedFormat.formatted(encodedQuery, latLng.lat(), latLng.lng());


        log.info("자동검색 query={}", query);
        log.info("autoCompletedUrl={}", autoCompletedUrl);

        JsonNode results = getJsonResponseByUrl(autoCompletedUrl).get("predictions");

        log.info("auto results={}", results);

        Set<RecommendPlaceResponse> places = new HashSet<>();
        if (results != null && results.isArray()) {
            for (JsonNode place : results) {
                places.add(toRecommendPlaceResponse(place));
            }
        }

        return places;
    }


    private Set<RecommendPlaceResponse> getPlacesByCityAndType(List<Long> cityIds, String type) {
        List<String> cityNames = getCityNames(cityIds);
        String placeType = PlaceTypeMapper.convertToGoogleType(type);
        Set<RecommendPlaceResponse> results = new HashSet<>();
        for (String cityName : cityNames) {
            try {
                LatLng latLng = getCityLocation(cityName);
                results.addAll(findNearByPlaces(latLng, placeType));
            } catch (Exception e) {
                log.error("도시 '{}'의 추천 장소 검색 중 오류: {}", cityName, e.getMessage(), e);
            }
        }
        return results;
    }

    private Set<RecommendPlaceResponse> findNearByPlaces(LatLng latLng, String placeType) throws IOException {
        String nearByUrl = nearByUrlFormat.formatted(latLng.lat(), latLng.lng(), placeType);
        JsonNode nearByLocations = getJsonResponseByUrl(nearByUrl).get("results");

        if (nearByLocations == null || !nearByLocations.isArray() || nearByLocations.isEmpty()) {
            return Set.of();
        }
        Set<RecommendPlaceResponse> recommendedPlaces = new HashSet<>();
        for (JsonNode nearByLocation : nearByLocations) {
            recommendedPlaces.add(toRecommendPlaceResponse(nearByLocation));
        }
        return recommendedPlaces;
    }

    private RecommendPlaceResponse toRecommendPlaceResponse(JsonNode location) {
        String placeId = location.path("place_id").asText();
        String name = location.path("name").asText();
        if (name.isEmpty()) {
            // 'name' 값이 없을 경우 'main_text'로 대체
            JsonNode structuredFormatting = location.path("structured_formatting");
            name = structuredFormatting.path("main_text").asText();
        }

        String address = location.path("vicinity").asText();
        if (address.isEmpty()) {
            // 'vicinity' 값이 없을 경우 'secondary_text'로 대체
            JsonNode structuredFormatting = location.path("structured_formatting");
            address = structuredFormatting.path("secondary_text").asText();
        }

        Set<String> themes = getPlaceThemes(location);
        return new RecommendPlaceResponse(placeId, name, address, themes);
    }

    private Set<String> getPlaceThemes(JsonNode nearByLocation) {
        Set<String> themes = new HashSet<>();
        JsonNode typesJson = nearByLocation.path("types");
        if (typesJson != null && typesJson.isArray()) {
            for (JsonNode typeJson : typesJson) {
                String theme = PlaceTypeMapper.translate(typeJson.asText());
                themes.add(theme);
            }
        }
        return themes;
    }

    private JsonNode getJsonResponseByUrl(String url) throws IOException {
        String response = httpClient.get(url);
        log.info("url response={}",response);
        return objectMapper.readTree(response);
    }

    private LatLng getCityLocation(String cityName) throws IOException {
        String geocodeUrl = geocodeUrlFormat.formatted(URLEncoder.encode(cityName, StandardCharsets.UTF_8));
        JsonNode geocodeJson = getJsonResponseByUrl(geocodeUrl);
        JsonNode location = geocodeJson.at("/results/0/geometry/location");
        if (location.isMissingNode()) {
            throw new CityException("해당 도시를 찾을 수 없습니다.");
        }
        return new LatLng(location.get("lat").asDouble(), location.get("lng").asDouble());
    }

    private List<String> getCityNames(List<Long> cityIds) {
        List<City> cities = cityRepository.findAllById(cityIds);
        return cities.stream()
                .map(City::getName)
                .toList();
    }

}
