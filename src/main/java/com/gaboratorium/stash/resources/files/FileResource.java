package com.gaboratorium.stash.resources.files;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaboratorium.stash.modules.stashResponse.StashResponse;
import com.gaboratorium.stash.resources.files.dao.File;
import com.gaboratorium.stash.resources.files.dao.FileDao;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;


@Path("/files")
@Produces(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class FileResource {

    private final ObjectMapper objectMapper;
    private final FileDao fileDao;

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(
        @FormDataParam("file") InputStream inputStream,
        @FormDataParam("file") FormDataBodyPart fileDetails
    ) throws IOException {

        final String fileName = fileDetails.getContentDisposition().getFileName();
        final java.io.File targetFile  = new java.io.File("C:/Users/gaboratorium/Desktop/" + fileName);
        FileUtils.copyInputStreamToFile(inputStream, targetFile);
        return StashResponse.ok();
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
