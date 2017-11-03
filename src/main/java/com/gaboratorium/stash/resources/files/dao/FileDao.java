package com.gaboratorium.stash.resources.files.dao;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

public interface FileDao {

    @SqlQuery("select * from files where id = :fileId and app_id = :appId;")
    @Mapper(FileMapper.class)
    File findById(
        @Bind("fileId") String fileId,
        @Bind("appId") String appId
    );

    @SqlQuery("insert into files values (:fileId, :appId, :filePath, :fileName, :fileOwnerId) returning *;")
    @Mapper(FileMapper.class)
    File insert(
        @Bind("fileId") String fileId,
        @Bind("appId") String appId,
        @Bind("filePath") String filePath,
        @Bind("fileName") String fileName,
        @Bind("fileOwnerId") String fileOwnerId
    );
}
