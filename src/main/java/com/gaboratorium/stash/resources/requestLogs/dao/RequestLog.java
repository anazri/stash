package com.gaboratorium.stash.resources.requestLogs.dao;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class RequestLog {

    @Getter
    final private String requestLogId;
    @Getter final private String requestType;
    @Getter final private String requestUrl;
    @Getter final private Boolean isSuccessful;
}
