package com.gotcharoom.gdp.global.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gotcharoom.gdp.auth.entity.BlacklistedToken;
import com.gotcharoom.gdp.auth.entity.RefreshToken;
import com.gotcharoom.gdp.auth.model.JwtToken;
import com.gotcharoom.gdp.auth.model.RefreshTokenRequest;
import com.gotcharoom.gdp.auth.model.CookieEnum;
import com.gotcharoom.gdp.auth.model.TokenLocationEnum;
import com.gotcharoom.gdp.auth.repository.BlacklistedTokenRepository;
import com.gotcharoom.gdp.auth.repository.RefreshTokenRepository;
import com.gotcharoom.gdp.global.security.service.CustomUserDetailsService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
@Slf4j
public class JwtUtil {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    @Value("${jwt.token.access-expiration-time}")
    private Long ACCESS_EXPIRATION_TIME;

    @Value("${jwt.token.refresh-expiration-time}")
    private Long REFRESH_EXPIRATION_TIME;

    @Value("${auth.token.location:COOKIE}")
    private TokenLocationEnum TOKEN_LOCATION;

    private final RefreshTokenRepository refreshTokenRepository;
    private final CustomUserDetailsService customUserDetailsService;
    private final BlacklistedTokenRepository blacklistedTokenRepository;
    private final OAuth2Util oAuth2Util;


    public JwtUtil(
            RefreshTokenRepository refreshTokenRepository,
            CustomUserDetailsService customUserDetailsService,
            BlacklistedTokenRepository blacklistedTokenRepository,
            OAuth2Util oAuth2Util
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.customUserDetailsService = customUserDetailsService;
        this.blacklistedTokenRepository = blacklistedTokenRepository;
        this.oAuth2Util = oAuth2Util;
    }

    @PostConstruct
    public void init() {
        this.convertSecretKey(SECRET_KEY);
    }

    private Key convertSecretKey(String secretKey) {
        if (secretKey == null || secretKey.isEmpty()) {
            throw new IllegalArgumentException("Secret key cannot be null or empty");
        }

        byte[] keyBytes = Decoders.BASE64.decode(secretKey); // Base64로 인코딩된 Secret Key 디코딩
        return Keys.hmacShaKeyFor(keyBytes); // Secret Key를 이용하여 Key 객체 생성
    }

    // AccessToken 생성
    public String createAccessToken(Authentication authentication) throws JsonProcessingException {
        String id = oAuth2Util.getIdFromAuthentication(authentication);
        return createToken(id, ACCESS_EXPIRATION_TIME);
    }

    // Refresh Token 생성
    public String createRefreshToken(Authentication authentication) throws JsonProcessingException {
        String id = oAuth2Util.getIdFromAuthentication(authentication);
        String refreshToken = createToken(id, REFRESH_EXPIRATION_TIME);

        RefreshTokenRequest refreshTokenRequest = RefreshTokenRequest.builder()
                .authName(id)
                .refreshToken(refreshToken)
                .ttl(REFRESH_EXPIRATION_TIME)
                .build();

        // redis에 저장
        refreshTokenRepository.save(refreshTokenRequest.toEntity());

        return refreshToken;
    }

    private String createToken(String id, Long expireTime) throws JsonProcessingException {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expireTime);

        Key convertedSecretKey = convertSecretKey(SECRET_KEY);

