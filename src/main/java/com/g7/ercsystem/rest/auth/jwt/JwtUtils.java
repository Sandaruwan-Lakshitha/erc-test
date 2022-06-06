package com.g7.ercsystem.rest.auth.jwt;

import com.g7.ercsystem.interfaces.UserService;
import com.g7.ercsystem.repository.UserRepository;
import com.g7.ercsystem.rest.auth.model.EnumRole;
import com.g7.ercsystem.rest.auth.model.Role;
import com.g7.ercsystem.rest.auth.model.User;
import com.g7.ercsystem.rest.auth.service.UserDetailsImpl;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Set;

@Component
@Slf4j
public class JwtUtils {

    @Autowired
    private UserService userService;
    @Value("${jwtSecret}")
    private String jwtSecret;
    private HttpServletRequest request;

    @Value("${jwtExpirationMs}")
    private int jwtExpirationMs;

    public JwtUtils(UserService userService, HttpServletRequest request) {
        this.userService = userService;
        this.request = request;
    }

    public String generateTokenFromUsername(String id){
        return Jwts.builder().setSubject(id).setIssuedAt(new Date())
                .setExpiration(new Date((new Date().getTime()+jwtExpirationMs)))
                .signWith(SignatureAlgorithm.HS512,jwtSecret)
                .compact();
    }


    public String generateJwtToken(UserDetailsImpl userPrincipal){
        return generateTokenFromUsername(userPrincipal.getId());
    }

    public String getUserIdFromJwtToken(String token){
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    private String parseJwt(HttpServletRequest request){
        String headerAuth = request.getHeader("Authorization");

        if(StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")){
            return headerAuth.substring(7);
        }

        return null;
    }

    public String getUserIdFromRequest(){
        return getUserIdFromJwtToken(parseJwt(request));
    }

    public boolean validateJwtToken(String authToken){
        try{
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        }catch (SignatureException e){
            log.error("Invalid JWT signature: {}",e.getMessage());
        }catch (MalformedJwtException e){
            log.error("Invalid JWT token: {}",e.getMessage());
        }catch (ExpiredJwtException e){
            log.error("JWT token is expired: {}",e.getMessage());
        }catch (UnsupportedJwtException e){
            log.error("JWT token is unsupported: {}",e.getMessage());
        }catch(IllegalArgumentException e){
            log.error("JWT claims string is empty: {}",e.getMessage());
        }

        return false;
    }

}
