package com.gaboratorium.stash.resources.apps;

import io.dropwizard.validation.ValidationMethod;

// @Data
public class CreateAppBody {
    public String getAppId() {
        return appId;
    }

    public String getAppName() {
        return appName;
    }

    public String getAppDescription() {
        return appDescription;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public String getMasterEmail() {
        return masterEmail;
    }

    public String appId;
    public String appName;
    public String appDescription;
    public String appSecret;
    public String masterEmail;

    @ValidationMethod(message = "Field `appId` has to be provided.")
    boolean isApplicationIdSet() {
        return appId != null;
    }

    @ValidationMethod(message = "Field `appSecret` has to be provided.")
    boolean isSecretProvided() {
        return appSecret != null;
    }

    @ValidationMethod(message = "Field `masterEmail` has to be provided.")
    boolean isEmailProvided() {
        return masterEmail != null;
    }



}
