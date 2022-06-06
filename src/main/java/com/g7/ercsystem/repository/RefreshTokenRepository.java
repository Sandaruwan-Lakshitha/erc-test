package com.g7.ercsystem.repository;

import com.g7.ercsystem.rest.auth.model.RefreshToken;
import com.g7.ercsystem.rest.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("RefreshTokenRepository")
public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {

    Optional<RefreshToken> findByToken(String token);

    @Modifying
    int deleteByUser(User user);
}
