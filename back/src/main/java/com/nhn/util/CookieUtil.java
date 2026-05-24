package com.nhn.util;

import java.util.Arrays;

import org.springframework.http.ResponseCookie;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CookieUtil {
    private static final String REFRESH_TOKEN_COOKIE = "refreshToken";
    private static final String REFRESH_TOKEN_PATH   = "/api/v1/auth";

    public static ResponseCookie createRefreshTokenCookie(final String token,
                                                          final long maxAgeSeconds,
                                                          final boolean secure) {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE, token)
                             .httpOnly(true)
                             .secure(secure)
                             .path(REFRESH_TOKEN_PATH)
                             .sameSite("Lax")
                             .maxAge(maxAgeSeconds)
                             .build();
    }

    public static ResponseCookie clearRefreshTokenCookie(final boolean secure) {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE, "")
                             .httpOnly(true)
                             .secure(secure)
                             .path(REFRESH_TOKEN_PATH)
                             .sameSite("Lax")
                             .maxAge(0)
                             .build();
    }

    public static String extractRefreshToken(final HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }
        return Arrays.stream(request.getCookies())
                     .filter(c -> REFRESH_TOKEN_COOKIE.equals(c.getName()))
                     .map(Cookie::getValue)
                     .findFirst()
                     .orElse(null);
    }
}
