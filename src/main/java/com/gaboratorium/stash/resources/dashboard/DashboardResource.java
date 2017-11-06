package com.gaboratorium.stash.resources.dashboard;

import com.gaboratorium.stash.resources.dashboard.views.DashboardViews;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
@Produces(MediaType.TEXT_HTML)
public class DashboardResource {

    private DashboardViews dashboardViews = new DashboardViews();

    @GET
    public DashboardViews.GettingStartedView getGettingStartedView() {
        return dashboardViews.getGettingStartedView();
    }

    @GET
    @Path("/app_settings")
    public DashboardViews.AppSettingsView getAppSettingsView() {
        return dashboardViews.getAppSettingsView();
    }
}
