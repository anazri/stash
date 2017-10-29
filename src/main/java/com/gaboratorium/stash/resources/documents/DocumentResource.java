package com.gaboratorium.stash.resources.documents;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaboratorium.stash.modules.appAuthenticator.appAuthenticationRequired.AppAuthenticationHeaders;
import com.gaboratorium.stash.modules.appAuthenticator.appAuthenticationRequired.AppAuthenticationRequired;
import com.gaboratorium.stash.modules.stashResponse.StashResponse;
import com.gaboratorium.stash.modules.stashTokenStore.StashTokenStore;
import com.gaboratorium.stash.resources.documents.dao.Document;
import com.gaboratorium.stash.resources.documents.dao.DocumentDao;
import com.gaboratorium.stash.resources.documents.requests.CreateDocumentRequestBody;
import com.gaboratorium.stash.resources.documents.requests.UpdateDocumentRequestBody;
import lombok.RequiredArgsConstructor;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
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

    // TODO: Check documeny ID existence
    // TODO: if owner is provided, check owner existence
    // TODO: Add @AppAuthRequired, add appId

    @POST
    @AppAuthenticationRequired
    public Response createDocument(
        @HeaderParam(AppAuthenticationHeaders.APP_ID) final String appId,
        @Valid @NotNull CreateDocumentRequestBody body
    ) throws SQLException, JsonProcessingException {

        final String documentId = UUID.randomUUID().toString();
        final Document document = documentDao.insert(
            documentId,
            appId,
            body.getDocumentContentAsJsonb(),
            body.documentOwnerId
        );

        return StashResponse.ok(document);
    }

    @GET
    @Path("/{id}")
    @AppAuthenticationRequired
    public Response getDocumentById(
        @HeaderParam(AppAuthenticationHeaders.APP_ID) final String appId,
        @PathParam("id") String documentId
    ) {
        final Document document = documentDao.findById(documentId, appId);
        final boolean isDocumentNotFound = document == null;

        return isDocumentNotFound ?
            StashResponse.notFound() :
            StashResponse.ok(document);
    }

    @GET
    @AppAuthenticationRequired
    public Response getDocumentByFilter(
        @HeaderParam(AppAuthenticationHeaders.APP_ID) final String appId,
        @NotNull @QueryParam("key") String key,
        @NotNull @QueryParam("value") String value,
        @QueryParam("keySecondary") String keySecondary,
        @QueryParam("valueSecondary") String valueSecondary
    ) {

        final boolean isSecondaryKeyValuePairProvided = keySecondary != null && valueSecondary != null;

        final List<Document> documents = isSecondaryKeyValuePairProvided ?
            documentDao.findByFilters(appId, key, value, keySecondary, valueSecondary) :
            documentDao.findByFilter(appId, key, value);

        final boolean isListEmpty = documents.isEmpty();

        return isListEmpty ?
            StashResponse.notFound() :
            StashResponse.ok(documents);
    }

    // TODO: this replaces the object. Probably a mapping should be made, where new properties are added
    // and existing properties are updated

    @PUT
    @AppAuthenticationRequired
    @Path("/{id}")
    public Response updateDocument(
        @HeaderParam(AppAuthenticationHeaders.APP_ID) final String appId,
        @PathParam("id") String documentId,
        @Valid @NotNull UpdateDocumentRequestBody body
    ) throws SQLException, JsonProcessingException {

        final Document document = documentDao.findById(documentId, appId);
        final boolean isDocumentNotFound = document == null;

        if (isDocumentNotFound) {
            return StashResponse.notFound();
        }

        final Document updatedDocument = documentDao.update(
            documentId,
            body.getDocumentContentAsJsonb()
        );

        return StashResponse.ok(updatedDocument);
    }

    // TODO: add AND app_id = :appId to all Daos

    @DELETE
    @Path("/{id}")
    @AppAuthenticationRequired
    public Response deleteDocument(
        @HeaderParam(AppAuthenticationHeaders.APP_ID) final String appId,
        @PathParam("id") String documentId
    ) {
        final Document document = documentDao.findById(documentId, appId);
        final boolean isDocumentNotFound = document == null;

        if (isDocumentNotFound) {
            return StashResponse.notFound();
        }

        documentDao.delete(
            documentId,
            appId
        );

        return StashResponse.ok();
    }
}
