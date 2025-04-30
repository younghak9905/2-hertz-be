package com.hertz.hertz_be.domain.user.entity.enums;

import lombok.Getter;

@Getter
public enum Gender {
    MALE("남자"),
    FEMALE("여자");

    private final String label;

    Gender(String label) {
        this.label = label;
    }
}
