package com.findDream.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtProvider {

    private final Key key;
    private final long expiration;

    public JwtProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expiration) {

        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expiration = expiration;
    }

    // JWT 생성
    public String createToken(Long userId, String userLoginId) {
        return Jwts.builder()
                .setSubject(userLoginId)        // 서브젝트 (username)
                .claim("userId", userId)        // 우리가 직접 넣는 정보
                .setIssuedAt(new Date())        // 발급 시간
                .setExpiration(new Date(System.currentTimeMillis() + expiration)) // 만료 시간
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // userId 추출
    public Long getUserId(String token) {
        return parseClaims(token).get("userId", Long.class);
    }

    // 토큰 검증
    public boolean validate(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
