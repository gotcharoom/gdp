package com.gotcharoom.gdp.global.config;


import com.gotcharoom.gdp.global.security.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Value("${gdp.application.front-uri}")
    private String GDP_FRONT_URI;

    @Value("${gdp.custom.oauth2-auth-uri}")
    private String OAUTH2_AUTH_URI;

    @Value("${gdp.custom.oauth2-redirect-uri}")
    private String OAUTH2_REDIRECT_URI;

    private final JwtFilter jwtFilter;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    private final CustomOauth2UserService customOauth2UserService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;

    public SecurityConfig(
            JwtFilter jwtFilter,
            JwtAccessDeniedHandler jwtAccessDeniedHandler,
            JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
            CustomOauth2UserService customOauth2UserService,
            OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler,
            OAuth2LoginFailureHandler oAuth2LoginFailureHandler
    ) {
        this.jwtFilter = jwtFilter;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.customOauth2UserService = customOauth2UserService;
        this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
        this.oAuth2LoginFailureHandler = oAuth2LoginFailureHandler;
    }

    @Bean
    BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {

        return authenticationConfiguration.getAuthenticationManager();
    }

    // TODO. [TR-YOO] Jwt Exception Handling 부분 수정하기
    // TODO. [TR-YOO] OAUTH2 Success / Failure URL 수정하기
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                // REST API이므로 basic auth 및 csrf 보안을 사용하지 않음
                .httpBasic(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                // JWT를 사용하기 때문에 세션을 사용하지 않음
                .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(AbstractHttpConfigurer::disable)
                .cors(customizer -> {
                    customizer.configurationSource(request -> {
                        CorsConfiguration config = new CorsConfiguration();
                        config.setAllowedOrigins(List.of(GDP_FRONT_URI));
                        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
                        config.setAllowCredentials(true);
                        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With"));

                        return config;
                    });
                })
                .authorizeHttpRequests(authRequest ->
                        authRequest
//                                .requestMatchers(
//                                        "/swagger",
//                                        "/swagger-ui.html",
//                                        "/swagger-ui/**",
//                                        "/api-docs",
//                                        "/api-docs/**",
//                                        "/v3/api-docs/**"
//                                )
//                                   .permitAll()
//                                .requestMatchers(
//                                        "/api/v1/auth/login",
//                                        "/api/v1/auth/logout",
//                                        "/api/v1/auth/refresh",
//                                        "/api/v1/auth/check",
//                                        "/api/v1/auth/remember-me",
//                                        OAUTH2_AUTH_URI+"/**",
//                                        OAUTH2_REDIRECT_URI+"/**",
//                                        "/api/v1/user/sign-up"
//                                )
//                                    .permitAll()
//                                .requestMatchers(
//                                        "/notice",
//                                        "/notice/**"
//                                )
//                                    .permitAll()
////                                .requestMatchers("/api/v1/user/**")
////                                  .hasRole()
//                                .anyRequest()
//                                    .authenticated()
                                .anyRequest().permitAll()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(configurer -> {
                    configurer
                            .accessDeniedHandler(jwtAccessDeniedHandler)
                            .authenticationEntryPoint(jwtAuthenticationEntryPoint);
                })
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(endpoint ->
                            endpoint.baseUri(OAUTH2_AUTH_URI)
                        )
                        .redirectionEndpoint(endpoint ->
                            endpoint.baseUri(OAUTH2_REDIRECT_URI+"/*")
                        )
                        .userInfoEndpoint(config -> {
                            config.userService(customOauth2UserService);
                        })
                        .successHandler(oAuth2LoginSuccessHandler)
                        .failureHandler(oAuth2LoginFailureHandler)
                )
                .build();
    }
}
