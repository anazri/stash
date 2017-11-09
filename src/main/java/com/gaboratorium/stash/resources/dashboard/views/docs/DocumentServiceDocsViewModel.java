package com.gaboratorium.stash.resources.dashboard.views.docs;

import com.gaboratorium.stash.resources.apps.dao.App;
import lombok.Data;
import lombok.Getter;

@Data
public class DocumentServiceDocsViewModel {

    @Getter
    private final App app;
}
