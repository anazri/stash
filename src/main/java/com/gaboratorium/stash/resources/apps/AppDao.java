package com.gaboratorium.stash.resources.apps;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

public interface AppDao {

    @SqlQuery("select * from apps where id = :appId")
    @Mapper(AppMapper.class)
    App findById(
        @Bind("appId") String appId
    );

    // TODO: getWithCredentials (secret, masterPassword)

    @SqlUpdate("insert into apps values (:appId, :appName, :appDescription, :appSecret, :masterEmail)")
    void insert(
        @Bind("appId") String appId,
        @Bind("appName") String appName,
        @Bind("appDescription") String appDescription,
        @Bind("appSecret") String appSecret,
        @Bind("masterEmail") String masterEmail
    );
}
