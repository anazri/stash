package com.gaboratorium.stash.resources.apps.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@JsonDeserialize
public class AuthenticateAppRequestBody {

    @JsonProperty @NotNull @Getter
    public String appId;

    @JsonProperty @NotNull @Getter
    public String appSecret;
}
