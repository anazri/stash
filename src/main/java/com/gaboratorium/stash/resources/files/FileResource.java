package com.gaboratorium.stash.resources.files;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaboratorium.stash.modules.stashResponse.StashResponse;
import com.gaboratorium.stash.resources.files.dao.File;
import com.gaboratorium.stash.resources.files.dao.FileDao;
import com.gaboratorium.stash.resources.files.requests.UploadFileRequestBody;
import lombok.RequiredArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;

@Path("/files")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class FileResource {

    private final ObjectMapper objectMapper;
    private final FileDao fileDao;

    @POST
    public Response uploadFile(
        @Valid @NotNull UploadFileRequestBody body
    ) {
        final String fileId = UUID.randomUUID().toString();
        final String appId  = "testAppId";
        final String fileUrl = "testFileUrl";

        final File file = fileDao.insert(
            fileId,
            appId,
            fileUrl,
            body.getOwnerId());

        return StashResponse.ok(file);
    }

    @GET
    @Path("/{id}")
    public Response getFile(
        @PathParam("id") String fileId
    ) {
        final String appId = "testAppId";

        final File file = fileDao.findById(
            fileId,
            appId
        );

        final boolean isFileNotFound = file == null;
        return isFileNotFound ?
            StashResponse.notFound() :
            StashResponse.ok(file);
    }
}
