package com.github.travel.auth.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class EmailCheckResponseDto {
    boolean isDuplicated;
}
