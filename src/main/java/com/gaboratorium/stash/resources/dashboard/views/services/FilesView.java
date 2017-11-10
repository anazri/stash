package com.gaboratorium.stash.resources.dashboard.views.services;

import io.dropwizard.views.View;
import lombok.Getter;

public class FilesView extends View {

    @Getter
    private final FilesViewModel vm;

    public FilesView(FilesViewModel vm) {
        super("files.mustache");
        this.vm = vm;
    }
}
