package com.g7.ercsystem.rest.auth.service;

import com.g7.ercsystem.exception.TokenRefreshException;
import com.g7.ercsystem.repository.RefreshTokenRepository;
import com.g7.ercsystem.repository.UserRepository;
import com.g7.ercsystem.rest.auth.model.RefreshToken;
import com.g7.ercsystem.rest.auth.model.User;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Value("${jwtRefreshExpirationMs}")
    private Long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    public Optional<RefreshToken> findByToken(String token){
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken  createRefreshToken(String userId,String jwt){

        String [] token={jwt.substring(0,99), String.valueOf(Instant.now().hashCode()),jwt.substring(100,228)};
        RefreshToken refreshToken =new RefreshToken();
        if(userRepository.findById(userId).isPresent()){
            refreshToken.setUser(userRepository.findById(userId).get());
            refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
            refreshToken.setToken(token[0]+token[1]+token[2]);
        }
        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyExpiration(RefreshToken token){
        if(token.getExpiryDate().compareTo(Instant.now())<0){
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(),"Refresh token was expired. Please make a new signing request");
        }
        return token;
    }

    @Transactional
    public long deleteByUserId(String userId){
        User user = new User();
        if(userRepository.findById(userId).isPresent()){
            user =userRepository.findById(userId).get();
        }
        return refreshTokenRepository.deleteByUser(user);
    }
}
