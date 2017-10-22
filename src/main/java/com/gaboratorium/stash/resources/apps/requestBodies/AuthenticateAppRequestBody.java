package com.gaboratorium.stash.resources.apps.requestBodies;

import lombok.Getter;
import javax.validation.constraints.NotNull;

public class AuthenticateAppRequestBody {

    @NotNull @Getter
    public String appSecret;
}
