package com.gaboratorium.stash.resources.dashboard.views;

import com.gaboratorium.stash.resources.dashboard.DashboardViewModel;
import io.dropwizard.views.View;
import lombok.Getter;

public class GettingStartedView extends View {

    @Getter
    private DashboardViewModel vm;

    public GettingStartedView(GettingStartedViewModel vm) {
        super("getting_started.mustache");
        this.vm = vm;
    }
}
