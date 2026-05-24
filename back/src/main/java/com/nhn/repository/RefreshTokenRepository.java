package com.nhn.repository;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhn.model.entity.user.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUserId(UUID userId);
    void deleteByExpiresAtBefore(Date date);
}
