package com.gaboratorium.stash.resources.apps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gaboratorium.stash.modules.stashResponse.StashResponse;
import com.gaboratorium.stash.modules.stashTokenStore.StashTokenStore;
import com.gaboratorium.stash.resources.apps.requestBodies.AuthenticateAppRequestBody;
import com.gaboratorium.stash.resources.apps.requestBodies.CreateAppRequestBody;
import lombok.RequiredArgsConstructor;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/apps")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class AppResource {

    // Constructor

    private final AppDao appDao;
    private final StashTokenStore appTokenStore;

    // Endpoints

    @POST
    public Response createApp(
        @Valid final CreateAppRequestBody body
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
    @Path("/{id}")
    public Response getApp(
        @PathParam("id") final String appId
    ) throws JsonProcessingException {
        final App app = appDao.findById(appId);
        final boolean isAppThere = app != null;
        if (isAppThere) {
            return StashResponse.ok(app);
        } else {
            return StashResponse.notFound("App was not found.");
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteApp(
        @PathParam("id") final String appId
    ) {
        Response deleteResponse;
        final App app = appDao.findById(appId);
        if (app == null) {
            deleteResponse = StashResponse.notFound();
        } else {
            appDao.delete(app.getAppId());
            deleteResponse = StashResponse.ok();
        }
        return deleteResponse;
    }

    @POST
    @Path("/{id}/authenticate")
    public Response authenticateApp(
        @PathParam("id") final String appId,
        @Valid final AuthenticateAppRequestBody body
    ) {
        return StashResponse.forbidden("Feature is in development");
    }
}
