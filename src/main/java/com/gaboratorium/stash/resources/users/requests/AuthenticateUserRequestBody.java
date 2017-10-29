package com.gaboratorium.stash.resources.users.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@JsonDeserialize
public class AuthenticateUserRequestBody {

    @JsonProperty @NotNull @Getter
    public String userId;

    @JsonProperty @NotNull @Getter
    public String userPasswordHash;
}
