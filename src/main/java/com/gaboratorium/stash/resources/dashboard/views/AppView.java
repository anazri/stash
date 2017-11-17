package com.gaboratorium.stash.resources.dashboard.views;

import com.gaboratorium.stash.resources.dashboard.DashboardViewModel;
import io.dropwizard.views.View;
import lombok.Getter;

public class AppView extends View {

    @Getter
    private final DashboardViewModel vm;

    public AppView(AppViewModel vm) {
        super("app.mustache");
        this.vm = vm;
    }
}
