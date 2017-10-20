package com.gaboratorium.stash.resources.apps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gaboratorium.stash.modules.stashResponse.StashResponse;
import io.dropwizard.jackson.Jackson;
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

    private final AppDao appDao;

    @POST
    public Response createApp(
        @Valid final CreateAppBody body
    ) {
        final boolean isAppIdFree = appDao.findById(body.appId) == null;
        if (!isAppIdFree) {
            return StashResponse.forbidden("App ID is taken, please choose another one.");
        } else {
           final App app = appDao.insert(
                body.appId,
                body.appName,
                body.appDescription,
                body.appSecret,
                body.masterEmail
            );

            return StashResponse.ok(app);
        }
    }

    @GET
    @Path("/{id}")
    public String getApp(
        @PathParam("id") final String appId
    ) throws JsonProcessingException {
        final App myApp = appDao.findById(appId);
        return Jackson.newObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(myApp);
    }

    // TODO: @DELETE

}
