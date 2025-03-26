package com.trippia.travel.domain.user.dto;

public interface OAuth2UserInfo {

    String getProvider();

    String getProviderId();

    String getEmail();

}
