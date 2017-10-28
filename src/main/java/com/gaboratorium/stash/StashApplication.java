package com.gaboratorium.stash;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaboratorium.stash.modules.appAuthenticator.appAuthenticationRequired.AppAuthenticationRequiredFilter;
import com.gaboratorium.stash.modules.stashTokenStore.StashTokenStore;
import com.gaboratorium.stash.resources.apps.dao.AppDao;
import com.gaboratorium.stash.resources.apps.AppResource;
import com.gaboratorium.stash.resources.users.UserResource;
import com.gaboratorium.stash.resources.users.dao.UserDao;
import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.db.ManagedDataSource;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
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
    }

    @Override
    public void run(StashConfiguration configuration, Environment environment) throws Exception {

        // Database
        final DataSourceFactory dataSourceFactory = configuration.getDatabase();
        final DBI dbi = getDBI(environment, dataSourceFactory);

        // Modules
        final StashTokenStore appTokenStore = new StashTokenStore();

        // Dao
        final AppDao appDao = dbi.onDemand(AppDao.class);
        final UserDao userDao = dbi.onDemand(UserDao.class);

        // Resource
        final AppResource appResource = new AppResource(
            mapper,
            appDao,
            appTokenStore
        );

        final UserResource userResource = new UserResource(
            mapper,
            userDao
        );

        // Run Migrations
        runDatabaseMigrations(environment, dataSourceFactory);

        // Module registrations
        environment.jersey().register(AppAuthenticationRequiredFilter.class);
        environment.jersey().register(appResource);
        environment.jersey().register(userResource);
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
