package org.example.ecommerceapi.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtService {
    @Value("${jwt.expiration-ms}")
    private long ms;

    @Value("${jwt.secret}")
    private String secretKey;


    public String generateToken(UserDetails userDetails) {
        long timeNow = System.currentTimeMillis();
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date(timeNow))
                .expiration(new Date(timeNow + ms))
                .signWith(getSignInKey())
                .compact();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
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
        return extractUsername(token).equals(userDetails.getUsername())
                && extractExpiration(token).after(new Date(System.currentTimeMillis()));
    }


    private SecretKey getSignInKey(){
        byte[] decodedKey = Base64.getDecoder().decode(this.secretKey);
        return Keys.hmacShaKeyFor(decodedKey);
    }
}
