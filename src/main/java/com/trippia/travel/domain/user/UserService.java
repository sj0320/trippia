package com.trippia.travel.domain.user;

import com.trippia.travel.domain.common.EmailAuthPurpose;
import com.trippia.travel.exception.user.UserException;
import com.trippia.travel.mail.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

import static com.trippia.travel.domain.user.dto.UserDto.SaveRequest;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final MailService mailService;

    @Transactional
    public void saveUser(SaveRequest request){
        if(userRepository.existsByEmail(request.getEmail())){
            throw new UserException("email","duplicate.email");
        }

        if(userRepository.existsByNickname(request.getNickname())){
            throw new UserException("nickname","duplicate.nickname");
        }
        User user = request.toEntity();
        userRepository.save(user);
    }

    public void sendCodeToEmail(String email, EmailAuthPurpose authPurpose){
        String authCode = generateRandomCode();
        mailService.sendEmail(email, authPurpose, authCode);
    }

    private String generateRandomCode() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }

}
