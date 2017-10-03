package com.gaboratorium.stash.resources;

import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/apps")
@Produces(MediaType.APPLICATION_JSON)
public class ApplicationResource {

    private String stashApplicationName;
    private ApplicationDao applicationDao;

    public ApplicationResource(String stashApplicationName, ApplicationDao applicationDao) {
        this.stashApplicationName = stashApplicationName;
        this.applicationDao = applicationDao;
    }

    @POST
    public void createApplication(
        @Valid final CreateApplicationBody body
    ) {
        // TODO: implement
        applicationDao.insert(body.getApplicationId(), body.getApplicationName(), body.getAdminEmail());
    }

    @GET
    public String sayHello() {
        final String applicationId = "1";
        final String myApplication = applicationDao.findById(applicationId);
        return "Hello stranger! Welcome to " + stashApplicationName + "/" + myApplication;
    }
}
