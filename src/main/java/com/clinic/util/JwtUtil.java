package com.clinic.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;

public class JwtUtil {

    // Base64-encoded 256-bit key (32 bytes). Replace this with a secure, secret value in production.
    private static final String SECRET_KEY = "u+GE3v7kKfUM97XU4flz7x3zGkj5urW+KAwkO/9x7n4=";
    private static final long EXPIRATION_TIME = 86400000; // 1 day in milliseconds

    private static final SecretKey KEY = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));

    public static String generateToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public static Claims validateToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public static String getRoleFromToken(String token) {
        return validateToken(token).get("role", String.class);
    }
    public static SecretKey getKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));
    }
    public static String getEmailFromToken(String token) {
        return validateToken(token).getSubject();
    }
}
