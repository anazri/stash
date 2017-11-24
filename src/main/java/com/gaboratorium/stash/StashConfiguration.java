package com.gaboratorium.stash;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import lombok.Getter;

class StashConfiguration extends Configuration {

    @JsonProperty @Getter
    private String applicationName;

    @JsonProperty @Getter
    private String appsTokenStoreKey;

    @JsonProperty @Getter
    private boolean isAppCreationOpen;

    @JsonProperty @Getter
    private boolean isAppAuthenticationRequired;

    @JsonProperty @Getter
    private Integer appAuthTokenExpiryTimeInMinutes;

    @JsonProperty @Getter
    private boolean isUserAuthenticationRequired;

    @JsonProperty @Getter
    private Integer userAuthTokenExpiryTimeInMinutes;

    @JsonProperty @Getter
    private boolean isMasterAuthenticationRequired;

    @JsonProperty @Getter
    private Integer masterAuthTokenExpiryTimeInMinutes;

    @JsonProperty @Getter
    private DataSourceFactory database = new DataSourceFactory();
}
