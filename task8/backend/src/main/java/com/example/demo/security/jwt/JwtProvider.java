package com.example.demo.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.example.demo.security.services.UserPrinciple;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtProvider {

    @Value("${com.example.demo.JWTSECRET}")
    private String jwtSecret;

    @Value("${com.example.demo.jwtExpiration}")
    private long jwtExpiration;

    public String generateJwtToken(Authentication authentication) {
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();

        return Jwts.builder()
                .subject(userPrinciple.getUsername())
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + jwtExpiration * 1000))
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)), Jwts.SIG.HS256)
                .compact();
        // deprecated:
        // .setSubject(userPrinciple.getUsername())
        // .setIssuedAt(new Date())
        // .setExpiration(new Date((new Date()).getTime() + jwtExpiration*1000))
        // .signWith(SignatureAlgorithm.HS512, jwtSecret)
        // .compact();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            SecretKey secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(authToken);
            // deprecated
            // Jwts.parser().setSigningKey(jwtSecret).build().parseSignedClaims(authToken);
            return true;
        } catch (SignatureException e) {
            System.out.println("Invalid JWT signature -> Message: {} " + e);
        } catch (MalformedJwtException e) {
            System.out.println("Invalid JWT token -> Message: {}" + e);
        } catch (ExpiredJwtException e) {
            System.out.println("Expired JWT token -> Message: {}" + e);
        } catch (UnsupportedJwtException e) {
            System.out.println("Unsupported JWT token -> Message: {}" + e);
        } catch (IllegalArgumentException e) {
            System.out.println("JWT claims string is empty -> Message: {}" + e);
        }

        return false;
    }

    public String getUserNameFromJwtToken(String token) {
        SecretKey secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getSubject();
        // deprecated:
        // return Jwts.parser()
        // .setSigningKey(jwtSecret)
        // .build()
        // .parseClaimsJws(token)
        // .getBody().getSubject();
    }
}
