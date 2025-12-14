package com.nhn.config;

import java.io.IOException;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.nhn.service.user.UserDetailsServiceImpl;
import com.nhn.util.JwtUtil;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {
    private final UserDetailsServiceImpl userDetailsServiceImpl;

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(final HttpServletRequest request,
                                    final HttpServletResponse response,
                                    final FilterChain filterChain) throws ServletException, IOException {
        String       username = null;
        final String token    = this.getToken(request);
        if (StringUtils.isNoneBlank(token)) {
            try {
                username = this.jwtUtil.getUsername(token);
            } catch (final IllegalArgumentException e) {
                log.error("Unable to get JWT Token");
            } catch (final ExpiredJwtException e) {
                log.error("JWT Token has expired");
            }
        }
        if (StringUtils.isNoneBlank(username)
            && Objects.isNull(SecurityContextHolder.getContext()
                                                   .getAuthentication())) {
            final UserDetails userDetails = this.userDetailsServiceImpl.loadUserByUsername(username);
            if (Objects.nonNull(userDetails)
                && this.jwtUtil.isValidToken(token, userDetails)) {
                final UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails,
                                                                                                                                        null,
                                                                                                                                        userDetails.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext()
                                     .setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        filterChain.doFilter(request, response);
    }

    private String getToken(final HttpServletRequest httpServletRequest) {
        final String bearerToken = httpServletRequest.getHeader("Authorization");
        if (StringUtils.isNoneBlank(bearerToken)
            && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
