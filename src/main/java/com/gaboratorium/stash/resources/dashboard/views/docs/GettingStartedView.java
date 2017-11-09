package com.gaboratorium.stash.resources.dashboard.views.docs;

import com.gaboratorium.stash.resources.dashboard.views.services.GettingStartedViewModel;
import io.dropwizard.views.View;
import lombok.Getter;

public class GettingStartedView extends View {

    @Getter
    private GettingStartedViewModel vm;

    public GettingStartedView(GettingStartedViewModel vm) {
        super("getting_started.mustache");
        this.vm = vm;
    }
}
