package com.gaboratorium.stash.resources.files;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaboratorium.stash.modules.appAuthenticator.appAuthenticationRequired.AppAuthenticationHeaders;
import com.gaboratorium.stash.modules.appAuthenticator.appAuthenticationRequired.AppAuthenticationRequired;
import com.gaboratorium.stash.modules.stashResponse.StashResponse;
import com.gaboratorium.stash.modules.stashTokenStore.StashTokenStore;
import com.gaboratorium.stash.modules.userAuthenticator.userAuthenticationRequired.UserAuthenticationHeaders;
import com.gaboratorium.stash.modules.userAuthenticator.userAuthenticationRequired.UserAuthenticationRequired;
import com.gaboratorium.stash.resources.files.dao.File;
import com.gaboratorium.stash.resources.files.dao.FileDao;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.print.attribute.standard.Media;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.net.URLConnection;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
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
        @Valid @FormDataParam("isPublic") Boolean isPublic,
        @Valid @FormDataParam("ownerId") String ownerId
    )  {

        final boolean isOwnerIdProvided = ownerId != null;
        final boolean isFilePublic = isPublic == null || isPublic;
        final String ownerNameInPath = isOwnerIdProvided ? ownerId  : "common";
        final String fileUploadPath = uploadPath + ownerNameInPath + "/";

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
            final java.io.File targetFile  = new java.io.File(fileUploadPath + fileName);
            FileUtils.copyInputStreamToFile(inputStream, targetFile);
            final String fileId = UUID.randomUUID().toString();
            final File file = fileDao.insert(
                fileId,
                appId,
                fileUploadPath,
                fileName,
                ownerId,
                isFilePublic
            );

            return StashResponse.ok(file);
        } catch (IOException error) {
            return StashResponse.error(error.getMessage());
        }

    }

    @GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Path("/{fileName}")
    public Response getFile(
        @NotNull @PathParam("fileName") String fileName,
        @QueryParam("ownerId") String ownerId,
        @NotNull @HeaderParam(AppAuthenticationHeaders.APP_ID) String appId,
        @HeaderParam(UserAuthenticationHeaders.USER_ID) String userId,
        @HeaderParam(UserAuthenticationHeaders.USER_TOKEN) String userToken
    ) {


        final File file = getFileByNameAndOwnerId(
            fileName,
            appId,
            ownerId
        );

        if (file == null) {
            return Response.status(404).entity("File not found.").build();
        }

        // Access validation
        final boolean isFileAccessible =  file.isPublic() || isOwnerAuthenticated(file, userId, userToken);

        // Build response
        if (isFileAccessible) {
            final java.io.File targetFile = new java.io.File(file.getFilePath() + file.getFileName());
            final String mimeType = URLConnection.guessContentTypeFromName(targetFile.getName());
            final String contentDisposition = String.format("attachment; filename=%s", targetFile.getName());


            return Response.ok(targetFile, mimeType)
                .header("Content-Disposition", contentDisposition)
                .build();
        } else {
            return Response.status(403).entity("Access denied.").build();
        }
    }

    private File getFileByNameAndOwnerId(
        String fileName,
        String appId,
        String ownerId
    ) {
        return ownerId == null ?
            fileDao.findOwnerlessFileByName(fileName, appId) :
            fileDao.findByNameAndOwner(fileName, appId, ownerId);
    }

    private boolean isOwnerAuthenticated(
        File file,
        String userId,
        String userToken
    ) {
        if (userId == null || userToken == null) {
            return false;
        } else {
            return stashTokenStore.isValid(userToken, userId) && file.getFileOwnerId().equals(userId);
        }
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{fileName}")
    @AppAuthenticationRequired
    @UserAuthenticationRequired
    public Response deleteFile(
        @NotNull @PathParam("fileName") String fileName,
        @NotNull @QueryParam("ownerId") String ownerId,
        @NotNull @HeaderParam(AppAuthenticationHeaders.APP_ID) String appId,
        @HeaderParam(UserAuthenticationHeaders.USER_ID) String userId,
        @HeaderParam(UserAuthenticationHeaders.USER_TOKEN) String userToken
    ) {

        final File file = fileDao.findByNameAndOwner(fileName, appId, userId);
        final boolean isOwnerAuthenticated = ownerId.equals(userId);
        final boolean isFileFound = file != null;

        if (!isOwnerAuthenticated || !isFileFound) { return StashResponse.forbidden(); }

        final java.nio.file.Path path = Paths.get(file.getFilePath() + file.getFileName());

        try {
            Files.delete(path);
            fileDao.delete(
                file.getFileId(),
                appId
            );

            return StashResponse.ok();
        } catch (IOException e) {
            return StashResponse.error(e.getMessage());
        }
    }

}
