package com.github.travel.auth.dto.request;


import com.github.travel.member.domain.LoginType;
import com.github.travel.member.domain.Member;
import com.github.travel.member.domain.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class SignupRequestDto {
    @Email
    @NotNull
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}", message = "비밀번호는 8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
    private String password;

    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9-_]{2,10}$", message = "닉네임은 특수문자를 제외한 2~10자리여야 합니다.")
    private String nickname;

    @Pattern(regexp = "^01([0|1|6|7|8|9])-?([0-9]{3,4})-?([0-9]{4})$")
    private String phoneNumber;

    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9-_,]{2,50}$", message = "주소는 한글, 영어대소문자, 숫자만 사용해주세요.")
    private String address;

    @Pattern(regexp = "^[A-Z_]{6,10}$", message = "회원 타입은 ROLE_ADMIN, ROLE_USER 중 입력해주세요")
    private String role;


    public Member toEntity(SignupRequestDto signupRequestDto){

        return Member.builder()
                .email(signupRequestDto.getEmail())
                .password(signupRequestDto.getPassword())
                .nickname(signupRequestDto.getNickname())
                .phoneNum(signupRequestDto.getPhoneNumber())
                .address(signupRequestDto.getAddress())
                .loginType(LoginType.BASIC)
                .role(Role.findByKey(signupRequestDto.role))
                .build();
    }
}
