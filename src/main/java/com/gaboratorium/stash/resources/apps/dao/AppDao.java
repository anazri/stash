package com.gaboratorium.stash.resources.apps.dao;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

public interface AppDao {

    @SqlQuery("select * from apps where id = :appId;")
    @Mapper(AppMapper.class)
    App findById(
        @Bind("appId") String appId
    );

    @SqlQuery("insert into apps values (:appId, :appName, :appDescription, :appSecret) returning *;")
    @Mapper(AppMapper.class)
    App insert(
        @Bind("appId") String appId,
        @Bind("appName") String appName,
        @Bind("appDescription") String appDescription,
        @Bind("appSecret") String appSecret
    );

    @SqlUpdate("delete from apps where id = :appId;")
    void delete(
        @Bind("appId") String appId
    );
}
