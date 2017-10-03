package com.gaboratorium.stash.HelloWorld.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/greeter")
@Produces(MediaType.APPLICATION_JSON)
public class GreeterResource {

    private String applicationName;

    public GreeterResource(String applicationName) {
        this.applicationName = applicationName;
    }

    @GET
    public String sayHello() {
        return "Hello stranger! Welcome to " + applicationName;
    }
}
