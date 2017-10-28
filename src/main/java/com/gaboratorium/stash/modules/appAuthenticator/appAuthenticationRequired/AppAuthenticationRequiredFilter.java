package com.gaboratorium.stash.modules.appAuthenticator.appAuthenticationRequired;

import com.gaboratorium.stash.modules.appAuthenticator.AppTokenStore;
import com.gaboratorium.stash.modules.stashResponse.StashResponse;
import liquibase.util.StringUtils;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import java.io.IOException;

@AppAuthenticationRequired
public class AppAuthenticationRequiredFilter implements ContainerRequestFilter {


    private final AppTokenStore stashTokenStore = new AppTokenStore();

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        final String token = requestContext.getHeaderString("X-Auth-Token");
        final String appId = requestContext.getHeaderString("X-Auth-App-Id");
        final String errorMsg =
            "App authentication failed because auth token was either not provided, corrupted or expired.";

        final boolean isParamListProvided = StringUtils.isEmpty(token) || StringUtils.isEmpty(appId);

        if (isParamListProvided) {
            requestContext.abortWith(StashResponse.forbidden(errorMsg));
        } else if (!stashTokenStore.isValid(token, appId)) {
            requestContext.abortWith(StashResponse.forbidden(errorMsg));
        }
    }
}

