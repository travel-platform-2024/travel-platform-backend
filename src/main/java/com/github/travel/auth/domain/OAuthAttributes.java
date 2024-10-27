package com.github.travel.auth.domain;

import com.github.travel.member.domain.LoginType;
import com.github.travel.member.domain.Member;
import com.github.travel.member.domain.Role;
import lombok.*;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

import java.util.Map;

@Builder
public record OAuthAttributes (
    Map<String, Object> attributes,
    String name,
    String email,
    String picture,
    LoginType loginType
){
    public static OAuthAttributes of(String registrationId, Map<String, Object> attributes) throws OAuth2AuthenticationException {
        return switch (registrationId) {
            case "google" -> ofGoogle(attributes);
            case "kakao" -> ofKakao(attributes);
            default -> throw new OAuth2AuthenticationException("ILLIGAL REGISTRATION ID");
        };
    }

    public static OAuthAttributes ofGoogle(Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .loginType(LoginType.GOOGLE)
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .picture((String) attributes.get("picture"))
                .attributes(attributes)
                .build();
    }

    private static OAuthAttributes ofKakao(Map<String, Object> attributes) {
        Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) account.get("profile");

        return OAuthAttributes.builder()
                .loginType(LoginType.KAKAO)
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .picture((String) attributes.get("picture"))
                .attributes(attributes)
                .build();
    }

    public Member toEntity(){
        return Member.builder()
                .nickname(name)
                .email(email)
                .role(Role.USER)
                .loginType(loginType)
                .build();
    }
}
