package com.gaboratorium.stash.modules.userAuthenticator.userAuthenticationRequired;

import com.gaboratorium.stash.modules.stashResponse.StashResponse;
import com.gaboratorium.stash.modules.stashTokenStore.StashTokenStore;
import liquibase.util.StringUtils;
import lombok.AllArgsConstructor;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Cookie;
import java.io.IOException;

@UserAuthenticationRequired
@AllArgsConstructor
public class UserAuthenticationRequiredFilter implements ContainerRequestFilter {

    private final StashTokenStore stashTokenStore;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        final String token = requestContext.getHeaderString(UserAuthenticationHeaders.USER_TOKEN);
        final String userId = requestContext.getHeaderString(UserAuthenticationHeaders.USER_ID);
        final boolean isParamListProvided = StringUtils.isEmpty(token) || StringUtils.isEmpty(userId);

        final String errorMsg =
            "User authentication failed because user token was either not provided, corrupted or expired.";

        final boolean isMasterAuthenticated = isMasterAuthenticated(requestContext);

        if (isParamListProvided && !isMasterAuthenticated) {
            requestContext.abortWith(StashResponse.forbidden(errorMsg));
        } else if (!stashTokenStore.isValid(token, userId) && !isMasterAuthenticated) {
            requestContext.abortWith(StashResponse.forbidden(errorMsg));
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
