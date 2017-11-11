package com.gaboratorium.stash.resources.dashboard.views;

import io.dropwizard.views.View;
import lombok.Getter;

public class AppServiceDocsView extends View {

    @Getter
    private final AppSettingsDocsViewModel vm;

    public AppServiceDocsView(AppSettingsDocsViewModel vm) {
        super("app_settings_docs.mustache");
        this.vm = vm;
    }
}
