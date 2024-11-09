package com.github.travel.auth.service;

import com.github.travel.auth.domain.CustomOAuth2User;
import com.github.travel.auth.domain.OAuthAttributes;
import com.github.travel.member.domain.LoginType;
import com.github.travel.member.domain.Member;
import com.github.travel.member.repository.MemberRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private static final Logger log = LoggerFactory.getLogger(CustomOAuth2UserService.class);
    private final MemberRepository memberRepository;
    private final HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        //서비스 ID
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        LoginType loginType = null;
        if("google".equals(registrationId)){
            loginType = LoginType.GOOGLE;
        }
        else if("naver".equals(registrationId)){
            loginType = LoginType.NAVER;
        }

        // OAuth2 로그인 진행 시 키가 되는 필드 값
        String userNameAttributeKey = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        // OAuth2UserService를 통해 가져온 OAuth2User의 attribute 등을 담을 클래스
        OAuthAttributes attributes = OAuthAttributes.of(registrationId,userNameAttributeKey, oAuth2User.getAttributes());

        // 사용자 저장 또는 업데이트
        Member member = saveSocialLogin(loginType, attributes);

        CustomOAuth2User customOAuth2User = new CustomOAuth2User(
                Collections.singleton(
                        new SimpleGrantedAuthority(member.getRole().getKey())),
                attributes.getAttributes(),
                attributes.getUserNameAttributeKey(),
                member
        );

        return customOAuth2User;
    }

    private Member saveSocialLogin(LoginType loginType, OAuthAttributes attributes){
        Member member = memberRepository.findByEmail(attributes.getEmail()).orElse(attributes.toEntity());

        if(loginType != member.getLoginType()){
            return null;
        }

        return memberRepository.save(member);
    }
}
