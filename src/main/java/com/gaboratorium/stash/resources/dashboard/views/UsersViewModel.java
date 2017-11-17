package com.gaboratorium.stash.resources.dashboard.views;

import com.gaboratorium.stash.resources.apps.dao.App;
import com.gaboratorium.stash.resources.dashboard.DashboardViewModel;
import com.gaboratorium.stash.resources.users.dao.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.util.List;

@RequiredArgsConstructor
public class UsersViewModel implements DashboardViewModel {

    @Getter
    private final App app;

    @Getter
    private final List<User> users;

    @Getter
    private final Integer numberOfUsers;

    @Getter
    private final String fkey;
}
