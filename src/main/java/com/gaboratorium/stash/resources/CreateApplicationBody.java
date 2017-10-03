package com.gaboratorium.stash.resources;

import io.dropwizard.validation.ValidationMethod;

public class CreateApplicationBody {
    public String getApplicationId() {
        return applicationId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public String applicationId;
    public String applicationName;

    @ValidationMethod(message = "Parameters cannot be null")
    boolean isParameterListSet() {
        return applicationId != null && applicationName != null;
    }

}
