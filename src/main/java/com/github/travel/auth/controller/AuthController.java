package com.github.travel.auth.controller;

import com.github.commonlib.CommonResponse;
import com.github.commonlib.ErrorResponse;
import com.github.travel.auth.dto.request.LoginRequestDto;
import com.github.travel.auth.dto.request.SignupRequestDto;
import com.github.travel.auth.dto.request.TokenRequestDto;
import com.github.travel.auth.dto.response.EmailCheckResponseDto;
import com.github.travel.auth.dto.response.MemberResponseDto;
import com.github.travel.auth.dto.response.TokenResponseDto;
import com.github.travel.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "계정 API")
@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "토큰 재발급", description = "토큰 재발급")
    @ApiResponses({
            @ApiResponse(responseCode = "201",
                    description = "토큰 발급에 성공하였습니다.",
                    content = @Content(schema = @Schema(implementation = CommonResponse.class))),
            @ApiResponse(responseCode = "400",
                    description = "E40001) 로그아웃된 사용자입니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400",
                    description = "E40001) 유저 정보가 일치하지 않습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401",
                    description = "E40102) 유효하지 않은 refresh token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403",
                    description = "E40302) 인증되지 않은 사용자입니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/auth/reissue")
    public ResponseEntity<CommonResponse<?>> reissueToken(@RequestBody TokenRequestDto tokenRequestDto){
        CommonResponse<TokenResponseDto> response = CommonResponse.success(authService.reissue(tokenRequestDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @Operation(summary = "회원가입", description = "회원가입")
    @ApiResponses({
            @ApiResponse(responseCode = "201",
                    description = "회원가입에 성공하였습니다.",
                    content = @Content(schema = @Schema(implementation = CommonResponse.class))),
            @ApiResponse(responseCode = "400",
                    description = "E40001) 입력값을 잘못 입력하였습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409",
                    description = "E40901) 가입되어 있는 이메일 주소입니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/auth/signup")
    public ResponseEntity<CommonResponse<?>> signup(@RequestBody @Valid SignupRequestDto signupRequestDto){
        CommonResponse<MemberResponseDto> response = CommonResponse.success(authService.signup(signupRequestDto),"회원가입 성공");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @Operation(summary = "로그인", description = "로그인")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "로그인에 성공하였습니다.",
                    content = @Content(schema = @Schema(implementation = CommonResponse.class))),
            @ApiResponse(responseCode = "401",
                    description = "E40101) 계정 정보가 맞지 않습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/auth/login")
    public ResponseEntity<CommonResponse<?>> login(@RequestBody LoginRequestDto loginRequestDto){
        CommonResponse<TokenResponseDto> response = CommonResponse.success(authService.login(loginRequestDto), "로그인 성공");

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/auth/email/exists")
    public ResponseEntity<CommonResponse<?>> checkEmailDuplicate(@RequestParam("email")String email){
        CommonResponse<EmailCheckResponseDto> response = CommonResponse.success(authService.checkEmailDuplicate(email));

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


}
