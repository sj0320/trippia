package com.trippia.travel.domain.user;

public enum TravelerGrade {

    BEGINNER(0, "초보자"),
    EXPLORER(50, "탐험가"),
    VOYAGER(150, "여행자"),
    ADVENTURER(300, "모험가"),
    NOMAD(600, "유목민"),
    MASTER(1000, "마스터");

    private final int requiredExp;
    private final String displayName;

    TravelerGrade(int requiredExp, String displayName){
        this.requiredExp = requiredExp;
        this.displayName = displayName;
    }

    public static TravelerGrade calculateGrade(int experience){
        TravelerGrade result = BEGINNER;
        for(TravelerGrade grade : values()){
            if(experience >= grade.requiredExp){
                result = grade;
            }
        }
        return result;
    }

    public String getDisplayName() {
        return displayName;
    }
}
