package com.trippia.travel.util;

import java.util.Map;

public class PlaceTypeMapper {

    private static final Map<String, String> TYPE_NAME_MAP = Map.ofEntries(
            // 관광 관련
            Map.entry("tourist_attraction", "관광 명소"),
            Map.entry("travel_agency", "관광 명소"),
            Map.entry("casino", "관광 명소"),

            // 교통 관련
            Map.entry("car_rental", "렌터카"),

            // 음식/음료
            Map.entry("restaurant", "음식점"),
            Map.entry("cafe", "카페"),
            Map.entry("bar", "바"),
            Map.entry("food", "음식점"),
            Map.entry("meal_takeaway", "음식점"),

            // 문화/예술
            Map.entry("museum", "박물관"),
            Map.entry("art_gallery", "미술관"),
            Map.entry("movie_theater", "영화관"),

            // 놀이/오락
            Map.entry("amusement_park", "놀이공원"),
            Map.entry("zoo", "동물원"),
            Map.entry("aquarium", "아쿠아리움"),
            Map.entry("night_club", "나이트클럽"),

            // 종교
            Map.entry("church", "교회"),
            Map.entry("hindu_temple", "힌두 사원"),
            Map.entry("mosque", "모스크"),
            Map.entry("synagogue", "유대교 회당"),
            Map.entry("place_of_worship","종교 시설"),

            // 쇼핑/휴식
            Map.entry("shopping_mall", "쇼핑몰"),
            Map.entry("park", "공원"),

            // 기타
            Map.entry("point_of_interest", "기타"),
            Map.entry("establishment", "기타")
    );

    private static final Map<String, String> KOREAN_TO_GOOGLE_MAP = Map.of(
            "맛집", "restaurant",
            "관광", "tourist_attraction",
            "카페", "cafe",
            "공원", "park",
            "박물관", "museum",
            "쇼핑", "shopping_mall"
    );

    public static String translate(String type) {
        return TYPE_NAME_MAP.getOrDefault(type, type);
    }

    public static String convertToGoogleType(String koreanType){
        return KOREAN_TO_GOOGLE_MAP.getOrDefault(koreanType,null);
    }

}
