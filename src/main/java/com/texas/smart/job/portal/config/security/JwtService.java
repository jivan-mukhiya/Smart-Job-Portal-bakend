package com.texas.smart.job.portal.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {


    @Value("${jwt.secret}")
    private String secret;


    @Value("${jwt.access-expiration}")
    private long accessExpiration;


    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;


    // =========================================================
    // GENERATE ACCESS TOKEN
    // =========================================================

    public String generateAccessToken(
            UserDetails userDetails
    ) {

        return generateToken(
                new HashMap<>(),
                userDetails,
                accessExpiration
        );
    }


    // =========================================================
    // GENERATE REFRESH TOKEN
    // =========================================================

    public String generateRefreshToken(
            UserDetails userDetails
    ) {

        return generateToken(
                new HashMap<>(),
                userDetails,
                refreshExpiration
        );
    }


    // =========================================================
    // GENERATE JWT TOKEN
    // =========================================================

    private String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {

        Date now = new Date();

        return Jwts.builder()

                .claims(extraClaims)

                .subject(
                        userDetails.getUsername()
                )

                .issuedAt(now)

                .expiration(
                        new Date(
                                now.getTime()
                                        + expiration
                        )
                )

                .signWith(
                        getSigningKey()
                )

                .compact();
    }


    // =========================================================
    // EXTRACT USERNAME / EMAIL
    // =========================================================

    public String extractUsername(
            String token
    ) {

        return extractClaim(
                token,
                Claims::getSubject
        );
    }


    // =========================================================
    // EXTRACT CLAIM
    // =========================================================

    public <T> T extractClaim(
            String token,
            Function<Claims, T> claimsResolver
    ) {

        Claims claims =
                extractAllClaims(token);

        return claimsResolver.apply(
                claims
        );
    }


    // =========================================================
    // VALIDATE TOKEN
    // =========================================================

    public boolean isTokenValid(
            String token,
            UserDetails userDetails
    ) {

        final String username =
                extractUsername(token);

        return username != null
                && username.equals(
                userDetails.getUsername()
        )
                && !isTokenExpired(token);
    }


    // =========================================================
    // CHECK TOKEN EXPIRATION
    // =========================================================

    public boolean isTokenExpired(
            String token
    ) {

        return extractExpiration(token)
                .before(new Date());
    }


    // =========================================================
    // EXTRACT EXPIRATION
    // =========================================================

    private Date extractExpiration(
            String token
    ) {

        return extractClaim(
                token,
                Claims::getExpiration
        );
    }


    // =========================================================
    // EXTRACT ALL CLAIMS
    // =========================================================

    private Claims extractAllClaims(
            String token
    ) {

        return Jwts.parser()

                .verifyWith(
                        getSigningKey()
                )

                .build()

                .parseSignedClaims(token)

                .getPayload();
    }


    // =========================================================
    // GENERATE SIGNING KEY
    // =========================================================

    private SecretKey getSigningKey() {

        byte[] keyBytes =
                Decoders.BASE64.decode(
                        secret
                );

        return Keys.hmacShaKeyFor(
                keyBytes
        );
    }
}