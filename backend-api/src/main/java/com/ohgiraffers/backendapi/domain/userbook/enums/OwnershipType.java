package com.ohgiraffers.backendapi.domain.userbook.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OwnershipType {
    OWNED("소유"),
    RENTED("대여");

    private final String description;
}
