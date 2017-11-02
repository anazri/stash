package com.gaboratorium.stash;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaboratorium.stash.modules.appAuthenticator.appAuthenticationRequired.AppAuthenticationRequiredFilter;
import com.gaboratorium.stash.modules.stashTokenStore.StashTokenStore;
import com.gaboratorium.stash.modules.userAuthenticator.userAuthenticationRequired.UserAuthenticationRequiredFilter;
import com.gaboratorium.stash.resources.apps.dao.AppDao;
import com.gaboratorium.stash.resources.apps.AppResource;
import com.gaboratorium.stash.resources.dashboard.DashboardResource;
import com.gaboratorium.stash.resources.documents.DocumentResource;
import com.gaboratorium.stash.resources.documents.dao.DocumentDao;
import com.gaboratorium.stash.resources.files.FileResource;
import com.gaboratorium.stash.resources.files.dao.FileDao;
import com.gaboratorium.stash.resources.users.UserResource;
import com.gaboratorium.stash.resources.users.dao.UserDao;
import io.dropwizard.Application;
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
import org.skife.jdbi.v2.DBI;
import java.sql.Connection;
import java.sql.SQLException;

public class StashApplication extends Application<StashConfiguration> {

    private ObjectMapper mapper = Jackson.newObjectMapper();

    public static void main(String[] args) throws Exception {
        new StashApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<StashConfiguration> bootstrap) {
        // Bootstrap
        bootstrap.addBundle(new ViewBundle<StashConfiguration>());
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
        final UserDao userDao = dbi.onDemand(UserDao.class);
        final DocumentDao documentDao = dbi.onDemand(DocumentDao.class);
        final FileDao fileDao = dbi.onDemand(FileDao.class);

        // Resource
        final AppResource appResource = new AppResource(
            mapper,
            appDao,
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
            fileDao
        );

        final DashboardResource dashboardResource = new DashboardResource();

        // Run Migrations
        runDatabaseMigrations(environment, dataSourceFactory);

        // Module registrations
        environment.jersey().register(AppAuthenticationRequiredFilter.class);
        environment.jersey().register(UserAuthenticationRequiredFilter.class);
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
