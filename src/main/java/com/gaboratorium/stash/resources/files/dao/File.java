package com.gaboratorium.stash.resources.files.dao;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class File {

    @Getter final private String fileId;
    @Getter final private String fileUrl;
    @Getter final private String fileOwnerId;
}
