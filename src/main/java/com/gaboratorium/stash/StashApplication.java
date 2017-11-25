package com.gaboratorium.stash;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.gaboratorium.stash.modules.appAuthenticator.appAuthenticationRequired.AppAuthenticationRequiredFilter;
import com.gaboratorium.stash.modules.masterAuthenticator.masterAuthenticationRequired.MasterAuthenticationRequiredFilter;
import com.gaboratorium.stash.modules.requestAuthorizator.RequestGuard;
import com.gaboratorium.stash.modules.stashTokenStore.StashTokenStore;
import com.gaboratorium.stash.modules.userAuthenticator.userAuthenticationRequired.UserAuthenticationRequiredFilter;
import com.gaboratorium.stash.resources.apps.dao.AppDao;
import com.gaboratorium.stash.resources.apps.AppResource;
import com.gaboratorium.stash.resources.apps.dao.MasterDao;
import com.gaboratorium.stash.resources.dashboard.DashboardResource;
import com.gaboratorium.stash.resources.documents.DocumentResource;
import com.gaboratorium.stash.resources.documents.dao.DocumentDao;
import com.gaboratorium.stash.resources.files.FileResource;
import com.gaboratorium.stash.resources.files.dao.FileDao;
import com.gaboratorium.stash.resources.requestLogs.dao.RequestLogDao;
import com.gaboratorium.stash.resources.users.UserResource;
import com.gaboratorium.stash.resources.users.dao.UserDao;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.db.ManagedDataSource;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.skife.jdbi.v2.DBI;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class StashApplication extends Application<StashConfiguration> {

    private ObjectMapper mapper = Jackson
        .newObjectMapper()
        .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

    public static void main(String[] args) throws Exception {

        new StashApplication().run(args);

        System.out.println();
        System.out.println("Stash Backend is up and running. Visit http://localhost:8080 to open the Stash Dashboard.");
    }

    @Override
    public void initialize(Bootstrap<StashConfiguration> bootstrap) {
        // Bootstrap
        bootstrap.addBundle(new ViewBundle<StashConfiguration>());
        bootstrap.addBundle(new AssetsBundle("/assets/"));
    }

    @Override
    public void run(StashConfiguration configuration, Environment environment) throws Exception {

        // Enable CORS headers
        final FilterRegistration.Dynamic cors =
            environment.servlets().addFilter("CORS", CrossOriginFilter.class);

        // Configure CORS parameters
        cors.setInitParameter("allowedOrigins", "*");
        cors.setInitParameter("allowedHeaders", "X-Requested-With,Content-Type,Accept,Origin");
        cors.setInitParameter("allowedMethods", "OPTIONS,GET,PUT,POST,DELETE,HEAD");

        // Add URL mapping
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");

        // Create database if not exists
        createStashDb(configuration);

        // Database
        final DataSourceFactory dataSourceFactory = configuration.getDatabase();
        final DBI dbi = getDBI(environment, dataSourceFactory);

        // Modules
        final StashTokenStore stashTokenStore = new StashTokenStore(
            configuration.getAppsTokenStoreKey(),
            configuration.getAppAuthTokenExpiryTimeInMinutes(),
            configuration.getUserAuthTokenExpiryTimeInMinutes(),
            configuration.getMasterAuthTokenExpiryTimeInMinutes()
        );

        final RequestGuard appRequestGuard = new RequestGuard(
            configuration.isAppAuthenticationRequired()
        );

        final RequestGuard userRequestGuard = new RequestGuard(
            configuration.isUserAuthenticationRequired()
        );

        // Daos
        final AppDao appDao = dbi.onDemand(AppDao.class);
        final MasterDao masterDao = dbi.onDemand(MasterDao.class);
        final UserDao userDao = dbi.onDemand(UserDao.class);
        final DocumentDao documentDao = dbi.onDemand(DocumentDao.class);
        final FileDao fileDao = dbi.onDemand(FileDao.class);
        final RequestLogDao requestLogDao = dbi.onDemand(RequestLogDao.class);

        // Filters
        final AppAuthenticationRequiredFilter appAuthenticationRequiredFilter =
            new AppAuthenticationRequiredFilter(
                stashTokenStore,
                configuration.isAppAuthenticationRequired()
            );

        final UserAuthenticationRequiredFilter userAuthenticationRequiredFilter =
            new UserAuthenticationRequiredFilter(
                stashTokenStore,
                configuration.isUserAuthenticationRequired()
            );

        final MasterAuthenticationRequiredFilter masterAuthenticationRequiredFilter =
            new MasterAuthenticationRequiredFilter(
                stashTokenStore
            );

        // Resources
        final AppResource appResource = new AppResource(
            mapper,
            appDao,
            masterDao,
            stashTokenStore,
            appRequestGuard
        );

        final UserResource userResource = new UserResource(
            mapper,
            userDao,
            appDao,
            stashTokenStore,
            appRequestGuard,
            userRequestGuard
        );

        final DocumentResource documentResource = new DocumentResource(
            mapper,
            documentDao,
            stashTokenStore,
            appRequestGuard,
            userRequestGuard
        );

        final FileResource fileResource = new FileResource(
            mapper,
            fileDao,
            stashTokenStore,
            appRequestGuard,
            userRequestGuard
        );

        final DashboardResource dashboardResource = new DashboardResource(
            appDao,
            userDao,
            masterDao,
            documentDao,
            fileDao,
            requestLogDao,
            stashTokenStore
        );

        // Run Migrations
        runDatabaseMigrations(environment, dataSourceFactory);

        // Register filters and resources
        environment.jersey().register(appAuthenticationRequiredFilter);
        environment.jersey().register(userAuthenticationRequiredFilter);
        environment.jersey().register(masterAuthenticationRequiredFilter);
        environment.jersey().register(MultiPartFeature.class);
        environment.jersey().register(appResource);
        environment.jersey().register(userResource);
        environment.jersey().register(documentResource);
        environment.jersey().register(fileResource);
        environment.jersey().register(dashboardResource);
    }

    private void runDatabaseMigrations(Environment environment, DataSourceFactory database) throws LiquibaseException, SQLException {
        final ManagedDataSource ds = database.build(environment.metrics(), "migration");
        final Connection connection = ds.getConnection();
        final Liquibase migrator = new Liquibase(
            "migrations.xml",
            new ClassLoaderResourceAccessor(),
            new JdbcConnection(connection)
        );

        migrator.update("Custom contexts");
    }

    private void createStashDb(StashConfiguration configuration) throws SQLException, ClassNotFoundException {

        Class.forName("org.postgresql.Driver");
        Connection connection = null;
        connection = DriverManager.getConnection(
            configuration.getDatabase().getUrl(),
            configuration.getDatabase().getUser(),
            configuration.getDatabase().getPassword()
        );

        ResultSet executeQuery = connection.createStatement().executeQuery("SELECT datname FROM pg_database;");

        List<String> tables = new ArrayList<>();

        while(executeQuery.next()) {
            tables.add(executeQuery.getString(1));
        }

        if(!tables.contains("stash")) {
            connection.createStatement().execute("Create database stash;");
        }
        connection.close();
    }

    private DBI getDBI (Environment environment, DataSourceFactory database) {
        return new DBIFactory().build(environment, database, "postgresql");
    }
}
