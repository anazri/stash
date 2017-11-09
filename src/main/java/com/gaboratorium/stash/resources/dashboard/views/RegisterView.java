package com.gaboratorium.stash.resources.dashboard.views;

import io.dropwizard.views.View;

public class RegisterView extends View {

    private final String message;

    public RegisterView(String message) {
        super("register.mustache");
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
