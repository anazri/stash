package com.gaboratorium.stash.resources.apps.dao;

import lombok.Data;
import lombok.Getter;

@Data
public class Master {

    @Getter final private String masterId;
    @Getter final private String appId;
    @Getter final private String masterEmail;
    @Getter final private String masterPasswordHash;
}
