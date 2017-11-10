package com.gaboratorium.stash.resources.dashboard;

import com.gaboratorium.stash.modules.masterAuthenticator.masterAuthenticationRequired.MasterAuthenticationRequired;
import com.gaboratorium.stash.modules.stashTokenStore.StashTokenStore;
import com.gaboratorium.stash.resources.apps.dao.App;
import com.gaboratorium.stash.resources.apps.dao.AppDao;
import com.gaboratorium.stash.resources.apps.dao.Master;
import com.gaboratorium.stash.resources.apps.dao.MasterDao;
import com.gaboratorium.stash.resources.dashboard.views.*;
import com.gaboratorium.stash.resources.dashboard.views.docs.*;
import com.gaboratorium.stash.resources.dashboard.views.services.*;
import io.dropwizard.views.View;
import lombok.RequiredArgsConstructor;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.UUID;

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
            final URI uri = URI.create("/dashboard/docs/getting_started");

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

        final String masterId = UUID.randomUUID().toString();

        masterDao.insert(
            masterId,
            appId,
            masterEmail,
            masterPasswordHash
        );

        return new LoginView("Your app has been succesfully registered.");
    }

    // Master authentication required

    @GET
    @MasterAuthenticationRequired
    @Path("dashboard/app")
    public AppView getAppView(
        @CookieParam("X-Auth-Master-Id") String masterId
    ) {
        final Master master = masterDao.findById(masterId);
        final App app = appDao.findById(master.getAppId());
        final AppViewModel model = new AppViewModel(
            app,
            master
        );
        return new AppView(model);
    }

    @GET
    @MasterAuthenticationRequired
    @Path("dashboard/users")
    public UsersView getUsersView(
        @CookieParam("X-Auth-Master-Id") String masterId
    ) {
        final Master master = masterDao.findById(masterId);
        final App app = appDao.findById(master.getAppId());
        final UsersViewModel model = new UsersViewModel(
            app
        );
        return new UsersView(model);
    }

    @GET
    @MasterAuthenticationRequired
    @Path("dashboard/documents")
    public DocumentsView getDocumentsView (
        @CookieParam("X-Auth-Master-Id") String masterId
    ) {
        final Master master = masterDao.findById(masterId);
        final App app = appDao.findById(master.getAppId());
        final DocumentsViewModel model = new DocumentsViewModel(
            app
        );
        return new DocumentsView(model);
    }

    @GET
    @MasterAuthenticationRequired
    @Path("dashboard/files")
    public FilesView getFilesView (
        @CookieParam("X-Auth-Master-Id") String masterId
    ) {
        final Master master = masterDao.findById(masterId);
        final App app = appDao.findById(master.getAppId());
        final FilesViewModel model = new FilesViewModel(
            app
        );
        return new FilesView(model);
    }

    @GET
    @MasterAuthenticationRequired
    @Path("/dashboard/docs/getting_started")
    public GettingStartedView getGettingStartedView(
        @CookieParam("X-Auth-Master-Id") String masterId
    ) {
        final Master master = masterDao.findById(masterId);
        final App app = appDao.findById(master.getAppId());
        final GettingStartedViewModel model = new GettingStartedViewModel(
            app
        );
        return new GettingStartedView(model);
    }

    @GET
    @MasterAuthenticationRequired
    @Path("/dashboard/docs/app_service")
    public AppServiceDocsView getAppSettingsDocsView(
        @CookieParam("X-Auth-Master-Id") String masterId
    ) {
        final Master master = masterDao.findById(masterId);
        final App app = appDao.findById(master.getAppId());
        final AppSettingsDocsViewModel model = new AppSettingsDocsViewModel(
            app
        );
        return new AppServiceDocsView(model);
    }

    @GET
    @MasterAuthenticationRequired
    @Path("/dashboard/docs/user_service")
    public UserServiceDocsView getUserServiceDocsView(
        @CookieParam("X-Auth-Master-Id") String masterId
    ) {
        final Master master = masterDao.findById(masterId);
        final App app = appDao.findById(master.getAppId());
        final UserServiceDocsViewModel model = new UserServiceDocsViewModel(
            app
        );
        return new UserServiceDocsView(model);
    }

    @GET
    @MasterAuthenticationRequired
    @Path("/dashboard/docs/document_service")
    public DocumentServiceDocsView getDocumentServiceDocsView(
        @CookieParam("X-Auth-Master-Id") String masterId
    ) {
        final Master master = masterDao.findById(masterId);
        final App app = appDao.findById(master.getAppId());
        final DocumentServiceDocsViewModel model = new DocumentServiceDocsViewModel(
            app
        );
        return new DocumentServiceDocsView(model);
    }

    @GET
    @MasterAuthenticationRequired
    @Path("/dashboard/docs/file_service")
    public FileServiceDocsView getFileServiceDocsView(
        @CookieParam("X-Auth-Master-Id") String masterId
    ) {
        final Master master = masterDao.findById(masterId);
        final App app = appDao.findById(master.getAppId());
        final FileServiceDocsViewModel model = new FileServiceDocsViewModel(
            app
        );
        return new FileServiceDocsView(model);
    }

    @GET
    @MasterAuthenticationRequired
    @Path("/dashboard/logout")
    public Response logout(

    ) {
        final URI uri = URI.create("/dashboard/gin");
        return Response.seeOther(uri)
            .header("Set-Cookie", "X-Auth-Master-Token=deleted;Domain=.example.com;Path=/;Expires=Thu, 01-Jan-1970 00:00:01 GMT")
            .header("Set-Cookie", "X-Auth-Master-Id=deleted;Domain=.example.com;Path=/;Expires=Thu, 01-Jan-1970 00:00:01 GMT")
            .build();
    }
}
