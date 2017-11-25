package com.gaboratorium.stash.modules.requestAuthorizator;

import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
public class RequestGuard {

    private boolean isAuthenticationRequired;

    public boolean isRequestAuthorized(Optional<String> requesterId, String targetId) {
        return !isAuthenticationRequired || requesterId.map(targetId::equals).orElse(false);
    }
}
