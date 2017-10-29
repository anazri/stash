package com.gaboratorium.stash.resources.documents;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaboratorium.stash.modules.stashResponse.StashResponse;
import com.gaboratorium.stash.modules.stashTokenStore.StashTokenStore;
import com.gaboratorium.stash.resources.documents.dao.Document;
import com.gaboratorium.stash.resources.documents.dao.DocumentDao;
import com.gaboratorium.stash.resources.documents.requests.CreateDocumentRequestBody;
import lombok.RequiredArgsConstructor;
import org.postgresql.util.PGobject;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Path("/documents")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class DocumentResource {

    // Constructor

    private final ObjectMapper mapper;
    private final DocumentDao documentDao;
    private final StashTokenStore stashTokenStore;

    // Endpoints

    // TODO: Check documetn ID existence
    // TODO: if owner is provided, check owner existence
    // TODO: Add @AppAuthRequired, add appId

    @POST
    public Response createDocument(
        @Valid @NotNull CreateDocumentRequestBody body
    ) throws SQLException, JsonProcessingException {

        final String documentId = UUID.randomUUID().toString();
        final Document document = documentDao.insert(
            documentId,
            "testAppId",
            body.getDocumentContentAsJsonb(),
            body.documentOwnerId
        );

        return StashResponse.ok(document);
    }

    @GET
    @Path("/{id}")
    public Response getDocumentById(
        @PathParam("id") String documentId
    ) {
        final Document document = documentDao.findById(documentId);
        final boolean isDocumentNotFound = document == null;

        return isDocumentNotFound ?
            StashResponse.notFound() :
            StashResponse.ok(document);
    }

    @GET
    public Response getDocumentByFilters(
        @QueryParam("key") String key,
        @QueryParam("value") String value
    ) {

        final List<Document> document = documentDao.findByFilter(key, value);
        return StashResponse.ok(document);
    }

    // Let's not talk about this
    private String getFiltersAsSql(List<String> filtersAsList) {

        String sql = "";

        for(String filter : filtersAsList) {
            final Integer index = filtersAsList.indexOf(filter);

            switch (index % 3) {
                case 0:
                    sql += "document_content ->> '" + filter + "' ";
                    break;
                case 1:
                    String operator = "";
                    switch (filter) {
                        case "equals":
                            operator = "= ";
                            break;
                        case "greater":
                            operator = "> ";
                            break;
                        case "less":
                            operator = "< ";
                            break;
                    }
                    sql += operator + " ";
                    break;
                case 2:
                    sql += "'" + filter + "' ";
                    if (index < filtersAsList.size() - 1) {
                        sql += "and ";
                    } else {
                        sql += ";";
                    }
                    break;

            }
        }

        System.out.println(sql);
        return sql;
    }


}
