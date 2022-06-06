package com.g7.ercsystem.assembler;

import com.g7.ercsystem.payload.requests.SignUpRequest;
import com.g7.ercsystem.payload.responses.UserResponse;
import com.g7.ercsystem.rest.auth.model.User;
import com.g7.ercsystem.rest.auth.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class AuthAssembler {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserServiceImpl userService;

    public User SignUpRequestToUserEntity(SignUpRequest request){
        User user =  new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setMemberDetails(request.getMemberDetails());
        user.setRoles(userService.getRoles(request.getRoles(), request.getEmail()));
        user.setCreatedDate(Instant.now());
        user.setModifiedDate(Instant.now());
        user.setIsVerified(true);
        user.setIsLocked(true);
        return user;
    }

    public UserResponse UserEntityToUserResponse(User user){
        UserResponse response = new UserResponse();

        response.setEmail(user.getEmail());
        response.setRoles(userService.setRoles(user.getRoles()));
        response.setMemberDetails(user.getMemberDetails());
        response.setIsLocked(user.getIsLocked());
        response.setIsVerified(user.getIsVerified());
        response.setCreatedDate(user.getCreatedDate());
        response.setModifiedDate(user.getModifiedDate());

        return response;
    }

}
