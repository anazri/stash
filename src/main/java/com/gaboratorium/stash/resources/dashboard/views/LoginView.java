package com.gaboratorium.stash.resources.dashboard.views;

import io.dropwizard.views.View;

public class LoginView extends View {

    private final String message;

    public LoginView(String message) {
        super("login.mustache");
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
