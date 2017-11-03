package com.gaboratorium.stash.resources.files;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaboratorium.stash.modules.appAuthenticator.appAuthenticationRequired.AppAuthenticationHeaders;
import com.gaboratorium.stash.modules.appAuthenticator.appAuthenticationRequired.AppAuthenticationRequired;
import com.gaboratorium.stash.modules.stashResponse.StashResponse;
import com.gaboratorium.stash.resources.files.dao.File;
import com.gaboratorium.stash.resources.files.dao.FileDao;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Path("/files")
@Produces(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class FileResource {

    private final ObjectMapper objectMapper;
    private final FileDao fileDao;
    private final String uploadPath = "C:/Users/gaboratorium/Desktop/";

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @AppAuthenticationRequired
    public Response uploadFile(
        @NotNull @Valid @FormDataParam("file") InputStream inputStream,
        @NotNull @Valid @FormDataParam("file") FormDataBodyPart fileDetails,
        @NotNull @Valid @HeaderParam(AppAuthenticationHeaders.APP_ID) String appId, // TODO: Snake notation ?
        @Valid @FormDataParam("ownerId") String ownerId
    )  {

        final String fileName = fileDetails.getContentDisposition().getFileName();
        final java.io.File targetFile  = new java.io.File(uploadPath + fileName);
        final String fileId = UUID.randomUUID().toString();


        try {
            FileUtils.copyInputStreamToFile(inputStream, targetFile);
            File file = fileDao.insert(
                fileId,
                appId,
                uploadPath,
                fileName,
                ownerId
            );

            return StashResponse.ok(file);
        } catch (IOException error) {
            return StashResponse.error(error.getMessage());
        }

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
