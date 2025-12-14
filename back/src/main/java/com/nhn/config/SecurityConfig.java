package com.nhn.config;

import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import com.nhn.service.user.UserDetailsServiceImpl;

import lombok.RequiredArgsConstructor;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;

    private final JwtAuthFilter jwtAuthFilter;

    private final UserDetailsServiceImpl userDetailsServiceImpl;

    /** 公開URL。 */
    private static final String[] publicUrls = {"/v3/api-docs/**",
                                                "/swagger-ui/**",
                                                "/swagger-ui.html",
                                                "/api/v1/users/verify",
                                                "/api/v1/users/resend-email-verification/**",
                                                "/api/v1/users/forgot-password",
                                                "/api/v1/users/reset-password",
                                                "/api/v1/auth/login",
                                                "/api/v1/auth/register"};

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(final AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        final DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(this.userDetailsServiceImpl);
        authProvider.setPasswordEncoder(this.passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
        final CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(List.of("*"));
        corsConfig.addAllowedMethod("*");
        corsConfig.addAllowedHeader("*");
        corsConfig.setExposedHeaders(Collections.singletonList("Content-Disposition"));
        corsConfig.setMaxAge(3600L);
        http.cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer.configurationSource(request -> corsConfig));

        http.csrf(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(authorize -> authorize.requestMatchers(publicUrls)
                                                         .permitAll()
                                                         .requestMatchers(HttpMethod.GET,
                                                                          "/api/v1/book-categories/**")

                                                         .permitAll()
                                                         .requestMatchers(HttpMethod.GET,
                                                                          "/api/v1/book-publishers/**")

                                                         .permitAll()
                                                         .requestMatchers(HttpMethod.GET,
                                                                          "/api/v1/book-authors/**")

                                                         .permitAll()
                                                         .requestMatchers(HttpMethod.GET,
                                                                          "/api/v1/books/**")

                                                         .permitAll()
                                                         .anyRequest()
                                                         .authenticated())
            .exceptionHandling(exceptionHandling -> exceptionHandling.authenticationEntryPoint(this.authenticationEntryPoint))
            .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(this.jwtAuthFilter,
                             UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
