package com.github.travel.auth.domain;

import com.github.travel.member.domain.LoginType;
import com.github.travel.member.domain.Member;
import com.github.travel.member.domain.Role;
import lombok.*;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OAuthAttributes{

    private Map<String, Object> attributes;
    private String userNameAttributeKey;
    private String name;
    private String email;
    private String picture;
    private LoginType loginType;

    public static OAuthAttributes of(String registrationId,String userNameAttributeKey, Map<String, Object> attributes) throws OAuth2AuthenticationException {
        return switch (registrationId) {
            case "google" -> ofGoogle(userNameAttributeKey, attributes);
            default -> throw new OAuth2AuthenticationException("ILLIGAL REGISTRATION ID");
        };
    }

    public static OAuthAttributes ofGoogle(String userNameAttributeKey, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .loginType(LoginType.GOOGLE)
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .picture((String) attributes.get("picture"))
                .userNameAttributeKey(userNameAttributeKey)
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
