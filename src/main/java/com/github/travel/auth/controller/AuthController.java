package com.github.travel.auth.controller;

import com.github.commonlib.ApiResponse;
import com.github.travel.auth.dto.request.LoginRequestDto;
import com.github.travel.auth.dto.request.SignupRequestDto;
import com.github.travel.auth.dto.request.TokenRequestDto;
import com.github.travel.auth.dto.response.EmailCheckResponseDto;
import com.github.travel.auth.dto.response.MemberResponseDto;
import com.github.travel.auth.dto.response.TokenResponseDto;
import com.github.travel.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/auth/reissue")
    public ResponseEntity<ApiResponse<?>> reissueToken(@RequestBody TokenRequestDto tokenRequestDto){
        ApiResponse<TokenResponseDto> response = ApiResponse.success(authService.reissue(tokenRequestDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/auth/signup")
    public ResponseEntity<ApiResponse<?>> signup(@RequestBody @Valid SignupRequestDto signupRequestDto){
        ApiResponse<MemberResponseDto> response = ApiResponse.success(authService.signup(signupRequestDto),"회원가입 성공");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ApiResponse<?>> login(@RequestBody LoginRequestDto loginRequestDto){
        ApiResponse<TokenResponseDto> response = ApiResponse.success(authService.login(loginRequestDto), "로그인 성공");

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/auth/email/exists")
    public ResponseEntity<ApiResponse<?>> checkEmailDuplicate(@RequestParam("email")String email){
        ApiResponse<EmailCheckResponseDto> response = ApiResponse.success(authService.checkEmailDuplicate(email));

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
