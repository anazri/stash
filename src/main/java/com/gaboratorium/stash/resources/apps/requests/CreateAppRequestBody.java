package com.gaboratorium.stash.resources.apps.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@JsonDeserialize
public class CreateAppRequestBody {

    @JsonProperty @NotNull @Getter
    public String appId;

    @JsonProperty @NotNull @Getter
    public String appSecret;

    @JsonProperty @NotNull @Getter
    public String masterEmail;

    @JsonProperty @NotNull @Getter
    public String masterPasswordHash;

    @JsonProperty @Getter
    public String appName;

    @JsonProperty @Getter
    public String appDescription;
}
