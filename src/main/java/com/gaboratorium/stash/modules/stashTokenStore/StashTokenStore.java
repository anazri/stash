package com.gaboratorium.stash.modules.stashTokenStore;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.Date;


@RequiredArgsConstructor
public class StashTokenStore {

    // Constructor
    final private String key;
    final private SignatureAlgorithm alg;

    // Other
    final private JwtBuilder builder = Jwts.builder();
    final private JwtParser parser = Jwts.parser();

    public String create(String subject) {
        return builder
            .setSubject(subject)
            .setExpiration(getHalfAnHourFromNow())
            .signWith(alg, key)
            .compact();
    }

    public boolean validate(String jwt) {
        try {
            parser.setSigningKey(key).parseClaimsJws(jwt);
            return true;
        } catch (SignatureException e) {
            return false;
        }
    }

    public Jwt getAppToken(String jwt) {
        return parser.parse(jwt);
    }

    private Date getHalfAnHourFromNow() {
        return Date.from(
            Instant
                .now()
                .plusSeconds(1800)
        );
    }
}
