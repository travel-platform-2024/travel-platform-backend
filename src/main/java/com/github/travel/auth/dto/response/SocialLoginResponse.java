package com.github.travel.auth.dto.response;


import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocialLoginResponse {
    private String accessToken;
}
