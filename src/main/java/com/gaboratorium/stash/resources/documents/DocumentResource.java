package com.gaboratorium.stash.resources.documents;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaboratorium.stash.modules.stashResponse.StashResponse;
import com.gaboratorium.stash.modules.stashTokenStore.StashTokenStore;
import com.gaboratorium.stash.resources.documents.dao.Document;
import com.gaboratorium.stash.resources.documents.dao.DocumentDao;
import com.gaboratorium.stash.resources.documents.requests.CreateDocumentRequestBody;
import lombok.RequiredArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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

    // TODO: Add json parsing
    // TODO: Check documetn ID existence
    // TODO: if owner is provided, check owner existence
    // TODO: Add @AppAuthRequired, add appId

    @POST
    public Response createDocument(
        @Valid @NotNull CreateDocumentRequestBody body
    ) throws JsonProcessingException {
        final String documentId = UUID.randomUUID().toString();
        final Document document = documentDao.insert(
            documentId,
            "testAppId",
            body.getDocumentContentAsString(),
            body.documentOwnerId
        );

        return StashResponse.ok(document);
    }

    // TODO: implement @GET
}
