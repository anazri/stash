package com.gaboratorium.stash.resources.dashboard;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gaboratorium.stash.modules.masterAuthenticator.masterAuthenticationRequired.MasterAuthenticationRequired;
import com.gaboratorium.stash.modules.stashResponse.StashResponse;
import com.gaboratorium.stash.modules.stashTokenStore.StashTokenStore;
import com.gaboratorium.stash.resources.apps.dao.App;
import com.gaboratorium.stash.resources.apps.dao.AppDao;
import com.gaboratorium.stash.resources.apps.dao.Master;
import com.gaboratorium.stash.resources.apps.dao.MasterDao;
import com.gaboratorium.stash.resources.dashboard.views.*;
import com.gaboratorium.stash.resources.documents.dao.Document;
import com.gaboratorium.stash.resources.documents.dao.DocumentDao;
import com.gaboratorium.stash.resources.files.dao.File;
import com.gaboratorium.stash.resources.files.dao.FileDao;
import com.gaboratorium.stash.resources.requestLogs.dao.RequestLogDao;
import com.gaboratorium.stash.resources.users.dao.User;
import com.gaboratorium.stash.resources.users.dao.UserDao;
import io.dropwizard.views.View;
import liquibase.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Cookie;
import java.net.URI;
import java.util.List;
import java.util.UUID;

@Path("/")
@Produces(MediaType.TEXT_HTML)
@RequiredArgsConstructor
public class DashboardResource {

    final private AppDao appDao;
    final private UserDao userDao;
    final private MasterDao masterDao;
    final private DocumentDao documentDao;
    final private FileDao fileDao;
    final private RequestLogDao requestLogDao;

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
        @NotNull @FormParam("appId") final String appId,
        @NotNull @FormParam("masterEmail") final String masterEmail,
        @NotNull @FormParam("masterPassword") final String masterPassword
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
        @NotNull @FormParam("appId") final String appId,
        @NotNull @FormParam("appSecret") final String appSecret,
        @NotNull @FormParam("appName") final String appName,
        @NotNull @FormParam("appDescription") final String appDescription,
        @NotNull @FormParam("masterEmail") final String masterEmail,
        @NotNull @FormParam("masterPasswordHash") final String masterPasswordHash
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

    // Dashboard (logged in)

    @GET
    @MasterAuthenticationRequired
    @Path("dashboard/app")
    public Response getAppView(
        @CookieParam("X-Auth-Master-Id") final String masterId
    ) {
        final Master master = masterDao.findById(masterId);
        final App app = appDao.findById(master.getAppId());
        final String appToken = stashTokenStore.create(app.getAppId(), StashTokenStore.getHalfAnHourFromNow());
        final String fkey = UUID.randomUUID().toString();
        final AppViewModel model = new AppViewModel(
            app,
            master,
            appToken,
            fkey
        );

        final View view = new AppView(model);
        return getDashboardViewResponse(view, fkey);
    }

    @GET
    @MasterAuthenticationRequired
    @Path("dashboard/users")
    public Response getUsersView(
        @CookieParam("X-Auth-Master-Id") final String masterId
    ) throws JsonProcessingException {

        final Master master = masterDao.findById(masterId);
        final App app = appDao.findById(master.getAppId());
        final List<User> users = userDao.findByAppId(app.getAppId());
        final Integer numberOfUsers = users.size();
        final String fkey = UUID.randomUUID().toString();

        final UsersViewModel model = new UsersViewModel(
            app,
            users,
            numberOfUsers,
            fkey
        );

        final View view = new UsersView(model);
        return getDashboardViewResponse(view, fkey);
    }

    @GET
    @MasterAuthenticationRequired
    @Path("dashboard/documents")
    public Response getDocumentsView (
        @CookieParam("X-Auth-Master-Id") final String masterId
    ) {
        final Master master = masterDao.findById(masterId);
        final App app = appDao.findById(master.getAppId());
        final List<Document> documents = documentDao.findByAppId(app.getAppId());
        final Integer numberOfDocuments = documents.size();
        final String fkey = UUID.randomUUID().toString();

        final DocumentsViewModel model = new DocumentsViewModel(
            app,
            documents,
            numberOfDocuments,
            fkey
        );

        final View view = new DocumentsView(model);
        return getDashboardViewResponse(view, fkey);
    }

    @GET
    @MasterAuthenticationRequired
    @Path("dashboard/files")
    public Response getFilesView (
        @CookieParam("X-Auth-Master-Id") final String masterId
    ) {
        final Master master = masterDao.findById(masterId);
        final App app = appDao.findById(master.getAppId());
        final List<File> files = fileDao.findByAppId(app.getAppId());
        final Integer numberOfFiles = files.size();
        final String fkey = UUID.randomUUID().toString();

        final FilesViewModel model = new FilesViewModel(
            app,
            files,
            numberOfFiles,
            fkey
        );


        final View view = new FilesView(model);

        return getDashboardViewResponse(view, fkey);
    }

