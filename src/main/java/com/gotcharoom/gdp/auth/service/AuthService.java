package com.gotcharoom.gdp.auth.service;

import com.gotcharoom.gdp.auth.entity.RefreshToken;
import com.gotcharoom.gdp.auth.model.AccessTokenEnum;
import com.gotcharoom.gdp.auth.repository.RefreshTokenRepository;
import com.gotcharoom.gdp.global.util.JwtUtil;
import com.gotcharoom.gdp.auth.model.GdpLoginRequest;
import com.gotcharoom.gdp.auth.model.JwtToken;
import com.gotcharoom.gdp.auth.model.LoginUserInfoResponse;
import com.gotcharoom.gdp.user.entity.GdpUser;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
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

    private final AuthenticationManager authenticationManager;

    private final JwtUtil jwtUtil;

    private final RefreshTokenRepository refreshTokenRepository;

    public AuthService(AuthenticationManager authenticationManager, JwtUtil jwtUtil, RefreshTokenRepository refreshTokenRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    // TODO. [TR-YOO] Password 인코딩 처리하기
    @Transactional
    public JwtToken generateJwtToken(GdpLoginRequest gdpLoginRequest) {

        try {

            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                    gdpLoginRequest.getId(),
                    gdpLoginRequest.getPassword()
            );

            Authentication authentication = authenticationManager.authenticate(token);

            String accessToken = jwtUtil.createAccessToken(authentication);
            String refreshToken = jwtUtil.createRefreshToken(authentication);

            return new JwtToken(accessToken, refreshToken);

        } catch(BadCredentialsException  e){

            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public LoginUserInfoResponse getLoginUserDetail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof GdpUser gdpUser)) {
            throw new RuntimeException("유저 정보를 가져올 수 없습니다");
        }

        return LoginUserInfoResponse.fromEntity(gdpUser);
    }

    // TODO. [TR-YOO] Refresh Token 만료 시 로그아웃처리하는 로직 추가 필요
    @Transactional
    public JwtToken replaceJwtToken(HttpServletRequest request) {

        String accessToken = jwtUtil.resolveToken(request);
        Authentication auth = jwtUtil.getAuthentication(accessToken);

        // TODO. [TR-YOO] Exception 교체하기
        // Refresh Token 체크
        RefreshToken refreshToken = null;
        try {
            refreshToken = refreshTokenRepository.findByAuthName(auth.getName())
                    .orElseThrow();

            jwtUtil.validateToken(refreshToken.getRefreshToken());
        } catch(Exception e) {
            throw new RuntimeException("Refresh Token이 유효하지 않습니다");
        }

        // TODO. [TR-YOO] Exception 교체하기
        // Access Token 체크
        try {
            jwtUtil.validateToken(accessToken);
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

    public void setAccessTokenCookie(JwtToken jwtToken, HttpServletResponse response) {

        String accessTokenStr = AccessTokenEnum.AccessToken.getType();
        Cookie cookie = new Cookie(accessTokenStr, jwtToken.getAccessToken());
        cookie.setHttpOnly(true); // CSRF 방지
        cookie.setPath("/"); // Cookie가 유효한 경로
        cookie.setMaxAge(-1); // 브라우저가 닫히면 Cookie 삭제
        response.addCookie(cookie);

        // cookie 방식 사용 시 Token 노출 지우기 위함
        jwtToken = null;
    }
}
