package com.gaboratorium.stash.resources.dashboard.views.services;

import com.gaboratorium.stash.resources.apps.dao.App;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserServiceViewModel {

    @Getter
    private final App app;
}
