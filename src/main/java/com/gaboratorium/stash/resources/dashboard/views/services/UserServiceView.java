package com.gaboratorium.stash.resources.dashboard.views.services;

import io.dropwizard.views.View;
import lombok.Data;

@Data
public class UserServiceView extends View {

    private final UserServiceViewModel vm;

    public UserServiceView(UserServiceViewModel vm) {
        super("user_service.mustache");
        this.vm = vm;
    }
}
