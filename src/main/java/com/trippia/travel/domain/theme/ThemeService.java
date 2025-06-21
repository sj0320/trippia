package com.trippia.travel.domain.theme;

import com.trippia.travel.controller.dto.theme.response.ThemeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ThemeService {

    private final ThemeRepository themeRepository;

    public List<ThemeResponse> getAllThemes(){
        return ThemeResponse.fromEntities(themeRepository.findAll());
    }

}
