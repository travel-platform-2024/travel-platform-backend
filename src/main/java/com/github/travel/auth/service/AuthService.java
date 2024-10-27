package com.github.travel.auth.service;

import com.github.commonlib.ApiException;
import com.github.commonlib.ErrorCode;
import com.github.travel.jwt.domain.RefreshToken;
import com.github.travel.jwt.filter.TokenProvider;
import com.github.travel.jwt.repository.JwtTokenRepository;
import com.github.travel.member.domain.Member;
import com.github.travel.member.repository.MemberRepository;
import com.github.travel.auth.dto.request.LoginRequestDto;
import com.github.travel.auth.dto.request.SignupRequestDto;
import com.github.travel.auth.dto.request.TokenRequestDto;
import com.github.travel.auth.dto.response.EmailCheckResponseDto;
import com.github.travel.auth.dto.response.MemberResponseDto;
import com.github.travel.auth.dto.response.TokenResponseDto;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final TokenProvider tokenProvider;
    private final JwtTokenRepository jwtTokenRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    public AuthService(TokenProvider tokenProvider, JwtTokenRepository jwtTokenRepository, AuthenticationManagerBuilder authenticationManagerBuilder, PasswordEncoder passwordEncoder, MemberRepository memberRepository) {
        this.tokenProvider = tokenProvider;
        this.jwtTokenRepository = jwtTokenRepository;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.passwordEncoder = passwordEncoder;
        this.memberRepository = memberRepository;
    }

    private String encodePassword(String password){
        return passwordEncoder.encode(password);
    }

    /*
    이메일 중복 체크
     */
    public EmailCheckResponseDto checkEmailDuplicate(String email){
        boolean isDuplicated = memberRepository.existsByEmail(email);

        return EmailCheckResponseDto.builder()
                .isDuplicated(isDuplicated)
                .build();
    }

    /*
    회원가입
     */
    @Transactional
    public MemberResponseDto signup(SignupRequestDto signupRequestDto) {
        if (memberRepository.existsByEmail(signupRequestDto.getEmail())) {
            throw new ApiException(ErrorCode.EMAIL_CONFLICT, "가입되어 있는 이메일 주소입니다.");
        }

        signupRequestDto.setPassword(encodePassword(signupRequestDto.getPassword()));
        Member member = signupRequestDto.toEntity(signupRequestDto);

        return MemberResponseDto.of(memberRepository.save(member));
    }

    /*
    로그인
     */
    @Transactional
    public TokenResponseDto login(LoginRequestDto loginRequestDto){
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword());

        //access token & resfresh token 발급
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        TokenResponseDto tokenDto = TokenResponseDto.builder()
                .accessToken(tokenProvider.generateAccessToken(authentication))
                .refreshToken(tokenProvider.generateRefreshToken(authentication))
                .build();

        //refresh token 저장
        RefreshToken refreshToken = RefreshToken.builder()
                .email(authentication.getName())
                .refreshToken(tokenDto.getRefreshToken())
                .build();

        jwtTokenRepository.save(refreshToken);

        return tokenDto;
    }

    /*
    토큰 재발급
     */
    @Transactional
    public TokenResponseDto reissue(TokenRequestDto tokenRequestDto) {
        //refresh token 검증
        if (!tokenProvider.validateToken(tokenRequestDto.getRefreshToken())) {
            throw new ApiException(ErrorCode.INVALID_JWT_TOKEN,"refresh token이 유효하지 않습니다.");
        }

        Authentication authentication = tokenProvider.getAuthentication(tokenRequestDto.getAccessToken());
        RefreshToken refreshToken = jwtTokenRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ApiException(ErrorCode.BAD_REQUEST, "로그아웃 된 사용자입니다."));

        if (!refreshToken.getRefreshToken().equals(tokenRequestDto.getRefreshToken())) {
            throw new ApiException(ErrorCode.BAD_REQUEST,"토큰의 유저 정보가 일치하지 않습니다.");
        }

        //토큰 발급
        String newAccessToken = null;
        String newRefreshToken = null;
        if (tokenProvider.needGenerateRefreshToken(refreshToken.getRefreshToken())) {
            newAccessToken = tokenProvider.generateAccessToken(authentication);
            newRefreshToken = tokenProvider.generateRefreshToken(authentication);

            jwtTokenRepository.save(refreshToken.updateRefreshToken(newRefreshToken));
        }
        else {
            newAccessToken = tokenProvider.generateAccessToken(authentication);
        }

        // 토큰 발급
        return TokenResponseDto.builder()
                .accessToken(newAccessToken)
                .refreshToken((newRefreshToken == null ? refreshToken.getRefreshToken() : newRefreshToken))
                .build();
    }
}
