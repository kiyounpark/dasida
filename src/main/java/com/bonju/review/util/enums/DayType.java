package com.bonju.review.util.enums;

import lombok.Getter;

@Getter
public enum DayType {
    ZERO(0),    // 0일차
    THREE(3),   // 3일차
    SEVEN(7),   // 7일차
    THIRTY(30); // 30일차

    private final int daysAgo;

    DayType(int daysAgo) {
        this.daysAgo = daysAgo;
    }
}