package com.trippia.travel.domain.user;

import com.trippia.travel.controller.dto.user.requset.UserInfoUpdateRequest;
import com.trippia.travel.controller.dto.user.requset.UserSaveRequest;
import com.trippia.travel.controller.dto.user.response.MyPageUserInfoResponse;
import com.trippia.travel.domain.common.EmailAuthPurpose;
import com.trippia.travel.domain.common.LoginType;
import com.trippia.travel.exception.user.UserException;
import com.trippia.travel.file.FileService;
import com.trippia.travel.mail.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.Random;

import static com.trippia.travel.domain.user.TravelerGrade.BEGINNER;
import static com.trippia.travel.exception.ErrorMessageSource.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;
    private final MailService mailService;
    private final FileService fileService;
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
                .grade(BEGINNER)
                .profileImageUrl(DEFAULT_PROFILE_IMAGE)
                .build();

        userRepository.save(user);
    }

    @Transactional
    public void saveSocialUser(String email, String nickname) {
        User user = getUser(email);

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
        if (optionalUser.isEmpty()) {
            return DEFAULT_PROFILE_IMAGE;
        }
        return optionalUser.get().getProfileImageUrl();
    }

    public MyPageUserInfoResponse getMyPageUserInfo(String email) {
        User user = getUser(email);
        return MyPageUserInfoResponse.builder()
                .userId(user.getId())
                .profileImageUrl(user.getProfileImageUrl())
                .nickname(user.getNickname())
                .bio(user.getBio())
                .travelerGrade(user.getGrade().getDisplayName())
                .build();
    }

    @Transactional
    public void updateUserInfo(String email, UserInfoUpdateRequest request, MultipartFile profileImage) {
        User user = getUser(email);
        if(!user.getNickname().equals(request.getNickname())){
            isDuplicatedNickname(request.getNickname());
        }
        String profileImageUrl = user.getProfileImageUrl();
        if(profileImage!=null && !profileImage.isEmpty()) {
            profileImageUrl = fileService.uploadFile(profileImage).getUrl();
        }
        user.updateProfileInfo(request.getNickname(), request.getBio(), profileImageUrl);
    }

    private String generateRandomCode() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND_MESSAGE));
    }

    private void isDuplicatedNickname(String nickname) {
        if(userRepository.existsByNickname(nickname)){
            throw new UserException("중복된 닉네임입니다.");
        }
    }
}
