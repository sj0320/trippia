package com.trippia.travel.controller.dto.post.request;

import com.trippia.travel.domain.companionpost.post.CompanionPost;
import com.trippia.travel.domain.companionpost.post.Gender;
import com.trippia.travel.domain.location.city.City;
import com.trippia.travel.domain.user.User;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CompanionPostSaveRequest {

    @NotNull(message = "계획 중인 도시를 입력해주세요")
    private Long cityId; // 여행 도시 ID

    private String cityName; // 프론트엔드에서 보여주기용 (서버에는 사용 안할 수도 있음)

    @NotBlank(message = "제목을 입력해주세요")
    private String title;

    @NotBlank(message = "내용을 입력해주세요")
    private String content;

    @NotNull(message = "여행 시작일을 입력해주세요")
    private LocalDate startDate;

    @NotNull(message = "여행 종료일을 입력해주세요")
    private LocalDate endDate;

    @NotNull(message = "성별 제한을 선택해주세요")
    private Gender genderRestriction;

    @Min(value = 0)
    private Integer recruitmentCount;

    @Builder
    private CompanionPostSaveRequest(Long cityId, String cityName, String title, String content,
                                    LocalDate startDate, LocalDate endDate,
                                    Gender genderRestriction, Integer recruitmentCount) {
        this.cityId = cityId;
        this.cityName = cityName;
        this.title = title;
        this.content = content;
        this.endDate = endDate;
        this.startDate = startDate;
        this.genderRestriction = genderRestriction;
        this.recruitmentCount = recruitmentCount;
    }

    public CompanionPost toEntity(User user, City city, String thumbnailUrl) {
        return CompanionPost.builder()
                .user(user)
                .title(title)
                .content(content)
                .thumbnailUrl(thumbnailUrl)
                .city(city)
                .startDate(startDate)
                .endDate(endDate)
                .genderRestriction(genderRestriction)
                .recruitmentCount(recruitmentCount)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
