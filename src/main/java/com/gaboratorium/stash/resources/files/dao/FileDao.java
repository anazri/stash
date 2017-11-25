package com.gaboratorium.stash.resources.files.dao;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import java.util.List;

public interface FileDao {

    @SqlQuery("select * from stash.files where id = :fileId and app_id = :appId;")
    @Mapper(FileMapper.class)
    File findById(
        @Bind("fileId") String fileId,
        @Bind("appId") String appId
    );

    @SqlQuery("select * from stash.files where file_name = :fileName and app_id = :appId and file_owner_id is null;")
    @Mapper(FileMapper.class)
    File findOwnerlessFileByName(
        @Bind("fileName") String fileName,
        @Bind("appId") String appId
    );

    @SqlQuery("select * from stash.files where file_name = :fileName and app_id = :appId and file_owner_id = :fileOwnerId")
    @Mapper(FileMapper.class)
    File findByNameAndOwner(
        @Bind("fileName") String fileName,
        @Bind("appId") String appId,
        @Bind("fileOwnerId") String fileOwnerId
    );

    @SqlQuery("select * from stash.files where app_id = :appId;")
    @Mapper(FileMapper.class)
    List<File> findByAppId(
        @Bind("appId") String appId
    );

    @SqlQuery("insert into stash.files values (:fileId, :appId, :filePath, :fileName, :fileOwnerId, :fileIsPublic) returning *;")
    @Mapper(FileMapper.class)
    File insert(
        @Bind("fileId") String fileId,
        @Bind("appId") String appId,
        @Bind("filePath") String filePath,
        @Bind("fileName") String fileName,
        @Bind("fileOwnerId") String fileOwnerId,
        @Bind("fileIsPublic") boolean isPublic
    );

    @SqlUpdate("delete from stash.files where id = :fileId and app_id = :appId")
    void delete(
        @Bind("fileId") String fileId,
        @Bind("appId") String appId
    );
}
