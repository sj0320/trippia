package com.trippia.travel.domain.theme;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.trippia.travel.controller.dto.ThemeDto.ThemeResponse;

@Service
@RequiredArgsConstructor
public class ThemeService {

    private final ThemeRepository themeRepository;

    public List<ThemeResponse> getAllThemes(){
        return ThemeResponse.fromEntities(themeRepository.findAll());
    }

}
