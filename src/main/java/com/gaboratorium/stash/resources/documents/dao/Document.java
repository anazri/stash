package com.gaboratorium.stash.resources.documents.dao;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class Document {

    @Getter final private String documentId;
    @Getter final private String appId;
    @Getter @JsonDeserialize final private JsonNode documentContent;
    @Getter final private String documentOwnerId;
    @Getter final private String documentCreatedAt;
}
