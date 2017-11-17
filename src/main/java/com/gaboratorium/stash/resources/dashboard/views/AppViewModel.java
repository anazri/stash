package com.gaboratorium.stash.resources.dashboard.views;

import com.gaboratorium.stash.resources.apps.dao.App;
import com.gaboratorium.stash.resources.apps.dao.Master;
import com.gaboratorium.stash.resources.dashboard.DashboardViewModel;
import lombok.Data;
import lombok.Getter;

@Data
public class AppViewModel implements DashboardViewModel {

    @Getter
    private final App app;
    @Getter
    private final Master master;
    @Getter
    private final String appToken;
    @Getter
    private final String fkey;
}
