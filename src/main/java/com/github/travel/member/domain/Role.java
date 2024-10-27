package com.github.travel.member.domain;


import lombok.Getter;

import java.util.Arrays;

public enum Role {
    USER("ROLE_USER", "일반회원"),
    ADMIN("ROLE_ADMIN", "어드민");

    @Getter
    private String key;
    private String title;

    Role(String key, String title) {
        this.key = key;
        this.title = title;
    }

    public static Role findByKey(String key){
        return Arrays.stream(Role.values())
                .filter(role -> role.getKey().equals(key))
                .findAny().orElse(null);
    }
}
