package com.trippia.travel.controller.dto.theme.response;

import com.trippia.travel.domain.theme.Theme;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ThemeResponse {

    private Long id;
    private String name;

    public static List<ThemeResponse> fromEntities(List<Theme> themes) {
        return themes.stream()
                .map(theme -> new ThemeResponse(theme.getId(), theme.getName()))
                .toList();
    }
}
