package com.github.travel.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenRequestDto {

    @Schema(description = "access 토큰", example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhYWFAbmF2ZXIuY29tIiwicm9sZSI6IlJPTEVfVVNFUiIsImlhdCI6MTczMDAyMDI3NCwiZXhwIjoxNzMwMDIyMDc0fQ.T-Hy2PL9NNDsrGVe2DnzsQq_bxsQtxW3TRUEj5iXz2-nI96S0VB_oEQXA4QnFh5qVoFCfU4nyeSkBkiWd9zmlA")
    private String accessToken;

    @Schema(description = "refresh 토큰", example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhYWFAbmF2ZXIuY29tIiwicm9sZSI6IlJPTEVfVVNFUiIsImlhdCI6MTczMDAyMDI1NCwiZXhwIjoxNzMwNjI1MDU0fQ.LwcLJgM1jUEZ0YCckNZXxth_5XuEi_mtVX08WM4Srv38AhaanyepyGx4GRixDMdFpdccMDMFxWew-U0Z69x1sg")
    private String refreshToken;
}