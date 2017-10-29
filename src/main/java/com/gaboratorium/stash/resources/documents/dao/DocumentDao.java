package com.gaboratorium.stash.resources.documents.dao;

import org.postgresql.util.PGobject;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import java.util.List;

public interface DocumentDao {

    @SqlQuery("select * from documents where id = :documentId;")
    @Mapper(DocumentMapper.class)
    Document findById(
        @Bind("documentId") String documentId
    );

    @SqlQuery("select * from documents where document_content ->> :key = :value")
    @Mapper(DocumentMapper.class)
    List<Document> findByFilter(
        @Bind("key") String key,
        @Bind("value") String value
    );


    @SqlQuery("insert into documents values (:documentId, :appId, :documentContent, :documentOwnerId) returning *;")
    @Mapper(DocumentMapper.class)
    Document insert(
        @Bind("documentId") String documentId,
        @Bind("appId") String appId,
        @Bind("documentContent") PGobject documentContent,
        @Bind("documentOwnerId") String documentOwnerId
    );


}
