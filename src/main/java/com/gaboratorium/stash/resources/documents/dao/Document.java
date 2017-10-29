package com.gaboratorium.stash.resources.documents.dao;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class Document {

    @Getter final private String documentId;
    @Getter final private String appId;
    @Getter final private String documentContent;
    @Getter final private String documentOwnerId;
    @Getter final private String documentCreatedAt;
}
