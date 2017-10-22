package com.gaboratorium.stash.modules.stashTokenStore;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;


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
}
