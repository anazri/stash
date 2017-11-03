package com.gaboratorium.stash.resources.files;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaboratorium.stash.modules.appAuthenticator.appAuthenticationRequired.AppAuthenticationHeaders;
import com.gaboratorium.stash.modules.appAuthenticator.appAuthenticationRequired.AppAuthenticationRequired;
import com.gaboratorium.stash.modules.stashResponse.StashResponse;
import com.gaboratorium.stash.modules.stashTokenStore.StashTokenStore;
import com.gaboratorium.stash.modules.userAuthenticator.userAuthenticationRequired.UserAuthenticationHeaders;
import com.gaboratorium.stash.resources.files.dao.File;
import com.gaboratorium.stash.resources.files.dao.FileDao;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.*;
import java.net.URLConnection;
import java.util.UUID;

@Path("/files")
@RequiredArgsConstructor
public class FileResource {

    private final ObjectMapper objectMapper;
    private final FileDao fileDao;
    private final StashTokenStore stashTokenStore;

    private final String uploadPath = "C:/Users/gaboratorium/Desktop/";

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @AppAuthenticationRequired
    public Response uploadFile(
        @NotNull @Valid @FormDataParam("file") InputStream inputStream,
        @NotNull @Valid @FormDataParam("file") FormDataBodyPart fileDetails,
        @NotNull @Valid @HeaderParam(AppAuthenticationHeaders.APP_ID) String appId, // TODO: Snake notation ?
        @HeaderParam(UserAuthenticationHeaders.USER_ID) String userId,
        @HeaderParam(UserAuthenticationHeaders.USER_TOKEN) String userToken,
        @Valid @FormDataParam("ownerId") String ownerId
    )  {

        final boolean isOwnerIdProvided = ownerId != null;

        // Owner validation
        if (isOwnerIdProvided) {
            final boolean isOwnerAuthenticated =
                ownerId.equals(userId) && stashTokenStore.isValid(userToken, userId);

            if (!isOwnerAuthenticated) {
                return StashResponse.forbidden("Owner authentication failed.");
            }
        }

        // File existence validation
        final String fileName = fileDetails.getContentDisposition().getFileName();
        final File checkedFile = fileDao.findByNameAndOwner(fileName, appId, ownerId);
        final boolean isFileTaken = checkedFile != null;
        if (isFileTaken) {
            return StashResponse
                .conflict("File already exists. Try to call Update File instead, or upload another file.");
        }

        // Try to copy file
        try {
            final java.io.File targetFile  = new java.io.File(uploadPath + fileName);
            FileUtils.copyInputStreamToFile(inputStream, targetFile);
            final String fileId = UUID.randomUUID().toString();
            final File file = fileDao.insert(
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
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Path("/{file_name}")
    public Response getFile(
        @NotNull @PathParam("file_name") String fileName,
        @HeaderParam(AppAuthenticationHeaders.APP_ID) String appId
    ) {

        final File file = fileDao.findByName(
            fileName,
            appId
        );

        final java.io.File targetFile = new java.io.File(file.getFilePath() + file.getFileName());
        final String mimeType = URLConnection.guessContentTypeFromName(targetFile.getName());
        final String contentDisposition = String.format("attachment; filename=%s", targetFile.getName());


        return Response.ok(targetFile, mimeType)
            .header("Content-Disposition", contentDisposition)
            .build();
    }
}
