package com.gaboratorium.stash.resources.apps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaboratorium.stash.modules.stashResponse.StashResponse;
import com.gaboratorium.stash.modules.stashTokenStore.StashTokenStore;
import com.gaboratorium.stash.resources.apps.dao.App;
import com.gaboratorium.stash.resources.apps.dao.AppDao;
import com.gaboratorium.stash.resources.apps.requests.AuthenticateAppRequestBody;
import com.gaboratorium.stash.resources.apps.requests.CreateAppRequestBody;
import io.dropwizard.jackson.Jackson;
import lombok.RequiredArgsConstructor;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Observable;

@Path("/apps")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class AppResource {

    // Constructor
    private final ObjectMapper mapper = Jackson.newObjectMapper();
    private final AppDao appDao;
    private final StashTokenStore appTokenStore;

    private final String tokenKey = "X-Auth-Token";

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
        @PathParam("id") final String appId,
        @HeaderParam(tokenKey) final String token
    ) throws JsonProcessingException {

        final boolean isTokenValid = appTokenStore.validate(token);
        final App app = appDao.findById(appId);
        final boolean isAppThere = app != null;

        if (isAppThere && isTokenValid) {
            return StashResponse.ok(app);
        } else {
            return StashResponse.notFound("App was not found.");
        }
    }

    // TODO: require token in headers

    @DELETE
    @Path("/{id}")
    public Response deleteApp(
        @PathParam("id") final String appId,
        @HeaderParam(tokenKey) final String token
    ) {
        return new StashResponse()
            .validate(response -> {
                final String test = "asd";
                return true;
            })
            .onInvalid()
            .validate(response -> appTokenStore.validate(token))
            .onValid()
            .build();


        // final boolean isTokenValid = appTokenStore.validate(token);
        // final App app = appDao.findById(appId);
        //
        // if (app == null || !isTokenValid) {
        //     return StashResponse.forbidden();
        // } else {
        //     appDao.delete(app.getAppId());
        //     return StashResponse.ok();
        // }
    }

    @POST
    @Path("/{id}/authenticate")
    public Response authenticateApp(
        @PathParam("id") final String appId,
        @Valid final AuthenticateAppRequestBody body
    ) {
        final App app = appDao.findById(appId);
        final boolean isSecretValid = app.getAppSecret().equals(body.appSecret);
        if (isSecretValid) {
            final JsonNode json  = mapper
                .createObjectNode()
                .put("token", appTokenStore.create("this is your token"));
            return StashResponse.ok(json);
        } else {
            return StashResponse.forbidden("Invalid credentials.");
        }
    }

}
