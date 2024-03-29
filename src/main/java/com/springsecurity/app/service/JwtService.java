/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.springsecurity.app.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 *
 * @author mm887
 */
@Service
public class JwtService {
    
    /*
    JSON Web Token
        contains {
            header, 
            payload "claims {registered, public, private}",
            signature
        }
    */
    
    // https://generate-random.org/encryption-key-generator?count=1&bytes=32&cipher=aes-256-cbc&string=&password=
    private static final String SECRET_KEY = "M2zLoq9E4baULSnpkbl7DvsectnqVOePpCoztsb2DqRpq96zbphD3dw98cKBn3F7";
    
    // extract one claim
    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject); // the subject == email/username
    }
    
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        // claims: {Registered, Public, Private}
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Payload part .. extract all claims in payload
    public Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /*
        -> signature part
            . To create the signature part you have to take 
                the encoded header, the encoded payload, a secret,
                the algorithm specified in the header, 
                and sign that.
    */
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    
    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(), userDetails);
    }
    
    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails)
    {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 *24))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }
    
    public boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }
    
    private Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }
    
}
