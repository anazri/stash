package com.gaboratorium.stash.resources.dashboard.views.services;

import com.gaboratorium.stash.resources.apps.dao.App;
import com.gaboratorium.stash.resources.files.dao.File;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
public class FilesViewModel {

    @Getter
    final private App app;

    @Getter
    final private List<File> files;

    @Getter
    final private Integer numberOfFiles;
}
