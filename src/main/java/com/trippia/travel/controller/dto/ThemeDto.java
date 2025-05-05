package com.trippia.travel.controller.dto;

import com.trippia.travel.domain.theme.Theme;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

public class ThemeDto {

    @AllArgsConstructor
    @Getter
    public static class ThemeResponse {
        private Long id;
        private String name;

        public static List<ThemeResponse> fromEntities(List<Theme> themes) {
            return themes.stream()
                    .map(theme -> new ThemeResponse(theme.getId(), theme.getName()))
                    .toList();
        }
    }


}
