package com.gaboratorium.stash.resources.documents.dao;

import org.postgresql.util.PGobject;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import java.util.List;

public interface DocumentDao {

    @SqlQuery("select * from documents where id = :documentId and app_id = :appId;")
    @Mapper(DocumentMapper.class)
    Document findById(
        @Bind("documentId") String documentId,
        @Bind("appId") String appId
    );

    @SqlQuery("select * from documents where " +
        "app_id = :appId and " +
        "document_content ->> :key = :value")
    @Mapper(DocumentMapper.class)
    List<Document> findByFilter(
        @Bind("appId") String appId,
        @Bind("key") String key,
        @Bind("value") String value
    );

    @SqlQuery("select * from documents where " +
        "app_id = :appId and " +
        "document_content ->> :key = :value and " +
        "document_content ->> :keySecondary = :valueSecondary")
    @Mapper(DocumentMapper.class)
    List<Document> findByFilters(
        @Bind("appId") String appId,
        @Bind("key") String key,
        @Bind("value") String value,
        @Bind("keySecondary") String keySecondary,
        @Bind("valueSecondary") String valueSecondary
    );

    @SqlQuery("select * from documents where app_id = :appId;")
    @Mapper(DocumentMapper.class)
    List<Document> findByAppId(
        @Bind("appId") String appId
    );

    @SqlQuery("insert into documents values (:documentId, :appId, :documentContent, :documentOwnerId) returning *;")
    @Mapper(DocumentMapper.class)
    Document insert(
        @Bind("documentId") String documentId,
        @Bind("appId") String appId,
        @Bind("documentContent") PGobject documentContent,
        @Bind("documentOwnerId") String documentOwnerId
    );

    @SqlQuery("update documents set document_content = :documentContent where id = :documentId returning *")
    @Mapper(DocumentMapper.class)
    Document update(
        @Bind("documentId") String documentId,
        @Bind("documentContent") PGobject documentContent
    );

    @SqlUpdate("delete from documents where id = :documentId and app_id = :appId;")
    void delete(
        @Bind("documentId") String documentId,
        @Bind("appId") String appId
    );


}