        return Jwts.builder()
                .subject(id)
                .issuedAt(now)
                .expiration(expireDate)
                .signWith(convertedSecretKey)
                .compact();
    }

    // Token > Claim > User > Authentication 객체 생성
    public Authentication getAuthentication(String token) {
        SecretKey convertedSecretKey = (SecretKey) convertSecretKey(SECRET_KEY);

        Claims userPrincipal = Jwts.parser()
                .verifyWith(convertedSecretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String subject = userPrincipal.getSubject();

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(subject);

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public Authentication getAuthenticationFromExpiredToken(String token) {
        SecretKey convertedSecretKey = (SecretKey) convertSecretKey(SECRET_KEY);
        Claims userPrincipal;

        try {
            userPrincipal = Jwts.parser()
                    .verifyWith(convertedSecretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

        } catch(ExpiredJwtException e) {
            log.info("Access Token이 만료되었습니다. Claims를 가져옵니다.");
            userPrincipal = e.getClaims();
        } catch (JwtException e) {
            throw new RuntimeException("유효하지 않은 JWT 토큰입니다.");
        }


        String subject = userPrincipal.getSubject();
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(subject);

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public boolean resolveRememberMe(HttpServletRequest req, HttpServletResponse rep) {

        if (req.getCookies() != null) {
            for (Cookie cookie : req.getCookies()) {
                String rememberMeStr = CookieEnum.REMEMBER_ME.getType();
                if (rememberMeStr.equals(cookie.getName())) {
                    String cookieValue = cookie.getValue();
                    removeRememberMeToken(rep);
                    return Boolean.parseBoolean(cookieValue);
                }
            }
        }
        return false;
    }

    public String resolveAccessToken(HttpServletRequest req) {

        return switch(TOKEN_LOCATION) {
            case HEADER -> resolveFromHeader(req);
            case COOKIE -> resolveAccessTokenFromCookie(req);
        };
    }

    public String resolveRefreshToken(HttpServletRequest req) {
        return switch(TOKEN_LOCATION) {
            case HEADER -> resolveFromHeader(req);
            case COOKIE -> resolveRefreshTokenFromCookie(req);
        };
    }


    // Header에서 Token 추출
    public String resolveFromHeader(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // Cookie에서 Token 추출
    public String resolveAccessTokenFromCookie(HttpServletRequest req) {

        if (req.getCookies() != null) {
            for (Cookie cookie : req.getCookies()) {
                String accessTokenStr = CookieEnum.ACCESS_TOKEN.getType();
                if (accessTokenStr.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }


    // Cookie에서 Token 추출
    public String resolveRefreshTokenFromCookie(HttpServletRequest req) {
        if (req.getCookies() != null) {
            for (Cookie cookie : req.getCookies()) {
                String refreshTokenStr = CookieEnum.REFRESH_TOKEN.getType();
                if (refreshTokenStr.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    // TODO. [TR-YOO] Exception 처리 수정하기
    // Access Token 검증
    public boolean validateToken(String token){
        try{
            SecretKey convertedSecretKey = (SecretKey) convertSecretKey(SECRET_KEY);

            Jwts.parser()
                    .verifyWith(convertedSecretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return true;
        } catch(ExpiredJwtException e) {
            log.error(e.getMessage());
            throw e;
        } catch(Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void removeRefreshToken(Authentication auth) {
        RefreshToken refreshToken = refreshTokenRepository.findByAuthName(auth.getName())
                .orElseThrow();

        refreshTokenRepository.delete(refreshToken);
    }

    public void addToBlacklist(String token) {
        if (token == null) { throw new RuntimeException(); }

        Long expirationTime = System.currentTimeMillis() + ACCESS_EXPIRATION_TIME;
        BlacklistedToken blacklistedToken = new BlacklistedToken(token, expirationTime);
        blacklistedTokenRepository.save(blacklistedToken);
    }

    public boolean isBlacklisted(String token) {

        return token != null && blacklistedTokenRepository.existsByToken(token);
    }

    public void setAccessTokenCookie(JwtToken jwtToken, HttpServletResponse response, boolean isRememberMe) {
        int maxAge = isRememberMe ? (int) (ACCESS_EXPIRATION_TIME / 1000) : -1;

        String accessTokenStr = CookieEnum.ACCESS_TOKEN.getType();
        Cookie cookie = new Cookie(accessTokenStr, jwtToken.getAccessToken());
        cookie.setHttpOnly(true); // CSRF 방지
        cookie.setPath("/"); // Cookie가 유효한 경로
        cookie.setMaxAge(maxAge); // -1 : 브라우저가 닫히면 Cookie 삭제
        response.addCookie(cookie);
    }

    public void setRefreshTokenCookie(JwtToken jwtToken, HttpServletResponse response, boolean isRememberMe) {
        int maxAge = isRememberMe ? (int) (ACCESS_EXPIRATION_TIME / 1000) : -1;

        String refreshTokenStr = CookieEnum.REFRESH_TOKEN.getType();
        Cookie cookie = new Cookie(refreshTokenStr, jwtToken.getAccessToken());
        cookie.setHttpOnly(true); // CSRF 방지
        cookie.setPath("/"); // Cookie가 유효한 경로
        cookie.setMaxAge(maxAge); // -1 : 브라우저가 닫히면 Cookie 삭제
        response.addCookie(cookie);
    }

    public void removeAccessTokenCookie(HttpServletResponse response) {
        int convertedTime = (int) (ACCESS_EXPIRATION_TIME / 1000);

        String accessTokenStr = CookieEnum.ACCESS_TOKEN.getType();
        Cookie cookie = new Cookie(accessTokenStr, null);
        cookie.setHttpOnly(true); // CSRF 방지
        cookie.setPath("/"); // Cookie가 유효한 경로
        cookie.setMaxAge(-1); // 브라우저가 닫히면 Cookie 삭제
        cookie.setMaxAge(convertedTime);
        response.addCookie(cookie);
    }

    public void removeRefreshTokenCookie(HttpServletResponse response) {
        int convertedTime = (int) (REFRESH_EXPIRATION_TIME / 1000);

        String refreshTokenStr = CookieEnum.REFRESH_TOKEN.getType();
        Cookie cookie = new Cookie(refreshTokenStr, null);
        cookie.setHttpOnly(true); // CSRF 방지
        cookie.setPath("/"); // Cookie가 유효한 경로
        cookie.setMaxAge(-1); // 브라우저가 닫히면 Cookie 삭제
        cookie.setMaxAge(convertedTime);
        response.addCookie(cookie);
    }

    public boolean checkAccessToken(HttpServletRequest request) {
        try {
            String token = resolveAccessToken(request);
            validateToken(token);

            return true;
        } catch (Exception e) {

            return false;
        }
    }

    public void setRememberMeToken(HttpServletResponse response, boolean isRememberMe) {
        String rememberMeStr = CookieEnum.REMEMBER_ME.getType();
        Cookie cookie = new Cookie(rememberMeStr, String.valueOf(isRememberMe));
        cookie.setHttpOnly(true); // CSRF 방지
        cookie.setPath("/");
        cookie.setMaxAge(-1);
        response.addCookie(cookie);
    }

    public void removeRememberMeToken( HttpServletResponse response) {
        String rememberMeStr = CookieEnum.REMEMBER_ME.getType();
        Cookie cookie = new Cookie(rememberMeStr, null);
        cookie.setHttpOnly(true); // CSRF 방지
        cookie.setPath("/");
        cookie.setMaxAge(-1);
        response.addCookie(cookie);
    }

    public void setPlatformConnectionToken(HttpServletResponse response) {
        String platformConnectionStr = CookieEnum.PLATFORM_CONNECTION.getType();
        Cookie cookie = new Cookie(platformConnectionStr, "true");
        cookie.setHttpOnly(true); // CSRF 방지
        cookie.setPath("/");
        cookie.setMaxAge(-1);
        response.addCookie(cookie);
    }

    public void removePlatformConnectionToken(HttpServletResponse response) {
        String platformConnectionStr = CookieEnum.PLATFORM_CONNECTION.getType();
        Cookie cookie = new Cookie(platformConnectionStr, null);
        cookie.setHttpOnly(true); // CSRF 방지
        cookie.setPath("/");
        cookie.setMaxAge(-1);
        response.addCookie(cookie);
    }
}
