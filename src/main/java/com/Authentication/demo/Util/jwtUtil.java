package com.Authentication.demo.Util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class jwtUtil {

    @Value("${jwt.secret.key}")
    private String SECRET_KEY;

    public String generateToken(UserDetails userDetails)
    {
        Map<String,Object> claims = new HashMap<>();  // ✅ FIXED: changed from cliams to claims

        return createToken(claims, userDetails.getUsername());  // ✅ FIXED: changed from cliams to claims

    }

    private String createToken(Map<String, Object> claims, String email) {  // ✅ FIXED: changed from cliams to claims

        return Jwts.builder()
                .setClaims(claims)  // ✅ FIXED: changed from cliams to claims
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();

    }

     private Claims extractAllclaims(String token)
     {
         return Jwts.parser()
                 .setSigningKey(SECRET_KEY)
                 .parseClaimsJws(token)
                 .getBody();
     }

     public <T> T extractClaim(String token, Function<Claims,T> claimsResolver)
     {
         final Claims claims = extractAllclaims(token);
         return claimsResolver.apply(claims);

     }

     public String extractEmail(String token)
     {
         return extractClaim(token,Claims::getSubject);
     }

     public Date extractExpiration(String token)
     {
            return extractClaim(token,Claims::getExpiration);
     }

     private Boolean isTokenExpired(String token)
     {
          return extractExpiration(token).before(new Date());
     }

     public boolean validateToken(String token, UserDetails userDetails)
     {
         final String email = extractEmail(token);
         return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
     }

}