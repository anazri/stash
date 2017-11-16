package com.gaboratorium.stash.resources.dashboard.views;

import com.gaboratorium.stash.resources.apps.dao.App;
import com.gaboratorium.stash.resources.apps.dao.Master;
import lombok.Data;
import lombok.Getter;

@Data
public class AppViewModel {

    @Getter
    private final App app;
    @Getter
    private final Master master;
    @Getter
    private final String appToken;
}
