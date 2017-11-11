package com.gaboratorium.stash.resources.dashboard.views;

import io.dropwizard.views.View;
import lombok.Getter;

public class FileServiceDocsView extends View {

    @Getter
    private FileServiceDocsViewModel vm;

    public FileServiceDocsView(FileServiceDocsViewModel vm) {
        super("file_service_docs.mustache");
        this.vm = vm;
    }
}
