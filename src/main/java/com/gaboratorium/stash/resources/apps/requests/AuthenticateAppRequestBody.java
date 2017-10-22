package com.gaboratorium.stash.resources.apps.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import javax.validation.constraints.NotNull;

public class AuthenticateAppRequestBody {

    @JsonProperty @NotNull @Getter
    public String appSecret;

}
