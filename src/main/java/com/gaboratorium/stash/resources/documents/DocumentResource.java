package com.gaboratorium.stash.resources.documents;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaboratorium.stash.modules.appAuthenticator.appAuthenticationRequired.AppAuthenticationHeaders;
import com.gaboratorium.stash.modules.appAuthenticator.appAuthenticationRequired.AppAuthenticationRequired;
import com.gaboratorium.stash.modules.requestAuthorizator.RequestGuard;
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
import java.util.Optional;
import java.util.UUID;

@Path("/apps")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class DocumentResource {

    // Constructor

    private final ObjectMapper mapper;
    private final DocumentDao documentDao;
    private final StashTokenStore stashTokenStore;
    private final RequestGuard appRequestGuard;
    private final RequestGuard userRequestGuard;

    // Endpoints

    @POST
    @Path("/{appId}/documents")
    @AppAuthenticationRequired
    public Response createDocument(
        @NotNull @Valid final CreateDocumentRequestBody body,
        @NotNull @PathParam("appId") final String appId,
        @HeaderParam(AppAuthenticationHeaders.APP_ID) final Optional<String> appIdHeader,
        @HeaderParam(UserAuthenticationHeaders.USER_ID) final Optional<String> userIdHeader,
        @HeaderParam(UserAuthenticationHeaders.USER_TOKEN) final Optional<String> userTokenHeader
    ) throws SQLException, JsonProcessingException {

        if (!appRequestGuard.isRequestAuthorized(appIdHeader, appId)) {
            return StashResponse.forbidden();
        }

        if (userRequestGuard.isAuthenticationRequired() && body.getDocumentOwnerId() != null) {

            if (!(userTokenHeader.isPresent() && userIdHeader.isPresent())) {
                return StashResponse.forbidden("User authentication token or ID header is not present");
            } else if (!userRequestGuard.isRequestAuthorized(userIdHeader, body.documentOwnerId.orElse(null))) {
                return StashResponse.forbidden("Requested user ID and requester user ID don't match.");
            } else if (!stashTokenStore.isValid(userTokenHeader.get(), userIdHeader.get())) {
                return StashResponse.forbidden("User authentication token is not valid.");
            }
        }

        final String documentId = UUID.randomUUID().toString();
        final boolean isIdTaken = documentDao.findById(documentId, appId) != null;
        if (isIdTaken) {
            return StashResponse.conflict("Sorry, something went wrong. Please try again.");
        }

        final String documentOwnerId;
        if (body.getDocumentOwnerId() == null) {
            documentOwnerId = null;
        } else {
            documentOwnerId = body.getDocumentOwnerId().orElse(null);
        }

        final Document document = documentDao.insert(
            documentId,
            appId,
            body.getDocumentContentAsJsonb(),
            documentOwnerId
        );

        return StashResponse.created(document);
    }

    @GET
    @Path("/apps/{appId}/documents/{documentId}")
    @AppAuthenticationRequired
    public Response getDocumentById(
        @NotNull @HeaderParam(AppAuthenticationHeaders.APP_ID) final String appId,
        @NotNull @PathParam("documentId") String documentId
    ) {
        final Document document = documentDao.findById(documentId, appId);
        final boolean isDocumentNotFound = document == null;

        return isDocumentNotFound ?
            StashResponse.notFound() :
            StashResponse.ok(document);
    }

    @GET
    @Path("/apps/{appId}/documents")
    @AppAuthenticationRequired
    public Response queryDocument(
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
    @Path("/apps/{appId}/documents/{documentId}")
    public Response updateDocument(
        @NotNull @HeaderParam(AppAuthenticationHeaders.APP_ID) final String appId,
        @NotNull @HeaderParam(UserAuthenticationHeaders.USER_ID) final String userId,
        @NotNull @PathParam("documentId") String documentId,
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
    @Path("/apps/{appId}/documents/{documentId}")
    @AppAuthenticationRequired
    @UserAuthenticationRequired
    public Response deleteDocument(
        @NotNull @HeaderParam(AppAuthenticationHeaders.APP_ID) final String appId,
        @NotNull @HeaderParam(UserAuthenticationHeaders.USER_ID) final String userId,
        @NotNull @PathParam("documentId") String documentId
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
