package com.trippia.travel.domain.user;

import com.trippia.travel.controller.dto.user.requset.UserSaveRequest;
import com.trippia.travel.domain.common.EmailAuthPurpose;
import com.trippia.travel.domain.common.LoginType;
import com.trippia.travel.domain.common.Role;
import com.trippia.travel.exception.user.UserException;
import com.trippia.travel.mail.MailService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ActiveProfiles("test")
@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @MockitoBean
    private MailService mailService;

    @AfterEach
    void tearDown(){
        userRepository.deleteAllInBatch();
    }


    @DisplayName("회원 정보로 일반 회원가입에 성공한다.")
    @Test
    void saveUser_success() {
        // given
        UserSaveRequest request = createUserSaveRequest("test@example.com", "password", "test");
        // when
        userService.saveUser(request);
        // then
        User savedUser = userRepository.findByEmail("test@example.com").orElseThrow();
        assertThat(savedUser.getNickname()).isEqualTo("test");
        assertThat(savedUser.getLoginType()).isEqualTo(LoginType.LOCAL);
        assertThat(passwordEncoder.matches("password", savedUser.getPassword())).isTrue();
    }

    @DisplayName("중복된 이메일로 회원가입 시 예외가 발생한다.")
    @Test
    void saveUser_duplicateEmail_fail() {
        // given
        UserSaveRequest request_1 = createUserSaveRequest("test@example.com", "password", "test");
        UserSaveRequest request_2 = createUserSaveRequest("test@example.com", "password_2", "test_2");
        userService.saveUser(request_1);

        // when & then
        UserException exception = (UserException) catchThrowable(() -> userService.saveUser(request_2));
        assertThat(exception.getFieldName()).isEqualTo("email");
        assertThat(exception.getCode()).isEqualTo("duplicate.email");
    }

    @DisplayName("중복된 닉네임으로 회원가입 시 예외가 발생한다.")
    @Test
    void saveUser_duplicateNickname_fail() {
        // given
        UserSaveRequest request_1 = createUserSaveRequest("test@example.com", "password", "test");
        UserSaveRequest request_2 = createUserSaveRequest("test2@example.com", "password_2", "test");
        userService.saveUser(request_1);
        // when & then
        UserException userException = (UserException) catchThrowable(() -> userService.saveUser(request_2));
        assertThat(userException.getFieldName()).isEqualTo("nickname");
        assertThat(userException.getCode()).isEqualTo("duplicate.nickname");
    }

    @DisplayName("소셜 로그인 사용자가 닉네임을 등록하고 회원가입을 완료한다.")
    @Test
    void saveSocialUser_success() {
        // given
        String registeredEmail = "test@example.com";
        User user = User.builder().email(registeredEmail).password("social").build();
        userRepository.save(user);
        String nickname = "test";
        // when
        userService.saveSocialUser(registeredEmail, nickname);
        // then
        User savedUser = userRepository.findByEmail(registeredEmail).orElseThrow();
        assertThat(savedUser.getNickname()).isEqualTo(nickname);
        assertThat(savedUser.getLoginType()).isNotEqualTo(LoginType.LOCAL);
        assertThat(savedUser.getRole()).isEqualTo(Role.ROLE_USER);
    }


    @DisplayName("이메일 인증 코드를 전송할때 mailService를 호출한다")
    @Test
    void sendCodeToEmail() {
        // given
        String email = "test@example.com";
        EmailAuthPurpose purpose = EmailAuthPurpose.REGISTER;
        // when
        userService.sendCodeToEmail(email, purpose);
        // then
        verify(mailService).sendEmail(
                eq(email), eq(purpose), anyString()
        );
    }


    private UserSaveRequest createUserSaveRequest(String email, String password, String nickname) {
        UserSaveRequest request = new UserSaveRequest();
        request.setEmail(email);
        request.setPassword(password);
        request.setNickname(nickname);
        return request;
    }

}