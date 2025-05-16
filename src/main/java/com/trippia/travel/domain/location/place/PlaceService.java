package com.trippia.travel.domain.location.place;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trippia.travel.controller.dto.diary.response.DiaryListResponse;
import com.trippia.travel.controller.dto.place.response.PlaceDetailsResponse;
import com.trippia.travel.controller.dto.place.response.PlaceSummaryResponse;
import com.trippia.travel.controller.dto.place.response.RecommendPlaceResponse;
import com.trippia.travel.domain.location.LatLng;
import com.trippia.travel.domain.location.city.City;
import com.trippia.travel.domain.location.city.CityRepository;
import com.trippia.travel.domain.post.diary.Diary;
import com.trippia.travel.domain.post.diaryplace.DiaryPlace;
import com.trippia.travel.domain.post.diaryplace.DiaryPlaceRepository;
import com.trippia.travel.exception.city.CityException;
import com.trippia.travel.util.HttpClient;
import com.trippia.travel.util.PlaceTypeMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PlaceService {

    @Value("${google.maps.api-key}")
    private String googleApiKey;
    private String globalAutoCompleteFormat;
    private String geocodeUrlFormat;
    private String nearByUrlFormat;
    private String autoCompletedFormat;
    private String placeDetailsUrlFormat;
    private String photoUrlFormat;

    @PostConstruct
    public void init() {
        this.globalAutoCompleteFormat = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=%s&language=ko&key=" + googleApiKey;
        this.geocodeUrlFormat = "https://maps.googleapis.com/maps/api/geocode/json?address=%s&language=ko&key=" + googleApiKey;
        this.nearByUrlFormat = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=%f,%f&radius=5000&type=%s&language=ko&key=" + googleApiKey;
        this.autoCompletedFormat = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=%s&location=%f,%f&radius=5000&language=ko&key=" + googleApiKey;
        this.placeDetailsUrlFormat = "https://places.googleapis.com/v1/places/%s?fields=%s&languageCode=ko&key=" + googleApiKey;
        this.photoUrlFormat = "https://places.googleapis.com/v1/%s/media?maxHeightPx=400&key=" + googleApiKey;
    }

    private final DiaryPlaceRepository diaryPlaceRepository;
    private final CityRepository cityRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient;

    public Set<RecommendPlaceResponse> getRecommendPlacesByType(List<Long> cityIds, String placeType) {
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

    public Set<RecommendPlaceResponse> getAutocompletePlace(String query) {
        Set<RecommendPlaceResponse> results = new HashSet<>();
        try {
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String autoCompleteUrl = globalAutoCompleteFormat.formatted(encodedQuery);

            JsonNode predictions = getJsonResponseByUrl(autoCompleteUrl).get("predictions");

            if (predictions != null && predictions.isArray()) {
                for (JsonNode place : predictions) {
                    results.add(toRecommendPlaceResponse(place));
                }
            }
        } catch (IOException e) {
            log.error("글로벌 자동완성 오류: {}", e.getMessage(), e);
        }
        return results;
    }

    public PlaceDetailsResponse getPlaceDetails(String placeId) {
        List<DiaryPlace> diaryPlaces = diaryPlaceRepository.findAllByPlace_PlaceId(placeId);
        List<Diary> relatedDiaries = diaryPlaces.stream()
                .map(DiaryPlace::getDiary)
                .toList();

        List<DiaryListResponse> relatedDiaryResponse = DiaryListResponse.from(relatedDiaries);

        String fields = String.join(",", List.of(
                "displayName",
                "formattedAddress",
                "websiteUri",
                "rating",
                "userRatingCount",
                "photos"
        ));

        try {
            String placeDetailsUrl = placeDetailsUrlFormat.formatted(placeId, fields);
            JsonNode placeDetails = getJsonResponseByUrl(placeDetailsUrl);

            String name = placeDetails.path("displayName").path("text").asText("");
            String address = placeDetails.path("formattedAddress").asText("");
            String website = placeDetails.path("websiteUri").asText("");
            double rating = placeDetails.path("rating").asDouble(0.0);
            int reviewCount = placeDetails.path("userRatingCount").asInt(0);

            String imageUrl = "";
            JsonNode photos = placeDetails.path("photos");
            if (photos.isArray() && !photos.isEmpty()) {
                String photoRef = photos.get(0).path("name").asText();
                imageUrl = photoUrlFormat.formatted(photoRef);
            }

            return PlaceDetailsResponse.builder()
                    .placeId(placeId)
                    .name(name)
                    .imageUrl(imageUrl)
                    .address(address)
                    .rating(rating)
                    .webSite(website)
                    .reviewCount(reviewCount)
                    .relatedDiaries(relatedDiaryResponse)
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public PlaceSummaryResponse getPlaceSummary(String placeId) throws IOException {
        String fields = String.join(",", List.of(
                "displayName",
                "formattedAddress"
        ));
        String placeSummaryUrl = placeDetailsUrlFormat.formatted(placeId, fields);
        JsonNode placeSummary = getJsonResponseByUrl(placeSummaryUrl);

        String name = placeSummary.path("displayName").path("text").asText("");
        String address = placeSummary.path("formattedAddress").asText("");
        return PlaceSummaryResponse.builder()
                .placeId(placeId)
                .name(name)
                .address(address)
                .build();
    }


    private Set<RecommendPlaceResponse> findPlacesByTextSearch(String query, LatLng latLng) throws IOException {
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String autoCompletedUrl = autoCompletedFormat.formatted(encodedQuery, latLng.lat(), latLng.lng());

        JsonNode results = getJsonResponseByUrl(autoCompletedUrl).get("predictions");


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
        return RecommendPlaceResponse.builder()
                .placeId(placeId)
                .placeName(name)
                .address(address)
                .themes(themes)
                .build();
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
        log.info("url response={}", response);
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
