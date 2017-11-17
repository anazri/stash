package com.gaboratorium.stash.resources.dashboard.views;

import com.gaboratorium.stash.resources.apps.dao.App;
import com.gaboratorium.stash.resources.dashboard.DashboardViewModel;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class AppSettingsDocsViewModel implements DashboardViewModel {

    @Getter
    private final App app;

    @Getter
    private final String fkey;
}
