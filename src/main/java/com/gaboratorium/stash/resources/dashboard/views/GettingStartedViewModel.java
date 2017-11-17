package com.gaboratorium.stash.resources.dashboard.views;

import com.gaboratorium.stash.resources.apps.dao.App;
import com.gaboratorium.stash.resources.dashboard.DashboardViewModel;
import lombok.Data;
import lombok.Getter;

@Data
public class GettingStartedViewModel implements DashboardViewModel {

    @Getter
    final private App app;

    @Getter
    final private String fkey;
}
