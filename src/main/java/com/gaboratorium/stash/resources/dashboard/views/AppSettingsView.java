package com.gaboratorium.stash.resources.dashboard.views;

import io.dropwizard.views.View;
import lombok.Getter;

public class AppSettingsView extends View {

    @Getter
    private final AppSettingsViewModel vm;

    public AppSettingsView(AppSettingsViewModel vm) {
        super("app_settings.mustache");
        this.vm = vm;
    }
}
