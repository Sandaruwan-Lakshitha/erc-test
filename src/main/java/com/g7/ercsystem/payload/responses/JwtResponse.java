package com.g7.ercsystem.payload.responses;

import lombok.Data;

import java.util.List;

@Data
public class JwtResponse {

    private String access;
    private String refresh;
    private List<String> roles;

    public JwtResponse(String access, String refresh,List<String> roles) {
        this.access = access;
        this.refresh = refresh;
        this.roles = roles;
    }
}
