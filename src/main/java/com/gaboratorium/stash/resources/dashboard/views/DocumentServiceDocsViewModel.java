package com.gaboratorium.stash.resources.dashboard.views;

import com.gaboratorium.stash.resources.apps.dao.App;
import com.gaboratorium.stash.resources.dashboard.DashboardViewModel;
import lombok.Data;
import lombok.Getter;

@Data
public class DocumentServiceDocsViewModel implements DashboardViewModel {

    @Getter
    private final App app;

    @Getter
    private final String fkey;
}
