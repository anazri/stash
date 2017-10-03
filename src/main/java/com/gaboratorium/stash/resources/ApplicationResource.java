package com.gaboratorium.stash.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/greeter")
@Produces(MediaType.APPLICATION_JSON)
public class ApplicationResource {

    private String stashApplicationName;
    private ApplicationDao applicationDao;

    public ApplicationResource(String stashApplicationName, ApplicationDao applicationDao) {
        this.stashApplicationName = stashApplicationName;
        this.applicationDao = applicationDao;
    }

    @GET
    public String sayHello() {
        String myApplication = applicationDao.findById(1);
        return "Hello stranger! Welcome to " + stashApplicationName + "/" + myApplication;
    }
}
