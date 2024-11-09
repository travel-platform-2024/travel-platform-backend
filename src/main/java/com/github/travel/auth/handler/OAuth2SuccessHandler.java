package com.github.travel.auth.handler;


import com.github.travel.auth.domain.CustomOAuth2User;
import com.github.travel.auth.dto.response.TokenResponseDto;
import com.github.travel.jwt.domain.RefreshToken;
import com.github.travel.jwt.filter.TokenProvider;
import com.github.travel.jwt.repository.JwtTokenRepository;
import com.github.travel.member.domain.Member;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final TokenProvider tokenProvider;
    private final JwtTokenRepository jwtTokenRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Member member = ((CustomOAuth2User) authentication.getPrincipal()).getMember();

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(member.getEmail(), member.getPassword());

        //access token & resfresh token 발급
        TokenResponseDto token  = TokenResponseDto.builder()
                .accessToken(tokenProvider.generateAccessToken(authentication))
                .refreshToken(tokenProvider.generateRefreshToken(authentication))
                .build();
        //refresh token 저장
        RefreshToken refreshToken = RefreshToken.builder()
                .email(authentication.getName())
                .refreshToken(token.getRefreshToken())
                .build();

        jwtTokenRepository.save(refreshToken);

        Cookie cookie = new Cookie("refreshToken", token.getRefreshToken());
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24);

        response.addCookie(cookie);
        response.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token.getAccessToken());

        response.setStatus(HttpStatus.OK.value());
    }
}
