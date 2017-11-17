package com.gaboratorium.stash.resources.dashboard.views;

import com.gaboratorium.stash.resources.dashboard.DashboardViewModel;
import io.dropwizard.views.View;
import lombok.Getter;

public class FilesView extends View {

    @Getter
    private final DashboardViewModel vm;

    public FilesView(FilesViewModel vm) {
        super("files.mustache");
        this.vm = vm;
    }
}
