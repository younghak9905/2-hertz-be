package com.hertz.hertz_be.domain.user.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OauthTokenInfo {
    private String refreshToken;
    private LocalDateTime refreshTokenExpiresAt;
}
