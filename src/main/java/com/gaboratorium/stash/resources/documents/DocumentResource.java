package com.gaboratorium.stash.resources.documents;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaboratorium.stash.modules.appAuthenticator.appAuthenticationRequired.AppAuthenticationHeaders;
import com.gaboratorium.stash.modules.appAuthenticator.appAuthenticationRequired.AppAuthenticationRequired;
import com.gaboratorium.stash.modules.stashResponse.StashResponse;
import com.gaboratorium.stash.modules.stashTokenStore.StashTokenStore;
import com.gaboratorium.stash.modules.userAuthenticator.userAuthenticationRequired.UserAuthenticationHeaders;
import com.gaboratorium.stash.modules.userAuthenticator.userAuthenticationRequired.UserAuthenticationRequired;
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

    @POST
    @AppAuthenticationRequired
    public Response createDocument(
        @NotNull @HeaderParam(AppAuthenticationHeaders.APP_ID) final String appId,
        @HeaderParam(UserAuthenticationHeaders.USER_ID) final String userId,
        @HeaderParam(UserAuthenticationHeaders.USER_TOKEN) final String userToken,
        @NotNull @Valid  CreateDocumentRequestBody body
    ) throws SQLException, JsonProcessingException {

        final String documentId = UUID.randomUUID().toString();

        final boolean isIdTaken = documentDao.findById(documentId, appId) != null;

        // TODO: Retry

        if (isIdTaken) {
            return StashResponse.conflict("Sorry, something went wrong. Please try again.");
        }

        final boolean isDocumentOwnerIdProvided = body.documentOwnerId != null;
        final boolean isUserCredentialsProvided = userId != null && userToken != null;

        // TODO: Eliminate the Christmas tree

        if (isDocumentOwnerIdProvided) {
            if (isUserCredentialsProvided) {
                final boolean isTokenValid = stashTokenStore.isValid(userToken, userId);
                final boolean isRequesterTheTargetUser = userId.equals(body.documentOwnerId);
                if (!isTokenValid || !isRequesterTheTargetUser) {
                    return StashResponse.forbidden("User authentication failed.");
                }
            } else {
                return StashResponse.forbidden("Owner cannot be set without user authentication (user token was not provided).");
            }

        }

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
        @NotNull @HeaderParam(AppAuthenticationHeaders.APP_ID) final String appId,
        @NotNull @PathParam("id") String documentId
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
        @NotNull @HeaderParam(AppAuthenticationHeaders.APP_ID) final String appId,
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
    @UserAuthenticationRequired
    @Path("/{id}")
    public Response updateDocument(
        @NotNull @HeaderParam(AppAuthenticationHeaders.APP_ID) final String appId,
        @NotNull @HeaderParam(UserAuthenticationHeaders.USER_ID) final String userId,
        @NotNull @PathParam("id") String documentId,
        @NotNull @Valid UpdateDocumentRequestBody body
    ) throws SQLException, JsonProcessingException {

        final Document document = documentDao.findById(documentId, appId);
        final boolean isDocumentNotFound = document == null;
        final boolean isUserTheOwner = userId.equals(document.getDocumentOwnerId());

        if (isDocumentNotFound || !isUserTheOwner) {
            return StashResponse.forbidden();
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
    @UserAuthenticationRequired
    public Response deleteDocument(
        @NotNull @HeaderParam(AppAuthenticationHeaders.APP_ID) final String appId,
        @NotNull @HeaderParam(UserAuthenticationHeaders.USER_ID) final String userId,
        @NotNull @PathParam("id") String documentId
    ) {
        final Document document = documentDao.findById(documentId, appId);
        final boolean isDocumentNotFound = document == null;
        final boolean isUserTheOwner = userId.equals(document.getDocumentOwnerId());

        if (isDocumentNotFound || !isUserTheOwner) {
            return StashResponse.forbidden();
        }

        documentDao.delete(
            documentId,
            appId
        );

        return StashResponse.ok();
    }
}
