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
    @Path("/{appId}/documents/{documentId}")
    @AppAuthenticationRequired
    public Response getDocumentById(
        @NotNull @PathParam("appId") final String appId,
        @NotNull @PathParam("documentId") final String documentId,
        @HeaderParam(AppAuthenticationHeaders.APP_ID) final Optional<String> appIdHeader
    ) {

        if (!appRequestGuard.isRequestAuthorized(appIdHeader, appId)) {
            return StashResponse.forbidden();
        }

        final Document document = documentDao.findById(documentId, appId);
        final boolean isDocumentNotFound = document == null;

        return isDocumentNotFound ?
            StashResponse.notFound() :
            StashResponse.ok(document);
    }

    @GET
    @Path("/{appId}/documents")
    @AppAuthenticationRequired
    public Response queryDocument(
        @NotNull @PathParam("appId") final String appId,
        @NotNull @QueryParam("key") final String key,
        @NotNull @QueryParam("value") final String value,
        @HeaderParam(AppAuthenticationHeaders.APP_ID) final Optional<String> appIdHeader,
        @QueryParam("keySecondary") final String keySecondary,
        @QueryParam("valueSecondary") final String valueSecondary
    ) {

        if (!appRequestGuard.isRequestAuthorized(appIdHeader, appId)) {
            return StashResponse.forbidden();
        }

        final boolean isSecondaryKeyValuePairProvided = keySecondary != null && valueSecondary != null;

        final List<Document> documents = isSecondaryKeyValuePairProvided ?
            documentDao.findByFilters(appId, key, value, keySecondary, valueSecondary) :
            documentDao.findByFilter(appId, key, value);

        final boolean isListEmpty = documents.isEmpty();

        return isListEmpty ?
            StashResponse.notFound() :
            StashResponse.ok(documents);
    }

    @PUT
    @AppAuthenticationRequired
    @UserAuthenticationRequired
    @Path("/{appId}/documents/{documentId}")
    public Response updateDocument(
        @NotNull @PathParam("appId") final String appId,
        @NotNull @PathParam("documentId") final String documentId,
        @HeaderParam(AppAuthenticationHeaders.APP_ID) final Optional<String> appIdHeader,
        @HeaderParam(UserAuthenticationHeaders.USER_ID) final Optional<String> userIdHeader,
        @NotNull @Valid final UpdateDocumentRequestBody body
    ) throws SQLException, JsonProcessingException {

        if (!appRequestGuard.isRequestAuthorized(appIdHeader, appId)) {
            return StashResponse.forbidden("App is not authorized");
        }

        final Document document = documentDao.findById(documentId, appId);
        final boolean isDocumentNotFound = document == null;

        if (isDocumentNotFound || !userRequestGuard.isRequestAuthorized(userIdHeader, document.getDocumentOwnerId())) {
            return StashResponse.forbidden("This user have no rights to update requested document.");
        }

        final Document updatedDocument = documentDao.update(
            documentId,
            body.getDocumentContentAsJsonb()
        );

        return StashResponse.ok(updatedDocument);
    }


    @DELETE
    @Path("/{appId}/documents/{documentId}")
    @AppAuthenticationRequired
    @UserAuthenticationRequired
    public Response deleteDocument(
        @HeaderParam(AppAuthenticationHeaders.APP_ID) final Optional<String> appIdHeader,
        @HeaderParam(UserAuthenticationHeaders.USER_ID) final Optional<String> userIdHeader,
        @NotNull @PathParam("appId") final String appId,
        @NotNull @PathParam("documentId") String documentId
    ) {

        if (!appRequestGuard.isRequestAuthorized(appIdHeader, appId)) {
            return StashResponse.forbidden("App authorization failed");
        }

        final Document document = documentDao.findById(documentId, appId);
        final boolean isDocumentNotFound = document == null;

        if (isDocumentNotFound) {
            return StashResponse.notFound();
        }

        if (!userRequestGuard.isRequestAuthorized(userIdHeader, document.getDocumentOwnerId())) {
            return StashResponse.forbidden("User authorization failed.");
        }

        documentDao.delete(
            documentId,
            appId
        );

        return StashResponse.ok();
    }
}
