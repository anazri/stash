package com.gaboratorium.stash.resources.dashboard;

import com.gaboratorium.stash.modules.stashResponse.StashResponse;
import com.gaboratorium.stash.resources.apps.dao.App;
import com.gaboratorium.stash.resources.apps.dao.AppDao;
import com.gaboratorium.stash.resources.dashboard.views.*;
import io.dropwizard.views.View;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

@Path("/")
@Produces(MediaType.TEXT_HTML)
@RequiredArgsConstructor
public class DashboardResource {

    final private AppDao appDao;

    @GET
    public Response getRoot() {
        final URI uri = URI.create("/dashboard/register");
        return Response.seeOther(uri).build();
    }

    @GET
    @Path("/dashboard")
    public Response getDashboard() {
        final URI uri = URI.create("/dashboard/register");
        return Response.seeOther(uri).build();
    }

    @GET
    @Path("/dashboard/getting_started")
    public GettingStartedView getGettingStartedView() {
        return new GettingStartedView();
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("/dashboard/register")
    public View registerApp(
        @NotNull @FormParam("appId") String appId,
        @NotNull @FormParam("appSecret") String appSecret,
        @NotNull @FormParam("appName") String appName,
        @NotNull @FormParam("appDescription") String appDescription,
        @NotNull @FormParam("masterEmail") String masterEmail,
        @NotNull @FormParam("masterPasswordHash") String masterPasswordHash
    ) {
        final boolean isRequestValid =
            !appId.isEmpty() &&
            !appSecret.isEmpty() &&
            !masterEmail.isEmpty() &&
            !masterPasswordHash.isEmpty();

        if (!isRequestValid) {
            return new RegisterView("Something went wrong. Please try again.");
        }

        final String validatedAppName = appName != null  ? appName : appId;

        appDao.insert(
            appId,
            validatedAppName,
            appDescription,
            appSecret,
            masterEmail,
            masterPasswordHash
        );

        return new LoginView("Your app has been succesfully registered.");
    }

    @GET
    @Path("dashboard/app_settings")
    public AppSettingsView getAppSettingsView(

    ) {
        final App app = appDao.findById("spotify");
        final AppSettingsViewModel model = new AppSettingsViewModel(app);
        return new AppSettingsView(model);
    }

    @GET
    @Path("dashboard/login")
    public LoginView getLoginView(

    ) {
        return new LoginView("");
    }

    @GET
    @Path("dashboard/register")
    public RegisterView getRegisterView(

    ) {
        return new RegisterView("");
    }
}
