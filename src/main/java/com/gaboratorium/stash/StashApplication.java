package com.gaboratorium.stash;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.gaboratorium.stash.modules.appAuthenticator.appAuthenticationRequired.AppAuthenticationRequiredFilter;
import com.gaboratorium.stash.modules.masterAuthenticator.masterAuthenticationRequired.MasterAuthenticationRequired;
import com.gaboratorium.stash.modules.masterAuthenticator.masterAuthenticationRequired.MasterAuthenticationRequiredFilter;
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
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.skife.jdbi.v2.DBI;
import java.sql.Connection;
import java.sql.SQLException;

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

        // Database
        final DataSourceFactory dataSourceFactory = configuration.getDatabase();
        final DBI dbi = getDBI(environment, dataSourceFactory);

        // Modules
        final StashTokenStore stashTokenStore = new StashTokenStore();

        // Dao
        final AppDao appDao = dbi.onDemand(AppDao.class);
        final MasterDao masterDao = dbi.onDemand(MasterDao.class);
        final UserDao userDao = dbi.onDemand(UserDao.class);
        final DocumentDao documentDao = dbi.onDemand(DocumentDao.class);
        final FileDao fileDao = dbi.onDemand(FileDao.class);
        final RequestLogDao requestLogDao = dbi.onDemand(RequestLogDao.class);

        // Resource
        final AppResource appResource = new AppResource(
            mapper,
            appDao,
            masterDao,
            stashTokenStore
        );

        final UserResource userResource = new UserResource(
            mapper,
            userDao,
            stashTokenStore
        );

        final DocumentResource documentResource = new DocumentResource(
            mapper,
            documentDao,
            stashTokenStore
        );

        final FileResource fileResource = new FileResource(
            mapper,
            fileDao,
            stashTokenStore
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

        environment.jersey().register(AppAuthenticationRequiredFilter.class);
        environment.jersey().register(UserAuthenticationRequiredFilter.class);
        environment.jersey().register(MasterAuthenticationRequiredFilter.class);
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

    private DBI getDBI (Environment environment, DataSourceFactory database) {
        return new DBIFactory().build(environment, database, "postgresql");
    }
}
