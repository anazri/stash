package com.gaboratorium.stash.resources.dashboard.views.services;

import com.gaboratorium.stash.resources.apps.dao.App;
import lombok.Data;
import lombok.Getter;

@Data
public class FilesViewModel {

    @Getter
    final private App app;
}
