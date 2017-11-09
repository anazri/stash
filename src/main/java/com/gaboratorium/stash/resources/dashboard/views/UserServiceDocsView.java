package com.gaboratorium.stash.resources.dashboard.views;

import io.dropwizard.views.View;
import lombok.Getter;

public class UserServiceDocsView extends View {

    @Getter
    private final UserServiceDocsViewModel vm;

    public UserServiceDocsView(UserServiceDocsViewModel vm) {
        super("user_service_docs.mustache");
        this.vm = vm;
    }
}
