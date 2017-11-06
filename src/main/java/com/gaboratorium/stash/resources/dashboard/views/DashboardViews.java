package com.gaboratorium.stash.resources.dashboard.views;

import com.gaboratorium.stash.resources.apps.dao.App;
import io.dropwizard.views.View;
import lombok.Getter;

public class DashboardViews {

    @Getter
    private final GettingStartedView gettingStartedView = new GettingStartedView();

    @Getter
    private final AppSettingsView appSettingsView = new AppSettingsView();

    public class GettingStartedView extends View {
        GettingStartedView() {
            super("getting_started.mustache");
        }
    }

    public class AppSettingsView extends View {
        AppSettingsView() {
            super("app_settings.mustache");
        }
    }

}
