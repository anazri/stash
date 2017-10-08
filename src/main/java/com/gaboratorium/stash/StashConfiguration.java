package com.gaboratorium.stash;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import org.hibernate.validator.constraints.NotEmpty;

class StashConfiguration extends Configuration {

    @NotEmpty private String applicationName;
    public DataSourceFactory database = new DataSourceFactory();
    public boolean isAppCreationOpen;

    @JsonProperty
    public boolean isAppCreationOpen() {
        return isAppCreationOpen;
    }

    @JsonProperty
    public String getApplicationName() {
        return applicationName;
    }

    @JsonProperty("database")
    public DataSourceFactory getDatabase() {
        return database;
    }
}
