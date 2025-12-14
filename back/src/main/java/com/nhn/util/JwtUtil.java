package com.nhn.util;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.nhn.enums.TokenTypes;
import com.nhn.properties.JwtSetting;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtUtil {
    private final JwtSetting jwtSetting;

    public String getUsername(final String token) {
        return this.getClaim(token, Claims::getSubject);
    }

    public Date getExpiration(final String token) {
        return this.getClaim(token, Claims::getExpiration);
    }

    public <T> T getClaim(final String token,
                          final Function<Claims, T> claimsResolver) {
        final Claims claims = this.getAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaims(final String token) {
        final SecretKey secretKey = Keys.hmacShaKeyFor(this.jwtSetting.getSecretKey()
                                                                      .getBytes(StandardCharsets.UTF_8));
        return Jwts.parserBuilder().setSigningKey(secretKey).build()
                   .parseClaimsJws(token).getBody();
    }

    public boolean isTokenExpired(final String token) {
        final Date expiration = this.getExpiration(token);
        return expiration.before(new Date());
    }

    public boolean isValidToken(final String token,
                                final UserDetails userDetails) {
        final String username = this.getUsername(token);
        return StringUtils.isNotBlank(username)
               && username.equals(userDetails.getUsername())
               && !this.isTokenExpired(token);
    }

    public String generateToken(final String email,
                                final TokenTypes tokenType) {
        final SecretKey secretKey = Keys.hmacShaKeyFor(this.jwtSetting.getSecretKey()
                                                                      .getBytes(StandardCharsets.UTF_8));
        return Jwts.builder().setSubject(email)
                   .setIssuedAt(new Date(System.currentTimeMillis()))
                   .setExpiration(new Date(System.currentTimeMillis() +
                                           1000 *
                                                                        this.jwtSetting.getExpirationAccessToken()))
                   .claim("type", tokenType).claim("email", email)
                   .signWith(secretKey, SignatureAlgorithm.HS256).compact();
    }
}
