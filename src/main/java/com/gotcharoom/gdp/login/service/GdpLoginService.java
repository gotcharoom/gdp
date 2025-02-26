package com.gotcharoom.gdp.login.service;

//import com.gotcharoom.gdp.global.util.JwtUtil;
import com.gotcharoom.gdp.global.util.JwtUtil;
import com.gotcharoom.gdp.login.model.GdpLoginRequest;
import com.gotcharoom.gdp.login.model.JwtToken;
import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Slf4j
@Service
public class GdpLoginService {

    private final AuthenticationManager authenticationManager;

    private final JwtUtil jwtUtil;

    public GdpLoginService(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
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
}
