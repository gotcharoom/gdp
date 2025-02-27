package com.gotcharoom.gdp.global.util;

import com.gotcharoom.gdp.auth.model.RefreshTokenRequest;
import com.gotcharoom.gdp.auth.repository.RefreshTokenRepository;
import com.gotcharoom.gdp.global.security.CustomUserDetailsService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
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
    private String secretKey;

    @Value("${jwt.token.access-expiration-time}")
    private Long accessExpirationTime;

    @Value("${jwt.token.refresh-expiration-time}")
    private Long refreshExpirationTime;

    private final RefreshTokenRepository refreshTokenRepository;
    private final CustomUserDetailsService customUserDetailsService;


    public JwtUtil(RefreshTokenRepository refreshTokenRepository, CustomUserDetailsService customUserDetailsService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.customUserDetailsService = customUserDetailsService;
    }

    @PostConstruct
    public void init() {
        this.convertSecretKey(secretKey);
    }

    private Key convertSecretKey(String secretKey) {
        if (secretKey == null || secretKey.isEmpty()) {
            throw new IllegalArgumentException("Secret key cannot be null or empty");
        }

        byte[] keyBytes = Decoders.BASE64.decode(secretKey); // Base64로 인코딩된 Secret Key 디코딩
        return Keys.hmacShaKeyFor(keyBytes); // Secret Key를 이용하여 Key 객체 생성
    }

    // AccessToken 생성
    public String createAccessToken(Authentication authentication){
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + accessExpirationTime);

        Key convertedSecretKey = convertSecretKey(secretKey);

        return Jwts.builder()
                .subject(authentication.getName())
                .issuedAt(now)
                .expiration(expireDate)
                .signWith(convertedSecretKey)
                .compact();
    }

    // Refresh Token 생성
    public String createRefreshToken(Authentication authentication){
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + refreshExpirationTime);

        Key convertedSecretKey = convertSecretKey(secretKey);

        String refreshToken = Jwts.builder()
                .subject(authentication.getName())
                .issuedAt(now)
                .expiration(expireDate)
                .signWith(convertedSecretKey)
                .compact();

        RefreshTokenRequest refreshTokenRequest = RefreshTokenRequest.builder()
                .authName(authentication.getName())
                .refreshToken(refreshToken)
                .ttl(refreshExpirationTime)
                .build();

        // redis에 저장
        refreshTokenRepository.save(refreshTokenRequest.toEntity());

        return refreshToken;
    }

    // Token > Claim > User > Authentication 객체 생성
    public Authentication getAuthentication(String token) {
        SecretKey convertedSecretKey = (SecretKey) convertSecretKey(secretKey);

        Claims userPrincipal = Jwts.parser()
                .verifyWith(convertedSecretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String userName = userPrincipal.getSubject();

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(userName);

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // Header에서 Token 추출
    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // TODO. [TR-YOO] Exception 처리 수정하기
    // Access Token 검증
    public boolean validateToken(String token){
        try{
            SecretKey convertedSecretKey = (SecretKey) convertSecretKey(secretKey);

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
}
