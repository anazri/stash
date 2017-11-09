package com.gaboratorium.stash.resources.dashboard;

import com.gaboratorium.stash.resources.apps.dao.App;
import com.gaboratorium.stash.resources.apps.dao.AppDao;
import com.gaboratorium.stash.resources.dashboard.views.*;
import lombok.RequiredArgsConstructor;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/")
@Produces(MediaType.TEXT_HTML)
@RequiredArgsConstructor
public class DashboardResource {

    final private AppDao appdao;

    @GET
    public GettingStartedView getGettingStartedView() {
        return new GettingStartedView();
    }

    @GET
    @Path("/app_settings/")
    public AppSettingsView getAppSettingsView(

    ) {
        final App app = appdao.findById("spotify");
        final AppSettingsViewModel model = new AppSettingsViewModel(app);
        return new AppSettingsView(model);
    }

    @GET
    @Path("/login")
    public LoginView getLoginView(

    ) {
        return new LoginView();
    }

    @GET
    @Path("/register")
    public RegisterView getRegisterView(

    ) {
        return new RegisterView();
    }
}
