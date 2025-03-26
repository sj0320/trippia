package com.trippia.travel.domain.user.dto;

import com.trippia.travel.exception.user.PasswordMatch;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

public class UserDto {

    @Data
    @PasswordMatch
    public static class SaveRequest{
        @NotBlank(message = "이메일은 필수 항목입니다.")
        @Email(message = "이메일 형식이 유효하지 않습니다.")
        private String email;

        @Size(min = 6, max = 20, message = "비밀번호는 6자 이상 20자 이하로 입력해주세요.")
        private String password;

        @NotBlank(message = "비밀번호 확인은 필수 항목입니다.")
        private String confirmPassword;

        @Size(min = 2, max = 20, message = "닉네임은 2자 이상 20자 이하로 입력해주세요.")
        private String nickname;
    }

    @Getter @Setter
    public static class SocialSaveRequest{
        @NotBlank(message = "이메일은 필수 항목입니다.")
        @Email(message = "이메일 형식이 유효하지 않습니다.")
        private String email;

        @Size(min = 2, max = 20, message = "닉네임은 2자 이상 20자 이하로 입력해주세요.")
        private String nickname;

        private String socialType;
    }

}
