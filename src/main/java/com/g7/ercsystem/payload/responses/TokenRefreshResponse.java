package com.g7.ercsystem.payload.responses;

import lombok.Data;

@Data
public class TokenRefreshResponse {
    private String accessToken;
    public TokenRefreshResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}
