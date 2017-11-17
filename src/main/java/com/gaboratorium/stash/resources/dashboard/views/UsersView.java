package com.gaboratorium.stash.resources.dashboard.views;

import com.gaboratorium.stash.resources.dashboard.DashboardViewModel;
import io.dropwizard.views.View;
import lombok.Data;

@Data
public class UsersView extends View {

    private final DashboardViewModel vm;

    public UsersView(UsersViewModel vm) {
        super("users.mustache");
        this.vm = vm;
    }
}
