package com.gaboratorium.stash;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import lombok.Getter;
import org.hibernate.validator.constraints.NotEmpty;

class StashConfiguration extends Configuration {

    @JsonProperty @Getter @NotEmpty
    private String applicationName;

    @JsonProperty @Getter @NotEmpty
    private String appsTokenStoreKey;

    @JsonProperty @Getter
    private boolean isAppCreationOpen;

    @JsonProperty @Getter
    private DataSourceFactory database = new DataSourceFactory();
}
