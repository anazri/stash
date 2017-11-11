package com.gaboratorium.stash.resources.dashboard.views;

import io.dropwizard.views.View;
import lombok.Getter;

public class AppView extends View {

    @Getter
    private final AppViewModel vm;

    public AppView(AppViewModel vm) {
        super("app.mustache");
        this.vm = vm;
    }
}
