package com.trippia.travel;

import com.trippia.travel.annotation.CurrentUser;
import com.trippia.travel.domain.user.User;
import com.trippia.travel.domain.user.UserRepository;
import com.trippia.travel.exception.user.UserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalControllerAdvice {

    private final UserRepository userRepository;

    @ModelAttribute("loginUserId")
    public Long getLoginUserId(@CurrentUser String email) {
        if (email != null) {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UserException("사용자를 찾을 수 없습니다."));
            return user.getId();
        }
        return null;
    }
}