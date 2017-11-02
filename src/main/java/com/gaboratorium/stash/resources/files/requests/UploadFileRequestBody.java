package com.gaboratorium.stash.resources.files.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@JsonDeserialize
public class UploadFileRequestBody {

    @JsonProperty @Getter @NotNull
    private String ownerId;
}
