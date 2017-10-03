package com.gaboratorium.stash.resources;

import io.dropwizard.validation.ValidationMethod;

// TODO: Install Lombok
// TODO: JDBI create database if not exists
// @Data
public class CreateApplicationBody {
    public String getApplicationId() {
        return applicationId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public String applicationId;
    public String applicationName;
    public String adminEmail;

    @ValidationMethod(message = "Parameters cannot be null")
    boolean isParameterListSet() {
        return applicationId != null && applicationName != null;
    }

}
