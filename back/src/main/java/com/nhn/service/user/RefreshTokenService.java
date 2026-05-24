package com.nhn.service.user;

import java.util.Date;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhn.exception.FuncErrorException;
import com.nhn.model.entity.user.RefreshToken;
import com.nhn.repository.RefreshTokenRepository;
import com.nhn.util.ErrorMsgUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public RefreshToken createRefreshToken(final UUID userId, final String token, final Date expiresAt) {
        final RefreshToken refreshToken = RefreshToken.builder()
                                                      .userId(userId)
                                                      .token(token)
                                                      .expiresAt(expiresAt)
                                                      .build();
        return this.refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken validateRefreshToken(final String token) {
        final RefreshToken refreshToken = this.refreshTokenRepository.findByToken(token)
                                                                      .orElseThrow(() -> new FuncErrorException(ErrorMsgUtil.createRefreshTokenInvalidErrorResponse()));
        if (refreshToken.getExpiresAt().before(new Date())) {
            this.refreshTokenRepository.delete(refreshToken);
            throw new FuncErrorException(ErrorMsgUtil.createRefreshTokenInvalidErrorResponse());
        }
        return refreshToken;
    }

    @Transactional
    public void deleteByUserId(final UUID userId) {
        this.refreshTokenRepository.deleteByUserId(userId);
    }
}
