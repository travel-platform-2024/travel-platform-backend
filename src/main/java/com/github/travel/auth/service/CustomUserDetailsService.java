package com.github.travel.auth.service;

import com.github.commonlib.ApiException;
import com.github.commonlib.ErrorCode;
import com.github.travel.member.domain.Member;
import com.github.travel.member.repository.MemberRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;


    public CustomUserDetailsService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return memberRepository.findByEmail(username).map(this::createUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException(ErrorCode.LOGIN_FAIL.toString()));
    }

    private UserDetails createUserDetails(Member member){
        return User.builder()
                .username(member.getEmail())
                .password(member.getPassword())
                .roles(String.valueOf(member.getRole()))
                .build();
    }
}
