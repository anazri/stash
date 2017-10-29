package com.gaboratorium.stash.resources.dashboard;

import com.gaboratorium.stash.resources.dashboard.views.DashboardView;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
@Produces(MediaType.TEXT_HTML)
public class DashboardResource {

    @GET
    public DashboardView getDashboard() {
        return new DashboardView();
    }
}
