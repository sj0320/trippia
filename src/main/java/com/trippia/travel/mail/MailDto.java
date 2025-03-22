package com.trippia.travel.mail;

import com.trippia.travel.domain.common.EmailAuthPurpose;
import lombok.Getter;
import lombok.Setter;

public class MailDto {

    @Getter
    @Setter
    public static class MailRequest {
        private String email;
        private EmailAuthPurpose purpose;
    }

    @Getter @Setter
    public static class MailVerificationRequest{
        private String email;
        private String code;
        private EmailAuthPurpose purpose;
    }

}
