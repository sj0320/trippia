package com.trippia.travel.mail;

import com.trippia.travel.domain.common.EmailAuthPurpose;
import com.trippia.travel.exception.user.UserException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class MailService {
    private static final String REGISTER_EMAIL_TEMPLATE = "user/sign-up-email.html";
    private static final String FIND_PASSWORD_EMAIL_TEMPLATE = "user/find-password-email.html";
    private static final String REGISTER_VERIFICATION_KEY = "email_auth:%s:%s";
    private static final String FIND_PASSWORD_VERIFICATION_KEY = "find_password:%s:%s";

    private static final String VERIFIED_KEY = "auth:verified:%s:%s";
    private static final long EMAIL_CODE_EXPIRATION_MINUTES = 5; // 만료 시간 (5분)
    private static final long EMAIL_VERIFIED_EXPIRATION_MINUTES = 10; // 인증 확인 만료 시간 (10분)

    private final JavaMailSender javaMailSender;
    private final ApplicationContext applicationContext;
    private final RedisTemplate<String, Object> redisTemplate;

    public void sendEmail(String email, EmailAuthPurpose authPurpose, String code) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            String text = createMailMessage(authPurpose, code);
            mimeMessageHelper.setSubject(getSubject(authPurpose));
            mimeMessageHelper.setText(text, true);
            mimeMessageHelper.setTo(email);
            javaMailSender.send(mimeMessage);

            // Redis에 인증 코드 저장 (만료 시간 설정)
            storeEmailAuthCode(email, authPurpose, code);
        } catch (MessagingException e) {
            throw new RuntimeException("이메일 전송 실패", e);
        }
    }

    public void verifyEmailCode(String email, EmailAuthPurpose authPurpose, String inputCode) {
        String key = generateKeyByPurpose(email, authPurpose);
        String storedCode = (String) redisTemplate.opsForValue().get(key);
        log.info("storeCode={}",storedCode);

        if (storedCode == null) {
            throw new UserException("이메일 인증번호가 만료되었습니다. 새로운 인증번호를 요청해주세요.");
        }

        if (!storedCode.equals(inputCode)) {
            throw new UserException("이메일 인증번호가 일치하지 않습니다. 다시 확인해주세요.");
        }

        redisTemplate.delete(key);
        storeEmailVerificationStatus(email, authPurpose);
    }

    public void isEmailVerified(String email, EmailAuthPurpose authPurpose){
        String key = generateEmailVerificationStatusKey(email, authPurpose);
        if(redisTemplate.opsForValue().get(key)==null){
            throw new UserException("email", "emailVerification.required");
        }
    }

    private String createMailMessage(EmailAuthPurpose purpose, String code) {
        String templateName = getTemplateName(purpose);
        Context context = new Context();
        context.setVariable("code", code);

        return applicationContext.getBean(org.thymeleaf.TemplateEngine.class)
                .process(templateName, context);
    }

    private String getTemplateName(EmailAuthPurpose purpose) {
        return switch (purpose) {
            case REGISTER -> REGISTER_EMAIL_TEMPLATE;
            case FIND_PASSWORD -> FIND_PASSWORD_EMAIL_TEMPLATE;
            default -> throw new IllegalArgumentException("지원되지 않는 인증 목적입니다.");
        };
    }

    private String getSubject(EmailAuthPurpose purpose) {
        return switch (purpose) {
            case REGISTER -> "회원가입 인증 코드";
            case FIND_PASSWORD -> "비밀번호 찾기 인증 코드";
            default -> "인증 코드";
        };
    }

    private void storeEmailAuthCode(String email, EmailAuthPurpose authPurpose, String code) {
        String key = generateKeyByPurpose(email, authPurpose);
        redisTemplate.opsForValue().set(key, code, EMAIL_CODE_EXPIRATION_MINUTES, TimeUnit.MINUTES);
        log.info("이메일 인증 코드 저장 완료 - 키: {}, 코드: {}", key, code);
    }

    private void storeEmailVerificationStatus(String email, EmailAuthPurpose authPurpose) {
        String verifiedKey = generateEmailVerificationStatusKey(email, authPurpose);
        redisTemplate.opsForValue().set(verifiedKey, "true", EMAIL_VERIFIED_EXPIRATION_MINUTES, TimeUnit.MINUTES);
    }

    private static String generateEmailVerificationStatusKey(String email, EmailAuthPurpose authPurpose) {
        return String.format(VERIFIED_KEY, email, authPurpose.name());
    }

    private String generateKeyByPurpose(String email, EmailAuthPurpose authPurpose) {
        String keyPattern = getKeyPatternByPurpose(authPurpose);
        return String.format(keyPattern, authPurpose.name().toLowerCase(), email);
    }

    private String getKeyPatternByPurpose(EmailAuthPurpose authPurpose) {
        return switch (authPurpose) {
            case REGISTER -> REGISTER_VERIFICATION_KEY;
            case FIND_PASSWORD -> FIND_PASSWORD_VERIFICATION_KEY;
            default -> throw new IllegalArgumentException("Unsupported auth purpose: " + authPurpose);
        };
    }
}