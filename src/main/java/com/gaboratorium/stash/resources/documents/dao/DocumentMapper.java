package com.gaboratorium.stash.resources.documents.dao;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DocumentMapper implements ResultSetMapper<Document> {
    @Override
    public Document map(int index, ResultSet resultSet, StatementContext ctx) throws SQLException {
        return new Document(
            resultSet.getString("id"),
            resultSet.getString("app_id"),
            resultSet.getString("document_content"),
            resultSet.getString("document_owner_id"),
            resultSet.getString("created_at")
        );
    }
}
