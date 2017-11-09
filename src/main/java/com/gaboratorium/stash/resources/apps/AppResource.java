package com.gaboratorium.stash.resources.apps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaboratorium.stash.modules.appAuthenticator.appAuthenticationRequired.AppAuthenticationHeaders;
import com.gaboratorium.stash.modules.appAuthenticator.appAuthenticationRequired.AppAuthenticationRequired;
import com.gaboratorium.stash.modules.stashResponse.StashResponse;
import com.gaboratorium.stash.modules.stashTokenStore.StashTokenStore;
import com.gaboratorium.stash.resources.apps.dao.App;
import com.gaboratorium.stash.resources.apps.dao.AppDao;
import com.gaboratorium.stash.resources.apps.dao.Master;
import com.gaboratorium.stash.resources.apps.dao.MasterDao;
import com.gaboratorium.stash.resources.apps.requests.AuthenticateAppRequestBody;
import com.gaboratorium.stash.resources.apps.requests.CreateAppRequestBody;
import lombok.RequiredArgsConstructor;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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
           final Master master = masterDao.insert(
               masterId,
               body.appId,
               body.masterEmail,
               body.masterPasswordHash
           );

            return StashResponse.ok(app);
        }
    }

    @GET
    @AppAuthenticationRequired
    public Response getApp(
        @NotNull @HeaderParam(AppAuthenticationHeaders.APP_ID) final String appId
    ) throws JsonProcessingException {

        final App app = appDao.findById(appId);
        return StashResponse.ok(app);
    }

    @DELETE
    @AppAuthenticationRequired
    public Response deleteApp(
        @NotNull @HeaderParam(AppAuthenticationHeaders.APP_ID) final String appId
    ) throws Exception {

        // TODO: return if query state
        appDao.delete(appId);
        return StashResponse.ok();
    }

    @POST
    @Path("/authenticate")
    public Response authenticateApp(
        @NotNull @Valid final AuthenticateAppRequestBody body
    ) {

        final App app = appDao.findById(body.getAppId());

        final boolean isAppNotFound = app == null;

        if (isAppNotFound) {
            return StashResponse.notFound();
        }

        final String token = stashTokenStore.create(app.getAppId(), StashTokenStore.getHalfAnHourFromNow());
        final boolean isSecretValid = app.getAppSecret().equals(body.getAppSecret());

        return isSecretValid ?
                StashResponse.ok(token) :
                StashResponse.forbidden();
    }
}
