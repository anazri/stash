package com.gaboratorium.stash.resources.dashboard.views;

import com.gaboratorium.stash.resources.apps.dao.App;
import com.gaboratorium.stash.resources.dashboard.DashboardViewModel;
import com.gaboratorium.stash.resources.documents.dao.Document;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
public class DocumentsViewModel implements DashboardViewModel {

    @Getter
    private final App app;

    @Getter
    private final List<Document> documents;

    @Getter
    private final Integer numberOfDocuments;

    @Getter
    private final String fkey;
}
