package com.gaboratorium.stash.resources.apps.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import javax.validation.constraints.NotNull;

public class CreateAppRequestBody {

    @JsonProperty @NotNull @Getter
    public String appId;

    @JsonProperty @NotNull @Getter
    public String appSecret;

    @JsonProperty @NotNull @Getter
    public String masterEmail;

    @JsonProperty @Getter
    public String appName;

    @JsonProperty @Getter
    public String appDescription;
}
