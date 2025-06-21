package com.trippia.travel.domain.common;

public enum CityType {
    JAPAN("일본"),
    KOREA("한국"),
    CHINA("중국"),
    EUROPE("유럽"),
    SOUTHEAST_ASIA("동남아시아"),
    OCEANIA("오세아니아"),
    NORTH_AMERICA("북미"),
    SOUTH_AMERICA("남미"),
    MIDDLE_EAST("중동");

    private final String name;

    CityType(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

}
