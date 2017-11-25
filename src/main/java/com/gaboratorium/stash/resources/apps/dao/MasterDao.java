package com.gaboratorium.stash.resources.apps.dao;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

public interface MasterDao {

    @SqlQuery("select * from stash.masters where id = :masterId;")
    @Mapper(MasterMapper.class)
    Master findById(
        @Bind("masterId") String masterId
    );

    @SqlQuery("select * from stash.masters where master_email = :masterEmail and master_password_hash = :masterPasswordHash and app_id = :appId;")
    @Mapper(MasterMapper.class)
    Master findByCredentials(
        @Bind("masterEmail") String masterEmail,
        @Bind("masterPasswordHash") String masterPasswordHash,
        @Bind("appId") String appId
    );

    @SqlQuery("insert into stash.masters values (:masterId, :appId, :masterEmail, :masterPasswordHash) returning *;")
    @Mapper(MasterMapper.class)
    Master insert(
        @Bind("masterId") String masterId,
        @Bind("appId") String appId,
        @Bind("masterEmail") String masterEmail,
        @Bind("masterPasswordHash") String masterPasswordHash
    );
}
