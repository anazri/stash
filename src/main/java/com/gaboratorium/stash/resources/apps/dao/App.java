package com.gaboratorium.stash.resources.apps.dao;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class App {

    @Getter final private String appId;
    @Getter final private String appName;
    @Getter final private String appDescription;
    @Getter final private String appSecret;
    @Getter final private String masterEmail;
}

