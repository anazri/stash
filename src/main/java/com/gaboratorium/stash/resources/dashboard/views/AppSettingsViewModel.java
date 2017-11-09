package com.gaboratorium.stash.resources.dashboard.views;

import com.gaboratorium.stash.resources.apps.dao.App;
import lombok.Data;
import lombok.Getter;

@Data
public class AppSettingsViewModel {

    @Getter
    private final App app;
}
