package com.gaboratorium.stash.resources.dashboard.views;

import com.gaboratorium.stash.resources.dashboard.DashboardViewModel;
import io.dropwizard.views.View;
import lombok.Getter;

public class DocumentServiceDocsView extends View {

    @Getter
    private final DashboardViewModel vm;

    public DocumentServiceDocsView(DocumentServiceDocsViewModel vm) {
        super("document_service_docs.mustache");
        this.vm = vm;
    }
}
