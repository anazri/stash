package com.gaboratorium.stash.resources.dashboard;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gaboratorium.stash.modules.masterAuthenticator.masterAuthenticationRequired.MasterAuthenticationRequired;
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
import com.gaboratorium.stash.resources.users.dao.User;
import com.gaboratorium.stash.resources.users.dao.UserDao;
import io.dropwizard.views.View;
import lombok.RequiredArgsConstructor;

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
    ) throws JsonProcessingException {
        final Master master = masterDao.findById(masterId);
        final App app = appDao.findById(master.getAppId());
        final List<User> users = userDao.findByAppId(app.getAppId());
        final Integer numberOfUsers = users.size();

        // final String users = Jackson.newObjectMapper().writeValueAsString(userList);

        final UsersViewModel model = new UsersViewModel(
            app,
            users,
            numberOfUsers
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
        final List<Document> documents = documentDao.findByAppId(app.getAppId());
        final Integer numberOfDocuments = documents.size();

        final DocumentsViewModel model = new DocumentsViewModel(
            app,
            documents,
            numberOfDocuments
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
        final List<File> files = fileDao.findByAppId(app.getAppId());
        final Integer numberOfFiles = files.size();

        final FilesViewModel model = new FilesViewModel(
            app,
            files,
            numberOfFiles
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
        @NotNull @CookieParam("X-Auth-Master-Token") Cookie masterTokenCookie,
        @NotNull @CookieParam("X-Auth-Master-Id") Cookie masterId
    ) {
        final NewCookie deletedMasterTokenCookie = new NewCookie(masterTokenCookie, null, 0, false);
        final NewCookie deletedMasterIdCookie = new NewCookie(masterId, null, 0, false);
        final URI uri = URI.create("/dashboard/login");

        return Response.seeOther(uri)
            .cookie(deletedMasterTokenCookie)
            .cookie(deletedMasterIdCookie)
            .build();
    }
}