    @GET
    @MasterAuthenticationRequired
    @Path("/dashboard/docs/getting_started")
    public Response getGettingStartedView(
        @CookieParam("X-Auth-Master-Id") final String masterId
    ) {
        final Master master = masterDao.findById(masterId);
        final App app = appDao.findById(master.getAppId());
        final String fkey = UUID.randomUUID().toString();

        final GettingStartedViewModel model = new GettingStartedViewModel(
            app,
            fkey
        );

        final View view = new GettingStartedView(model);

        return getDashboardViewResponse(view, fkey);
    }

    @GET
    @MasterAuthenticationRequired
    @Path("/dashboard/docs/app_service")
    public Response getAppSettingsDocsView(
        @CookieParam("X-Auth-Master-Id") final String masterId
    ) {
        final Master master = masterDao.findById(masterId);
        final App app = appDao.findById(master.getAppId());
        final String fkey = UUID.randomUUID().toString();
        final AppSettingsDocsViewModel model = new AppSettingsDocsViewModel(
            app,
            fkey
        );
        final View view = new AppServiceDocsView(model);
        return getDashboardViewResponse(view, fkey);
    }

    @GET
    @MasterAuthenticationRequired
    @Path("/dashboard/docs/user_service")
    public Response getUserServiceDocsView(
        @CookieParam("X-Auth-Master-Id") final String masterId
    ) {
        final Master master = masterDao.findById(masterId);
        final App app = appDao.findById(master.getAppId());
        final String fkey = UUID.randomUUID().toString();
        final UserServiceDocsViewModel model = new UserServiceDocsViewModel(
            app,
            fkey
        );
        final View view = new UserServiceDocsView(model);
        return getDashboardViewResponse(view, fkey);
    }

    @GET
    @MasterAuthenticationRequired
    @Path("/dashboard/docs/document_service")
    public Response getDocumentServiceDocsView(
        @CookieParam("X-Auth-Master-Id") final String masterId
    ) {
        final Master master = masterDao.findById(masterId);
        final App app = appDao.findById(master.getAppId());
        final String fkey = UUID.randomUUID().toString();
        final DocumentServiceDocsViewModel model = new DocumentServiceDocsViewModel(
            app,
            fkey
        );

        final View view = new DocumentServiceDocsView(model);
        return getDashboardViewResponse(view, fkey);
    }

    @GET
    @MasterAuthenticationRequired
    @Path("/dashboard/docs/file_service")
    public Response getFileServiceDocsView(
        @CookieParam("X-Auth-Master-Id") final String masterId
    ) {
        final Master master = masterDao.findById(masterId);
        final App app = appDao.findById(master.getAppId());
        final String fkey = UUID.randomUUID().toString();
        final FileServiceDocsViewModel model = new FileServiceDocsViewModel(
            app,
            fkey
        );

        final View view = new FileServiceDocsView(model);
        return getDashboardViewResponse(view, fkey);
    }

    @POST
    @MasterAuthenticationRequired
    @Path("/dashboard/logout")
    public Response logout(
        @NotNull @CookieParam("X-Auth-Master-Token") final Cookie masterTokenCookie,
        @NotNull @CookieParam("X-Auth-Master-Id") final Cookie masterId,
        @NotNull @CookieParam("fkey") final Cookie fkeyCookie,
        @NotEmpty @FormParam("fkey") final String fkeyFromForm
    ) {

        final boolean isRequestComesFromDashboard = fkeyCookie.getValue().equals(fkeyFromForm);
        if (!isRequestComesFromDashboard) {
            return StashResponse.forbidden();
        }

        final NewCookie deletedMasterTokenCookie = new NewCookie(masterTokenCookie, null, 0, false);
        final NewCookie deletedMasterIdCookie = new NewCookie(masterId, null, 0, false);
        final NewCookie deleteFkeyCookie = new NewCookie(fkeyCookie, null, 0, false);

        final URI uri = URI.create("/dashboard/login");

        return Response.seeOther(uri)
            .cookie(deletedMasterTokenCookie)
            .cookie(deletedMasterIdCookie)
            .cookie(deleteFkeyCookie)
            .build();
    }

    private Response getDashboardViewResponse(final View view, final String fkey) {
        final NewCookie fkeyCookie = new NewCookie("fkey", fkey, "/dashboard", "localhost", null, 60*60*24*365, false);
        return Response.status(Response.Status.OK)
            .type(MediaType.TEXT_HTML)
            .entity(view)
            .cookie(fkeyCookie)
            .build();
    }
}
