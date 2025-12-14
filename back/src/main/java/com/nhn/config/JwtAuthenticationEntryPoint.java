package com.nhn.config;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(final HttpServletRequest request, final HttpServletResponse response,
                         final AuthenticationException authException) throws IOException, ServletException {
		response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
	}
}
