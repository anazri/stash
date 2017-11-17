package com.gaboratorium.stash.resources.dashboard.views;

import com.gaboratorium.stash.resources.dashboard.DashboardViewModel;
import io.dropwizard.views.View;
import lombok.Getter;

public class FileServiceDocsView extends View {

    @Getter
    private DashboardViewModel vm;

    public FileServiceDocsView(FileServiceDocsViewModel vm) {
        super("file_service_docs.mustache");
        this.vm = vm;
    }
}
