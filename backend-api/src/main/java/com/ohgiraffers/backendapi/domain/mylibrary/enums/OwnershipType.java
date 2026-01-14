package com.ohgiraffers.backendapi.domain.mylibrary.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OwnershipType {
    OWNED("소유"),
    RENTED("대여");

    private final String description;
}
