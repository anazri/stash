package com.gaboratorium.stash.resources.apps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaboratorium.stash.modules.appAuthenticator.appAuthenticationRequired.AppAuthenticationRequired;
import com.gaboratorium.stash.modules.stashResponse.StashResponse;
import com.gaboratorium.stash.modules.appAuthenticator.AppTokenStore;
import com.gaboratorium.stash.resources.apps.dao.App;
import com.gaboratorium.stash.resources.apps.dao.AppDao;
import com.gaboratorium.stash.resources.apps.requests.CreateAppRequestBody;
import com.gaboratorium.stash.resources.apps.requests.HeaderParams;
import lombok.RequiredArgsConstructor;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/apps")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class AppResource {

    // Constructor

    private final ObjectMapper mapper;
    private final AppDao appDao;
    private final AppTokenStore appTokenStore;

    // Endpoints

    @POST
    public Response createApp(
        @Valid @NotNull final CreateAppRequestBody body
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
                body.appSecret,
                body.masterEmail
            );

            return StashResponse.ok(app);
        }
    }

    @GET
    @AppAuthenticationRequired
    public Response getApp(
        @HeaderParam(HeaderParams.APP_ID) final String appId
    ) throws JsonProcessingException {

        final App app = appDao.findById(appId);
        return StashResponse.ok(app);
    }

    @DELETE
    @AppAuthenticationRequired
    public Response deleteApp(
        @HeaderParam(HeaderParams.APP_ID) final String appId
    ) throws Exception {

        // TODO: return if query state
        appDao.delete(appId);
        return StashResponse.ok();
    }

    @POST
    @Path("/authenticate")
    public Response authenticateApp(
        @HeaderParam(HeaderParams.APP_ID) final String appId,
        @HeaderParam(HeaderParams.APP_SECRET) final String appSecret
    ) {

        final App app = appDao.findById(appId);
        final String token = appTokenStore.create(app.getAppId());
        final boolean isSecretValid = app.getAppSecret().equals(appSecret);

        return isSecretValid ?
                StashResponse.ok(token) :
                StashResponse.forbidden();
    }
}
