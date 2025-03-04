package com.gotcharoom.gdp.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gotcharoom.gdp.auth.entity.RefreshToken;
import com.gotcharoom.gdp.auth.model.*;
import com.gotcharoom.gdp.auth.repository.RefreshTokenRepository;
import com.gotcharoom.gdp.global.util.JwtUtil;
import com.gotcharoom.gdp.global.util.OAuth2Util;
import com.gotcharoom.gdp.user.entity.GdpUser;
import com.gotcharoom.gdp.user.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Slf4j
@Service
public class AuthService {

    @Value("${jwt.token.access-expiration-time}")
    private Long ACCESS_EXPIRATION_TIME;

    @Value("${jwt.token.refresh-expiration-time}")
    private Long REFRESH_EXPIRATION_TIME;

    private final AuthenticationManager authenticationManager;

    public final JwtUtil jwtUtil;

    public final OAuth2Util oAuth2Util;

    private final RefreshTokenRepository refreshTokenRepository;

    private final UserRepository userRepository;

    public AuthService(AuthenticationManager authenticationManager,
                       JwtUtil jwtUtil,
                       OAuth2Util oAuth2Util,
                       RefreshTokenRepository refreshTokenRepository,
                       UserRepository userRepository
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.oAuth2Util = oAuth2Util;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    // TODO. [TR-YOO] Password 인코딩 처리하기
    @Transactional
    public JwtToken generateJwtToken(LoginRequest loginRequest) {

        try {

            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                    loginRequest.getId(),
                    loginRequest.getPassword()
            );

            Authentication authentication = authenticationManager.authenticate(token);

            String accessToken = jwtUtil.createAccessToken(authentication);
            String refreshToken = jwtUtil.createRefreshToken(authentication);

            return new JwtToken(accessToken, refreshToken);

        } catch(BadCredentialsException  e){

            log.error(e.getMessage());
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    // TODO. [TR-YOO] Exception 변경하기
    @Transactional
    public void executeLogout(HttpServletRequest request) {

        try {
            String accessToken = jwtUtil.resolveAccessToken(request);

            if (accessToken == null || accessToken.isEmpty()) {
                log.info("토큰이 없는 요청입니다. 이미 로그아웃된 상태일 수 있습니다");
                return;
            }

            Authentication auth = jwtUtil.getAuthenticationFromExpiredToken(accessToken);

            // AccessToken Blacklist 처리
            jwtUtil.addToBlacklist(accessToken);

            // Refresh Token 삭제
            jwtUtil.removeRefreshToken(auth);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }



    public LoginUserInfoResponse getLoginUserDetail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("유저 정보를 가져올 수 없습니다");
        }

        GdpUser gdpUser = userRepository.findById(authentication.getName())
                .orElseThrow(() -> new RuntimeException("해당계정을 찾을 수 없습니다"));

        return LoginUserInfoResponse.fromEntity(gdpUser);
    }

    @Transactional
    public JwtToken replaceJwtToken(HttpServletRequest request) throws JsonProcessingException {

        String refreshTokenFromCookie = jwtUtil.resolveRefreshToken(request);
        Authentication auth = jwtUtil.getAuthenticationFromExpiredToken(refreshTokenFromCookie);

        // TODO. [TR-YOO] Exception 교체하기
        // Refresh Token 체크
        RefreshToken refreshToken = null;
        try {
            System.out.println(auth.getName());
            refreshToken = refreshTokenRepository.findByAuthName(auth.getName())
                    .orElseThrow();

            jwtUtil.validateToken(refreshToken.getRefreshToken());
        } catch(Exception e) {
            throw new RuntimeException("Refresh Token이 유효하지 않습니다");
        }

        // TODO. [TR-YOO] Exception 교체하기
        // Access Token 체크
        try {
            jwtUtil.validateToken(refreshTokenFromCookie);
        } catch (ExpiredJwtException e) {
            log.info("Access Token 유효기간 만료");
        } catch (Exception e) {
            throw new RuntimeException("Access Token이 유효하지 않습니다");
        }

        return JwtToken.builder()
                .refreshToken(refreshToken.getRefreshToken())
                .accessToken(jwtUtil.createAccessToken(auth))
                .build();
    }


    @Transactional
    public boolean checkAccessToken(HttpServletRequest request) {

        return jwtUtil.checkAccessToken(request);
    }

    public void setAllToken(JwtToken jwtToken, HttpServletResponse response) {
        jwtUtil.setAccessTokenCookie(jwtToken, response);
        jwtUtil.setRefreshTokenCookie(jwtToken, response);
    }

    public void setAccessToken(JwtToken jwtToken, HttpServletResponse response) {
        jwtUtil.setAccessTokenCookie(jwtToken, response);
    }

    public void removeAllToken(HttpServletResponse response) {

        jwtUtil.removeAccessTokenCookie(response);
        jwtUtil.removeRefreshTokenCookie(response);
    }
}
