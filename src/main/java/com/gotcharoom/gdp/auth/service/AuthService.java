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

    @Transactional
    public JwtToken generateJwtToken(LoginRequest loginRequest) {
        try {
            log.info("JWT 토큰 생성 요청 - 사용자 ID: {}", loginRequest.getId());

            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                    loginRequest.getId(),
                    loginRequest.getPassword()
            );

            Authentication authentication = authenticationManager.authenticate(token);
            log.info("사용자 인증 성공 - ID: {}", authentication.getName());

            String accessToken = jwtUtil.createAccessToken(authentication);
            String refreshToken = jwtUtil.createRefreshToken(authentication);

            log.info("AccessToken 및 RefreshToken 생성 완료");
            return new JwtToken(accessToken, refreshToken);

        } catch (BadCredentialsException e) {
            log.error("로그인 실패 - 잘못된 자격 증명: {}", e.getMessage());
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            log.error("JWT 생성 중 JSON 처리 오류: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void executeLogout(HttpServletRequest request) {
        try {
            String accessToken = jwtUtil.resolveAccessToken(request);
            if (accessToken == null || accessToken.isEmpty()) {
                log.info("AccessToken이 존재하지 않음 - 이미 로그아웃된 상태일 수 있음");
                return;
            }

            Authentication auth = jwtUtil.getAuthenticationFromExpiredToken(accessToken);
            log.info("로그아웃 처리 시작 - 사용자: {}", auth.getName());

            jwtUtil.addToBlacklist(accessToken);
            log.info("AccessToken 블랙리스트 등록 완료");

            jwtUtil.removeRefreshToken(auth);
            log.info("RefreshToken 삭제 완료");

        } catch (Exception e) {
            log.error("로그아웃 처리 중 예외 발생: {}", e.getMessage(), e);
            throw new RuntimeException();
        }
    }

    public LoginUserInfoResponse getLoginUserDetail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("인증 정보가 존재하지 않음 - 유저 정보 조회 실패");
            throw new RuntimeException("유저 정보를 가져올 수 없습니다");
        }

        log.info("로그인 유저 정보 조회 요청 - 사용자: {}", authentication.getName());

        GdpUser gdpUser = userRepository.findById(authentication.getName())
                .orElseThrow(() -> {
                    log.error("해당 사용자 정보를 찾을 수 없음 - ID: {}", authentication.getName());
                    return new RuntimeException("해당계정을 찾을 수 없습니다");
                });

        return LoginUserInfoResponse.fromEntity(gdpUser);
    }

    @Transactional
    public JwtToken replaceJwtToken(HttpServletRequest request) throws JsonProcessingException {
        String refreshTokenFromCookie = jwtUtil.resolveRefreshToken(request);
        log.info("RefreshToken을 이용한 JWT 재발급 요청 수신");

        Authentication auth = jwtUtil.getAuthenticationFromExpiredToken(refreshTokenFromCookie);
        log.info("인증 정보 복원 완료 - 사용자: {}", auth.getName());

        try {
            RefreshToken refreshToken = refreshTokenRepository.findByAuthName(auth.getName())
                    .orElseThrow(() -> {
                        log.error("RefreshToken DB 조회 실패 - 사용자: {}", auth.getName());
                        return new RuntimeException("Refresh Token이 존재하지 않습니다");
                    });

            jwtUtil.validateToken(refreshToken.getRefreshToken());
            log.info("RefreshToken 유효성 검증 완료");

        } catch (Exception e) {
            log.error("RefreshToken 유효성 검증 실패: {}", e.getMessage(), e);
            throw new RuntimeException("Refresh Token이 유효하지 않습니다");
        }

        try {
            jwtUtil.validateToken(refreshTokenFromCookie);
        } catch (ExpiredJwtException e) {
            log.info("Access Token 만료 상태");
        } catch (Exception e) {
            log.error("Access Token 유효성 검증 실패: {}", e.getMessage(), e);
            throw new RuntimeException("Access Token이 유효하지 않습니다");
        }

        String newAccessToken = jwtUtil.createAccessToken(auth);
        log.info("새로운 AccessToken 생성 완료");

        return JwtToken.builder()
                .refreshToken(refreshTokenFromCookie)
                .accessToken(newAccessToken)
                .build();
    }

    @Transactional
    public boolean checkAccessToken(HttpServletRequest request) {
        log.info("AccessToken 유효성 확인 요청");
        return jwtUtil.checkAccessToken(request);
    }

    public void setAllToken(JwtToken jwtToken, HttpServletRequest request, HttpServletResponse response) {
        boolean isRememberMe = jwtUtil.resolveRememberMe(request, response);
        log.info("AccessToken 및 RefreshToken 쿠키 설정 - RememberMe: {}", isRememberMe);

        jwtUtil.setAccessTokenCookie(jwtToken, response, isRememberMe);
        jwtUtil.setRefreshTokenCookie(jwtToken, response, isRememberMe);

        log.info("모든 토큰 쿠키 설정 완료");
    }

    public void setAccessToken(JwtToken jwtToken, HttpServletRequest request, HttpServletResponse response) {
        boolean isRememberMe = jwtUtil.resolveRememberMe(request, response);
        log.info("AccessToken 쿠키 설정 - RememberMe: {}", isRememberMe);

        jwtUtil.setAccessTokenCookie(jwtToken, response, isRememberMe);
        log.info("AccessToken 쿠키 설정 완료");
    }

    public void removeAllToken(HttpServletResponse response) {
        log.info("토큰 쿠키 삭제 요청 수신");
        jwtUtil.removeAccessTokenCookie(response);
        jwtUtil.removeRefreshTokenCookie(response);
        log.info("모든 토큰 쿠키 삭제 완료");
    }

    public void setRememberMeCookie(RememberMeRequest request, HttpServletResponse response) {
        boolean isRememberMe = request.isRememberMe();
        log.info("RememberMe 쿠키 설정 요청 - 상태: {}", isRememberMe);
        jwtUtil.setRememberMeToken(response, isRememberMe);
    }
}
