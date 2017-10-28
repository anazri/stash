package com.gaboratorium.stash.modules.appAuthenticator;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import java.time.Instant;
import java.util.Date;

@RequiredArgsConstructor
public class AppTokenStore {

    // TODO: Should be loaded from configuration file
    final private String key = "NoOneShouldKnowThisNotEvenSkat";
    final private SignatureAlgorithm alg = SignatureAlgorithm.HS256;
    final private String iss = "StashBackendByGaborPinter";
    final private String aud = "StashApp";
    final private JwtBuilder builder = Jwts.builder();
    final private JwtParser parser = Jwts.parser();

    public String create(String appId) {
        return builder
            .signWith(alg, key)
            .setSubject(appId)
            .setIssuer(iss)
            .setAudience(aud)
            .setNotBefore(getNow())
            .setExpiration(getHalfAnHourFromNow())
            .compact();
    }

    public boolean isValid(String jwt, String appId) {
        try {
            parser
                .setSigningKey(key)
                .requireSubject(appId)
                .requireIssuer(iss)
                .requireAudience(aud)
                .parseClaimsJws(jwt);
            return true;
        } catch (SignatureException | ExpiredJwtException | IncorrectClaimException | MalformedJwtException e) {
            return false;
        }
    }

    private Date getNow() {
        return Date.from(
            Instant.now()
        );
    }

    private Date getHalfAnHourFromNow() {
        return Date.from(
            Instant
                .now()
                .plusSeconds(1800)
        );
    }
}
