package com.gaboratorium.stash.modules.appAuthenticator.appAuthenticationRequired;

import com.gaboratorium.stash.modules.stashTokenStore.StashTokenStore;
import com.gaboratorium.stash.modules.stashResponse.StashResponse;
import liquibase.util.StringUtils;
import lombok.AllArgsConstructor;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Cookie;
import java.io.IOException;

@AppAuthenticationRequired
@AllArgsConstructor
public class AppAuthenticationRequiredFilter implements ContainerRequestFilter {

    private final StashTokenStore stashTokenStore;
    private final boolean isAppAuthenticationRequired;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        if (isAppAuthenticationRequired) {
            final String token = requestContext.getHeaderString(AppAuthenticationHeaders.APP_TOKEN);
            final String appId = requestContext.getHeaderString(AppAuthenticationHeaders.APP_ID);
            final boolean isParamListProvided = StringUtils.isEmpty(token) || StringUtils.isEmpty(appId);
            final String errorMsg =
                "App authentication failed because auth token was either not provided, corrupted or expired.";

            final boolean isMasterAuthenticated = isMasterAuthenticated(requestContext);

            if (isParamListProvided && !isMasterAuthenticated) {
                requestContext.abortWith(StashResponse.forbidden(errorMsg));
            } else if (!stashTokenStore.isValid(token, appId) && !isMasterAuthenticated) {
                requestContext.abortWith(StashResponse.forbidden(errorMsg));
            }
        }
    }

    private boolean isMasterAuthenticated(
        ContainerRequestContext requestContext
    ) {
        final Cookie tokenCookie = requestContext.getCookies().get("X-Auth-Master-Token");
        final Cookie masterIdCookie = requestContext.getCookies().get("X-Auth-Master-Id");

        final boolean isParamListNotProvided =  tokenCookie == null || masterIdCookie == null;

        if (isParamListNotProvided) {
            return false;
        } else {
            final String token = tokenCookie.getValue();
            final String masterId = masterIdCookie.getValue();
            return stashTokenStore.isValid(token, masterId);
        }
    }
}

