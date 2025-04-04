package com.trippia.travel.domain.common;

public enum TravelCompanion {
    SOLO("혼자"),
    FRIEND("친구"),
    FAMILY("가족"),
    COUPLE("연인"),
    CHILD("아이"),
    OTHERS("기타");

    private final String description;

    // 생성자
    TravelCompanion(String description) {
        this.description = description;
    }

    public static TravelCompanion fromString(String value){
        for(TravelCompanion companion : TravelCompanion.values()){
            if(companion.description.equals(value)){
                return companion;
            }
        }
        throw new IllegalArgumentException("존재하지 않는 companion 유형입니다: " + value);
    }
}