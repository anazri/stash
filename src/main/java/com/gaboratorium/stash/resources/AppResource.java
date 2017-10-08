package com.gaboratorium.stash.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.dropwizard.jackson.Jackson;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/apps")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class AppResource {

    private final AppDao appDao;

    @POST
    public void createApp(
        @Valid final CreateAppBody body
    ) {
        appDao.insert(body.getAppId(), body.getAppName(), body.getAppDescription(), body.getAppSecret(), body.getMasterEmail());
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
