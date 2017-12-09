package com.gaboratorium.stash.resources.apps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaboratorium.stash.modules.appAuthenticator.appAuthenticationRequired.AppAuthenticationHeaders;
import com.gaboratorium.stash.modules.appAuthenticator.appAuthenticationRequired.AppAuthenticationRequired;
import com.gaboratorium.stash.modules.requestAuthorizator.RequestGuard;
import com.gaboratorium.stash.modules.stashResponse.StashResponse;
import com.gaboratorium.stash.modules.stashTokenStore.StashTokenStore;
import com.gaboratorium.stash.resources.apps.dao.App;
import com.gaboratorium.stash.resources.apps.dao.AppDao;
import com.gaboratorium.stash.resources.apps.dao.MasterDao;
import com.gaboratorium.stash.resources.apps.requests.AuthenticateAppRequestBody;
import com.gaboratorium.stash.resources.apps.requests.CreateAppRequestBody;
import lombok.RequiredArgsConstructor;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;
import java.util.UUID;

@Path("/apps")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class AppResource {

    // Constructor

    private final ObjectMapper mapper;
    private final AppDao appDao;
    private final MasterDao masterDao;
    private final StashTokenStore stashTokenStore;
    private final RequestGuard appRequestGuard;

    // Endpoints

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED})
    public Response createApp(
        @NotNull @Valid final CreateAppRequestBody body
    ) {
        final boolean isAppIdFree = appDao.findById(body.appId) == null;
        if (!isAppIdFree) {
            return StashResponse.forbidden("App ID is taken, please choose another one.");
        } else {
            final String appName = body.appName != null ? body.appName : body.appId;
            final App app = appDao.insert(
                body.appId,
                appName,
                body.appDescription,
                body.appSecret
            );

            final String masterId = UUID.randomUUID().toString();

            masterDao.insert(
                masterId,
                body.appId,
                body.masterEmail,
                body.masterPasswordHash
            );

            return StashResponse.created(app);
        }
    }

    @GET
    @Path("/{appId}")
    @AppAuthenticationRequired
    public Response getApp(
        @NotNull @PathParam("appId") final String appId,
        @HeaderParam(AppAuthenticationHeaders.APP_ID) final Optional<String> appIdHeader
    ) throws JsonProcessingException {

        if (!appRequestGuard.isRequestAuthorized(appIdHeader, appId)) {
            return StashResponse.forbidden();
        } else {
            final App app = appDao.findById(appId);
            if (app == null) {
                return StashResponse.notFound();
            } else {
                return StashResponse.ok(app);
            }
        }
    }

    @DELETE
    @Path("/{appId}")
    @AppAuthenticationRequired
    public Response deleteApp(
        @NotNull @PathParam("appId") final String appId,
        @HeaderParam(AppAuthenticationHeaders.APP_ID) final Optional<String> appIdHeader
    ) throws Exception {

        if (!appRequestGuard.isRequestAuthorized(appIdHeader, appId)) {
            return StashResponse.forbidden();
        } else {
            appDao.delete(appId);
            return StashResponse.noContent();
        }
    }

    @POST
    @Path("/{appId}/authenticate")
    public Response authenticateApp(
        @NotNull @PathParam("appId") final String appId,
        @NotNull @Valid final AuthenticateAppRequestBody body
    ) {

        final App app = appDao.findById(appId);

        final boolean isAppNotFound = app == null;

        if (isAppNotFound) {
            return StashResponse.notFound();
        }

        final String token = stashTokenStore.create(app.getAppId(), stashTokenStore.getAppAuthTokenExpiryTime());
        final boolean isSecretValid = app.getAppSecret().equals(body.getAppSecret());

        return isSecretValid ?
                StashResponse.ok(token) :
                StashResponse.forbidden();
    }
}
