package com.gaboratorium.stash.resources.dashboard.views;

import com.gaboratorium.stash.resources.dashboard.DashboardViewModel;
import io.dropwizard.views.View;
import lombok.Getter;

public class AppServiceDocsView extends View {

    @Getter
    private final DashboardViewModel vm;

    public AppServiceDocsView(AppSettingsDocsViewModel vm) {
        super("app_settings_docs.mustache");
        this.vm = vm;
    }
}
