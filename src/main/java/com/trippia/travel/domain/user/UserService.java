package com.trippia.travel.domain.user;

import com.trippia.travel.controller.dto.user.requset.UserSaveRequest;
import com.trippia.travel.domain.common.EmailAuthPurpose;
import com.trippia.travel.domain.common.LoginType;
import com.trippia.travel.exception.user.UserException;
import com.trippia.travel.mail.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Random;

import static com.trippia.travel.exception.ErrorMessageSource.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;
    private final MailService mailService;
    private static final String DEFAULT_PROFILE_IMAGE = "https://trippia-storage.s3.ap-northeast-2.amazonaws.com/default-image-trrippia.png";

    @Transactional
    public void saveUser(UserSaveRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserException("email", DUPLICATE_EMAIL);
        }

        if (userRepository.existsByNickname(request.getNickname())) {
            throw new UserException("nickname", DUPLICATE_NICKNAME);
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(bCryptPasswordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .loginType(LoginType.LOCAL)
                .profileImageUrl(DEFAULT_PROFILE_IMAGE)
                .build();

        userRepository.save(user);
    }

    @Transactional
    public void saveSocialUser(String email, String nickname){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND_MESSAGE));

        if (userRepository.existsByNickname(nickname)) {
            throw new UserException("nickname", DUPLICATE_NICKNAME);
        }
        user.updateNickname(nickname);
        user.updateProfile(DEFAULT_PROFILE_IMAGE);
        user.completeRegistration();
    }

    public void sendCodeToEmail(String email, EmailAuthPurpose authPurpose) {
        String authCode = generateRandomCode();
        mailService.sendEmail(email, authPurpose, authCode);
    }

    public String getProfileImageUrl(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isEmpty()){
            return DEFAULT_PROFILE_IMAGE;
        }
        return optionalUser.get().getProfileImageUrl();
    }

    private String generateRandomCode() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }

}
