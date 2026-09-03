package org.example.ecommerceapi.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {
    @Value("${jwt.expiration-ms}")
    private long ms;

    @Value("${jwt.secret}")
    private SecretKey secretKey;


    public String generateToken(UserDetails userDetails) {
        long timeNow = System.currentTimeMillis();
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date(timeNow))
                .expiration(new Date(timeNow + ms))
                .signWith(secretKey)
                .compact();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUsername(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getSubject();
    }

    public Date extractExpiration(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getExpiration();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        if (extractUsername(token).equals(userDetails.getUsername())
                && extractExpiration(token).after(new Date(System.currentTimeMillis()))) {
            return true;
        } else {
            return false;
        }
    }
}
