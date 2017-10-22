package com.gaboratorium.stash.resources.apps.requestBodies;

import lombok.Getter;

import javax.validation.constraints.NotNull;

public class CreateAppRequestBody {

    @NotNull @Getter
    public String appId;

    @NotNull @Getter
    public String appSecret;

    @NotNull @Getter
    public String masterEmail;

    @Getter
    public String appName;

    @Getter
    public String appDescription;
}
