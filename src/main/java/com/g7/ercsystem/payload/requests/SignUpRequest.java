package com.g7.ercsystem.payload.requests;

import com.g7.ercsystem.rest.auth.model.MemberDetails;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
public class SignUpRequest {

    private String email;
    private String password;
    private MemberDetails memberDetails;
    private Set<String> roles;
}
