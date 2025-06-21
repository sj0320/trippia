package com.trippia.travel.controller.dto.post.request;

import com.trippia.travel.domain.companionpost.post.Gender;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class CompanionPostUpdateRequest {

    @NotNull(message = "계획 중인 도시를 입력해주세요")
    private Long cityId; // 여행 도시 ID

    private String cityName; // 프론트엔드에서 보여주기용 (서버에는 사용 안할 수도 있음)

    @NotBlank(message = "제목을 입력해주세요")
    private String title;

    @NotBlank(message = "내용을 입력해주세요")
    private String content;

    @NotNull(message = "여행 시작일을 입력해주세요")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @NotNull(message = "여행 종료일을 입력해주세요")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @NotNull(message = "성별 제한을 선택해주세요")
    private Gender genderRestriction;

    @Min(value = 0)
    private Integer recruitmentCount;

    @Builder
    private CompanionPostUpdateRequest(Long cityId, String cityName, String title, String content,
                                      LocalDate startDate, LocalDate endDate, Gender genderRestriction,
                                      Integer recruitmentCount) {
        this.cityId = cityId;
        this.cityName = cityName;
        this.title = title;
        this.content = content;
        this.startDate = startDate;
        this.endDate = endDate;
        this.genderRestriction = genderRestriction;
        this.recruitmentCount = recruitmentCount;
    }

}
