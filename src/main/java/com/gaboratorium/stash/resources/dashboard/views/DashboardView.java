package com.gaboratorium.stash.resources.dashboard.views;

import io.dropwizard.views.View;
import lombok.Getter;


public class DashboardView extends View {

    @Getter
    private String name = "Gabor";

    private String pageName = "home.mustache";

    public DashboardView() {
        super("index.mustache");
    }
}
