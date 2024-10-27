package com.github.travel.member.domain;


import lombok.Getter;
import lombok.RequiredArgsConstructor;


public enum LoginType {
    GOOGLE("google"),
    KAKAO("kakao"),
    NAVER("naver"),
    BASIC("basic");


    private String registrationId;

    LoginType(String registrationId) {
        this.registrationId = registrationId;
    }

    public String getRegistrationId() {
        return registrationId;
    }
}
