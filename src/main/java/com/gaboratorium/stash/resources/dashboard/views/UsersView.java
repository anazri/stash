package com.gaboratorium.stash.resources.dashboard.views;

import io.dropwizard.views.View;
import lombok.Data;

@Data
public class UsersView extends View {

    private final UsersViewModel vm;

    public UsersView(UsersViewModel vm) {
        super("users.mustache");
        this.vm = vm;
    }
}
