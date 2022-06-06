package com.g7.ercsystem.payload.requests;

import lombok.Data;

@Data
public class LoginRequest {

    private String email;
    private String password;
}
