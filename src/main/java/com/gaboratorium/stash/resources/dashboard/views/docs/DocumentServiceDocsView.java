package com.gaboratorium.stash.resources.dashboard.views.docs;

import io.dropwizard.views.View;
import lombok.Getter;

public class DocumentServiceDocsView extends View {

    @Getter
    private final DocumentServiceDocsViewModel vm;

    public DocumentServiceDocsView(DocumentServiceDocsViewModel vm) {
        super("document_service_docs.mustache");
        this.vm = vm;
    }
}
