package com.gaboratorium.stash.modules.stashTokenStore;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import java.time.Instant;
import java.util.Date;

@RequiredArgsConstructor
public class StashTokenStore {

    final private String key;
    final private Integer appAuthTokenExpiryTimeInMinutes;
    final private Integer userAuthTokenExpiryTimeInMinutes;
    final private Integer masterAuthTokenExpiryTimeInMinutes;

    final private SignatureAlgorithm alg = SignatureAlgorithm.HS256;
    final private String iss = "StashBackendByGaborPinter";
    final private String aud = "StashApp";
    final private JwtBuilder builder = Jwts.builder();
    final private JwtParser parser = Jwts.parser();

    public String create(String subject, Date expiration) {
        return builder
            .signWith(alg, key)
            .setSubject(subject)
            .setIssuer(iss)
            .setAudience(aud)
            .setNotBefore(getNow())
            .setExpiration(expiration)
            .compact();
    }

    public boolean isValid(String jwt, String requiredSubject) {
        try {
            parser
                .setSigningKey(key)
                .requireSubject(requiredSubject)
                .requireIssuer(iss)
                .requireAudience(aud)
                .parseClaimsJws(jwt);
            return true;
        } catch (SignatureException | ExpiredJwtException | IncorrectClaimException | MalformedJwtException e) {
            return false;
        }
    }

    public Date getAppAuthTokenExpiryTime() {
        return getExpiryDateFromNow(appAuthTokenExpiryTimeInMinutes);
    }

    public Date getUserAuthTokenExpiryTime() {
        return getExpiryDateFromNow(userAuthTokenExpiryTimeInMinutes);
    }

    public Date getMasterAuthTokenExpiryTime() {
        return getExpiryDateFromNow(masterAuthTokenExpiryTimeInMinutes);
    }

    private Date getNow() {
        return Date.from(
            Instant.now()
        );
    }

    private Date getExpiryDateFromNow(Integer minutes) {
        return Date.from(
            Instant
                .now()
                .plusSeconds(minutes * 60)
        );
    }




}
