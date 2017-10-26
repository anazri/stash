package com.gaboratorium.stash.modules.stashAppAuthenticator;

import com.gaboratorium.stash.resources.apps.dao.App;
import com.gaboratorium.stash.resources.apps.dao.AppDao;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
public class StashAppAuthenticator implements Authenticator<String, AppPrincipal> {

    final private String key;
    final private SignatureAlgorithm alg;
    final private AppDao appDao;

    @Override
    public Optional<AppPrincipal> authenticate(
        String token
    ) throws AuthenticationException {
        try {
            final String appId = Jwts.parser()
                .requireSubject("app_id")
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();

            final App app = appDao.findById(appId);

            return Optional.of(
                new AppPrincipal(
                    "appPrincipal",
                    app
                )
            );
        } catch (SignatureException e) {
            return Optional.empty();
        }
    }
}




