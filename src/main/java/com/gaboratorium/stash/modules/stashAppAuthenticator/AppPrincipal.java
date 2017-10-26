package com.gaboratorium.stash.modules.stashAppAuthenticator;

import com.gaboratorium.stash.resources.apps.dao.App;
import lombok.AllArgsConstructor;

import java.security.Principal;

@AllArgsConstructor
public class AppPrincipal implements Principal {

    private final String name;
    public final App app;

    @Override
    public String getName() {
        return name;
    }
}
