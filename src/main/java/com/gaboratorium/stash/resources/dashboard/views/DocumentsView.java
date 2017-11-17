package com.gaboratorium.stash.resources.dashboard.views;

import com.gaboratorium.stash.resources.dashboard.DashboardViewModel;
import io.dropwizard.views.View;
import lombok.Getter;

public class DocumentsView extends View {

    @Getter
    private final DashboardViewModel vm;

    public DocumentsView(DocumentsViewModel vm) {
        super("documents.mustache");
        this.vm = vm;
    }
}
