package com.trippia.travel.domain.companionpost.post;

import lombok.Getter;

@Getter
public enum Gender {
    MALE("남성"),
    FEMALE("여성"),
    ALL("무관");

    private final String displayName;

    Gender(String displayName){
        this.displayName = displayName;
    }

}
