package com.trippia.travel.domain.common;

import org.springframework.data.domain.Sort;

import java.util.Arrays;

public enum SortOption {

    LATEST("latest", Sort.by(Sort.Direction.DESC, "createdAt")),
    VIEWS("views", Sort.by(Sort.Direction.DESC, "viewCount")),
    POPULAR("likes", Sort.by(Sort.Direction.DESC, "likeCount")),
    OLDEST("oldest", Sort.by(Sort.Direction.ASC, "createdAt"));

    private final String key;
    private final Sort sort;

    SortOption(String key, Sort sort) {
        this.key = key;
        this.sort = sort;
    }

    public Sort getSort(){
        return sort;
    }

    public static SortOption from(String key) {
        return Arrays.stream(values())
                .filter(option -> option.key.equalsIgnoreCase(key))
                .findFirst()
                .orElse(LATEST);
    }

}
