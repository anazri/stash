package com.gaboratorium.stash.modules.userAuthenticator.userAuthenticationRequired;

import com.gaboratorium.stash.modules.stashResponse.StashResponse;
import com.gaboratorium.stash.modules.stashTokenStore.StashTokenStore;
import liquibase.util.StringUtils;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import java.io.IOException;

@UserAuthenticationRequired
public class UserAuthenticationRequiredFilter implements ContainerRequestFilter {

    private final StashTokenStore stashTokenStore = new StashTokenStore();

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        final String token = requestContext.getHeaderString(UserAuthenticationHeaders.USER_TOKEN);
        final String userId = requestContext.getHeaderString(UserAuthenticationHeaders.USER_ID);
        final boolean isParamListProvided = StringUtils.isEmpty(token) || StringUtils.isEmpty(userId);

        final String errorMsg =
            "User authentication failed because user token was either not provided, corrupted or expired.";

        if (isParamListProvided) {
            requestContext.abortWith(StashResponse.forbidden(errorMsg));
        } else if (!stashTokenStore.isValid(token, userId)) {
            requestContext.abortWith(StashResponse.forbidden(errorMsg));
        }
    }
}
