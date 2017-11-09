package com.gaboratorium.stash.resources.dashboard.views.docs;

import com.gaboratorium.stash.resources.apps.dao.App;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class AppSettingsDocsViewModel {

    @Getter
    private final App app;
}
