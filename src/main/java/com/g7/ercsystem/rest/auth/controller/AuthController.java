package com.g7.ercsystem.rest.auth.controller;

import com.g7.ercsystem.assembler.AuthAssembler;
import com.g7.ercsystem.exception.TokenRefreshException;
import com.g7.ercsystem.payload.requests.LoginRequest;
import com.g7.ercsystem.payload.requests.SignUpRequest;
import com.g7.ercsystem.payload.responses.JwtResponse;
import com.g7.ercsystem.payload.responses.MessageResponse;
import com.g7.ercsystem.payload.responses.TokenRefreshResponse;
import com.g7.ercsystem.payload.responses.UserResponse;
import com.g7.ercsystem.repository.UserRepository;
import com.g7.ercsystem.rest.auth.jwt.JwtUtils;
import com.g7.ercsystem.rest.auth.model.RefreshToken;
import com.g7.ercsystem.rest.auth.model.User;
import com.g7.ercsystem.rest.auth.service.RefreshTokenService;
import com.g7.ercsystem.rest.auth.service.UserDetailsImpl;
import com.g7.ercsystem.rest.auth.service.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*",maxAge = 3600)
@Slf4j
public class AuthController {

    private final UserRepository userRepository;
    private final UserServiceImpl userService;
    private final AuthAssembler assembler;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;

    public AuthController(UserRepository userRepository, UserServiceImpl userService, AuthAssembler assembler, AuthenticationManager authenticationManager, JwtUtils jwtUtils, RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.assembler = assembler;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.refreshTokenService = refreshTokenService;
    }

    @GetMapping(value = "/test")
    @PreAuthorize("hasRole('ADMIN')")
    public String test(){
        return jwtUtils.getUserIdFromRequest();
    }

    @PostMapping(value = "/login")
    public ResponseEntity<?> SignIn(@RequestBody LoginRequest request){
        try{

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            String jwt = jwtUtils.generateJwtToken(userDetails);
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId(),jwt);
            System.out.println(userDetails.getId());

            JwtResponse jwtResponse =  new JwtResponse(
                    jwt,
                    refreshToken.getToken(),
                    roles
            );
            log.info("authenticate user successfully,username is {}",userDetails.getUsername());
            return new ResponseEntity<>(jwtResponse, HttpStatus.OK);

        }catch (Exception e){
                e.printStackTrace();
                throw  e;
        }
    }

    @PostMapping(value = "/signup")
    public ResponseEntity<?> SignUp(@RequestBody SignUpRequest request){

        try {
            if(userRepository.existsByEmail(request.getEmail())){
                log.error("Error : Username is already taken ! Username is {}",request.getEmail());
                return ResponseEntity.badRequest().body(
                        new MessageResponse("Error : Username is already taken !")
                );
            }

            if(request.getRoles() == null){
                log.error("Error : Role is not found from request body");
                return ResponseEntity.badRequest().body(
                        new MessageResponse("Error : Role is not found from request body!")
                );
            }

            User savedUser = userService.addUser(assembler.SignUpRequestToUserEntity(request));

            return new ResponseEntity<>("UserId : "+savedUser.getId(), HttpStatus.CREATED);

        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        Cookie name = WebUtils.getCookie(request, "refresh");
        String requestRefreshToken = name != null ? name.getValue() : null;//request.getToken();
        System.out.println(requestRefreshToken);
        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtUtils.generateTokenFromUsername(user.getId());
                    log.info("Successfully return new access token from refreshtoken");
                    return ResponseEntity.ok(new TokenRefreshResponse(token));
                })
                .orElseThrow(() ->{
                    log.info("Refresh token is not in database ! token is {}",requestRefreshToken);
                    return new TokenRefreshException(requestRefreshToken,"Refresh token is not in database!");
                });
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<?> profile(@PathVariable String id){
        System.out.println("result "+jwtUtils.getUserIdFromRequest().equalsIgnoreCase(id));
        try {
            if(!userService.existById(id)){
                log.error("Error : Username is not exist for u_id {}",id);
                return new ResponseEntity<>(new MessageResponse("Error : Username not found !"),HttpStatus.NOT_FOUND);
            }
            UserResponse user = assembler.UserEntityToUserResponse(userService.getUserById(id, jwtUtils.getUserIdFromRequest()));
            return new ResponseEntity<>(user,HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }

    }
}
