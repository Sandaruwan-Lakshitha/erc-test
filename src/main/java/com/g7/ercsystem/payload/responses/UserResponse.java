package com.g7.ercsystem.payload.responses;

import com.g7.ercsystem.rest.auth.model.MemberDetails;
import com.g7.ercsystem.rest.auth.model.Role;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Set;

@Data
@NoArgsConstructor
public class UserResponse {
    private String email;
    private MemberDetails memberDetails;
    private Set<String> roles;
    private Boolean isVerified;
    private Boolean isLocked;
    private Instant createdDate;
    private Instant modifiedDate;
}
