package com.gaboratorium.stash.resources.documents.dao;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.dropwizard.jackson.Jackson;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DocumentMapper implements ResultSetMapper<Document> {
    @Override
    public Document map(int index, ResultSet resultSet, StatementContext ctx) throws SQLException {

        JsonNode documentAsJsonNode = Jackson.newObjectMapper().createObjectNode();
        try {
            documentAsJsonNode = Jackson.newObjectMapper().readTree(resultSet.getString("document_content"));
        } catch (IOException ignored) {

        }

        return new Document(
            resultSet.getString("id"),
            resultSet.getString("app_id"),
            documentAsJsonNode,
            resultSet.getString("document_owner_id"),
            resultSet.getString("created_at")
        );
    }
}
