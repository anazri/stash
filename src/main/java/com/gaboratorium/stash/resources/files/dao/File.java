package com.gaboratorium.stash.resources.files.dao;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class File {

    @Getter final private String fileId;
    @Getter final private String filePath;
    @Getter final private String fileName;
    @Getter final private String fileOwnerId;
    @Getter final private boolean isPublic;
}
