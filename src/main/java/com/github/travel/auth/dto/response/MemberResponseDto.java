package com.github.travel.auth.dto.response;

import com.github.travel.member.domain.Member;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class MemberResponseDto {
    private String email;
    private String nickName;

    public static MemberResponseDto of(Member member){
        return MemberResponseDto.builder()
                .email(member.getEmail())
                .nickName(member.getNickname())
                .build();
    }
}
