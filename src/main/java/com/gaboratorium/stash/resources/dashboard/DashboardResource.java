package com.gaboratorium.stash.resources.dashboard;

import com.gaboratorium.stash.modules.masterAuthenticator.masterAuthenticationRequired.MasterAuthenticationRequired;
import com.gaboratorium.stash.modules.stashTokenStore.StashTokenStore;
import com.gaboratorium.stash.resources.apps.dao.App;
import com.gaboratorium.stash.resources.apps.dao.AppDao;
import com.gaboratorium.stash.resources.apps.dao.Master;
import com.gaboratorium.stash.resources.apps.dao.MasterDao;
import com.gaboratorium.stash.resources.dashboard.views.*;
import io.dropwizard.views.View;
import lombok.RequiredArgsConstructor;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.net.URI;

@Path("/")
@Produces(MediaType.TEXT_HTML)
@RequiredArgsConstructor
public class DashboardResource {

    final private AppDao appDao;
    final private MasterDao masterDao;
    private final StashTokenStore stashTokenStore;

    // Public

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
    @Path("dashboard/login")
    public LoginView getLoginView(

    ) {
        return new LoginView("");
    }

    @POST
    @Path("dashboard/login")
    public Response getLoginView(
        @NotNull @FormParam("appId") String appId,
        @NotNull @FormParam("masterEmail") String masterEmail,
        @NotNull @FormParam("masterPassword") String masterPassword
    ) {
        final Master master = masterDao.findByCredentials(
            masterEmail,
            masterPassword,
            appId
        );

        final boolean isMasterAuthenticated = master != null;

        if (!isMasterAuthenticated) {
            final URI uri = URI.create("/dashboard/login");
            return Response
                .seeOther(uri)
                .build();
        } else {
            final URI uri = URI.create("/dashboard/getting_started");

            final String masterToken = stashTokenStore.create(master.getMasterId(), StashTokenStore.getHalfAnHourFromNow());

            return Response
                .seeOther(uri)
                .cookie(new NewCookie("X-Auth-Master-Id", master.getMasterId()))
                .cookie(new NewCookie("X-Auth-Master-Token", masterToken))
                .build();
        }
    }

    @GET
    @Path("dashboard/register")
    public RegisterView getRegisterView(

    ) {
        return new RegisterView("");
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
            appSecret
        );

        return new LoginView("Your app has been succesfully registered.");
    }

    // Master authentication required

    @GET
    @MasterAuthenticationRequired
    @Path("dashboard/app_settings")
    public AppSettingsView getAppSettingsView(

    ) {
        final App app = appDao.findById("spotify");
        final AppSettingsViewModel model = new AppSettingsViewModel(app);
        return new AppSettingsView(model);
    }

    @GET
    @MasterAuthenticationRequired
    @Path("/dashboard/getting_started")
    public GettingStartedView getGettingStartedView() {
        return new GettingStartedView();
    }



}
