package com.github.travel.common.utils;

import com.github.commonlib.ApiException;
import com.github.commonlib.ErrorCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
@NoArgsConstructor
public class SecurityUtil {

    public static String getMemberEmail() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            throw new ApiException(ErrorCode.INVALID_JWT_TOKEN, "인증 정보가 없습니다.");
        }

        return authentication.getName();
    }
}
